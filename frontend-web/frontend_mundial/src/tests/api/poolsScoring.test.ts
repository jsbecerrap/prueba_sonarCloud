import { describe, it, expect, beforeEach, vi } from "vitest";
import { recalcPoolPoints } from "../../api/poolsScoring";
import { mockDb } from "../../api/mockDb";
import { poolsMock } from "../../api/poolsMockDb";
import type { Prediction } from "../../types/prediction";

vi.mock("../../api/scoring", () => ({ scorePrediction: vi.fn() }));
import { scorePrediction } from "../../api/scoring";
const mockScorePrediction = vi.mocked(scorePrediction);

beforeEach(() => {
  mockDb.predictions = [];
  poolsMock[0].members = [
  { user: { id: "u1", name: "Sara", stickers: [], repeated: [] }, points: 0 },
  { user: { id: "u2", name: "Juan", stickers: [], repeated: [] }, points: 0 },
] as any;
  mockScorePrediction.mockReset();
});

const basePred = (overrides: Partial<Prediction> = {}): Prediction => ({
  id: "pr1", poolId: "p1", userId: "u1", matchId: "m1",
  homeScore: 1, awayScore: 0,
  createdAt: new Date().toISOString(), locked: false, ...overrides,
});

describe("recalcPoolPoints", () => {
  it("no hace nada si la pool no existe", async () => {
    await recalcPoolPoints("pool_inexistente");
    expect(mockScorePrediction).not.toHaveBeenCalled();
  });
  it("marca predicción como PENDING si el partido no terminó", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "SCHEDULED", score: undefined };
    const pred = basePred({ matchId: mockDb.matches[0].id });
    mockDb.predictions.push(pred);
    await recalcPoolPoints("p1");
    expect(pred.result).toBe("PENDING");
    expect(pred.points).toBe(0);
    expect(mockScorePrediction).not.toHaveBeenCalled();
  });
  it("marca predicción como PENDING si el partido está LIVE", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "LIVE", score: undefined };
    const pred = basePred({ matchId: mockDb.matches[0].id });
    mockDb.predictions.push(pred);
    await recalcPoolPoints("p1");
    expect(pred.result).toBe("PENDING");
    expect(pred.points).toBe(0);
  });
  it("asigna WIN y 3 puntos cuando scorePrediction retorna 3", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "FINISHED", score: { home: 2, away: 1 } };
    const pred = basePred({ matchId: mockDb.matches[0].id });
    mockDb.predictions.push(pred);
    mockScorePrediction.mockReturnValue(3);
    await recalcPoolPoints("p1");
    expect(pred.points).toBe(3);
    expect(pred.result).toBe("WIN");
  });
  it("asigna LOSS y 0 puntos cuando scorePrediction retorna 0", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "FINISHED", score: { home: 2, away: 1 } };
    const pred = basePred({ matchId: mockDb.matches[0].id });
    mockDb.predictions.push(pred);
    mockScorePrediction.mockReturnValue(0);
    await recalcPoolPoints("p1");
    expect(pred.points).toBe(0);
    expect(pred.result).toBe("LOSS");
  });
  it("asigna WIN cuando scorePrediction retorna 1 y ganó local", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "FINISHED", score: { home: 2, away: 0 } };
    const pred = basePred({ matchId: mockDb.matches[0].id });
    mockDb.predictions.push(pred);
    mockScorePrediction.mockReturnValue(1);
    await recalcPoolPoints("p1");
    expect(pred.points).toBe(1);
    expect(pred.result).toBe("WIN");
  });
  it("asigna DRAW cuando scorePrediction retorna 1 y fue empate", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "FINISHED", score: { home: 1, away: 1 } };
    const pred = basePred({ matchId: mockDb.matches[0].id });
    mockDb.predictions.push(pred);
    mockScorePrediction.mockReturnValue(1);
    await recalcPoolPoints("p1");
    expect(pred.result).toBe("DRAW");
  });
  it("acumula puntos para múltiples predicciones del mismo usuario", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "FINISHED", score: { home: 2, away: 1 } };
    mockDb.matches[1] = { ...mockDb.matches[1], status: "FINISHED", score: { home: 0, away: 0 } };
    const pred1 = basePred({ id: "pr1", matchId: mockDb.matches[0].id });
    const pred2 = basePred({ id: "pr2", matchId: mockDb.matches[1].id });
    mockDb.predictions.push(pred1, pred2);
    mockScorePrediction.mockReturnValueOnce(3).mockReturnValueOnce(1);
    await recalcPoolPoints("p1");
    const member = poolsMock[0].members.find((m: any) => m.user.id === "u1");
   expect((member as any)?.points).toBe(4);
  });
  it("ignora predicciones de partidos fuera de la pool", async () => {
    const pred = basePred({ matchId: "partido_fuera_de_pool" });
    mockDb.predictions.push(pred);
    await recalcPoolPoints("p1");
    expect(mockScorePrediction).not.toHaveBeenCalled();
  });
  it("asigna 0 puntos a miembro sin predicciones", async () => {
    await recalcPoolPoints("p1");
    const member = poolsMock[0].members.find((m: any) => m.user.id === "u2");
    expect((member as any)?.points).toBe(0);
  });
});