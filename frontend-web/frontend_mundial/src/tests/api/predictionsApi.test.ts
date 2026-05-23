import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() } }));

import {
  getPredictionsByPool, getMyPredictions, upsertPrediction,
  editarPronostico, eliminarPronostico, CLOSE_MINUTES_BEFORE,
} from "../../api/predictionsApi";
import { mockDb, matchesMock } from "../../api/mockDb";
import type { Match } from "../../types/match";
import type { Prediction } from "../../types/prediction";

const futureMatch = (): Match => ({
  id: "m_future", home: { id: "t1", name: "Colombia", code: "COL" },
  away: { id: "t2", name: "Ecuador", code: "ECU" },
  stadium: "El Campin", city: "Bogotá",
  startTimeISO: new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString(),
  status: "SCHEDULED",
});

const pastMatch = (): Match => ({
  id: "m_past", home: { id: "t1", name: "Colombia", code: "COL" },
  away: { id: "t2", name: "Ecuador", code: "ECU" },
  stadium: "El Campin", city: "Bogotá",
  startTimeISO: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
  status: "SCHEDULED",
});

const basePred = (overrides: Partial<Prediction> = {}): Prediction => ({
  id: "pr1", poolId: "p1", userId: "u1", matchId: "m_future",
  homeScore: 1, awayScore: 0,
  createdAt: new Date().toISOString(), locked: false, ...overrides,
});

beforeEach(() => {
  mockDb.matches = [...matchesMock];
  mockDb.predictions = [];
});

describe("getPredictionsByPool", () => {
  it("retorna vacío si no hay predicciones para la pool", async () => {
    expect(await getPredictionsByPool("p1")).toHaveLength(0);
  });
  it("retorna solo predicciones de la pool indicada", async () => {
    mockDb.predictions.push(basePred({ poolId: "p1" }), basePred({ id: "pr2", poolId: "p2" }));
    const result = await getPredictionsByPool("p1");
    expect(result).toHaveLength(1);
    expect(result[0].poolId).toBe("p1");
  });
});

describe("getMyPredictions", () => {
  it("retorna vacío si el usuario no tiene predicciones", async () => {
    expect(await getMyPredictions("p1", "u1")).toHaveLength(0);
  });
  it("retorna solo las predicciones del usuario en esa pool", async () => {
    mockDb.predictions.push(basePred({ id: "pr1", poolId: "p1", userId: "u1" }), basePred({ id: "pr2", poolId: "p1", userId: "u2" }));
    const result = await getMyPredictions("p1", "u1");
    expect(result).toHaveLength(1);
    expect(result[0].userId).toBe("u1");
  });
});

describe("upsertPrediction", () => {
  beforeEach(() => { mockDb.matches.push(futureMatch()); });

  it("crea una predicción nueva correctamente", async () => {
    const result = await upsertPrediction("p1", "u1", "m_future", 2, 1);
    expect(result.homeScore).toBe(2);
    expect(result.awayScore).toBe(1);
    expect(result.poolId).toBe("p1");
  });
  it("agrega la predicción al mockDb", async () => {
    await upsertPrediction("p1", "u1", "m_future", 1, 0);
    expect(mockDb.predictions).toHaveLength(1);
  });
  it("actualiza una predicción existente", async () => {
    await upsertPrediction("p1", "u1", "m_future", 1, 0);
    const result = await upsertPrediction("p1", "u1", "m_future", 3, 2);
    expect(result.homeScore).toBe(3);
    expect(mockDb.predictions).toHaveLength(1);
  });
  it("clampea scores negativos a 0", async () => {
    const result = await upsertPrediction("p1", "u1", "m_future", -3, -1);
    expect(result.homeScore).toBe(0);
    expect(result.awayScore).toBe(0);
  });
  it("clampea scores mayores a 20 a 20", async () => {
    const result = await upsertPrediction("p1", "u1", "m_future", 99, 50);
    expect(result.homeScore).toBe(20);
    expect(result.awayScore).toBe(20);
  });
  it("trunca scores decimales", async () => {
    const result = await upsertPrediction("p1", "u1", "m_future", 2.9, 1.7);
    expect(result.homeScore).toBe(2);
    expect(result.awayScore).toBe(1);
  });
  it("lanza error si el partido no existe", async () => {
    await expect(upsertPrediction("p1", "u1", "no_existe", 1, 0)).rejects.toThrow("Match not found");
  });
  it("lanza error si el partido ya pasó el tiempo de cierre", async () => {
    mockDb.matches.push(pastMatch());
    await expect(upsertPrediction("p1", "u1", "m_past", 1, 0)).rejects.toThrow("Pronóstico cerrado");
  });
  it("lanza error si el partido está LIVE", async () => {
    mockDb.matches.push({ ...futureMatch(), id: "m_live", status: "LIVE" });
    await expect(upsertPrediction("p1", "u1", "m_live", 1, 0)).rejects.toThrow("Pronóstico cerrado");
  });
  it("lanza error si el partido está FINISHED", async () => {
    mockDb.matches.push({ ...futureMatch(), id: "m_fin", status: "FINISHED" });
    await expect(upsertPrediction("p1", "u1", "m_fin", 1, 0)).rejects.toThrow("Pronóstico cerrado");
  });
  it("lanza error si la predicción existente está bloqueada", async () => {
    mockDb.predictions.push(basePred({ matchId: "m_future", locked: true }));
    await expect(upsertPrediction("p1", "u1", "m_future", 2, 1)).rejects.toThrow("Pronóstico bloqueado");
  });
});

describe("editarPronostico", () => {
  it("edita el score correctamente", async () => {
    mockDb.predictions.push(basePred());
    const result = await editarPronostico("pr1", 3, 2);
    expect(result.homeScore).toBe(3);
    expect(result.awayScore).toBe(2);
  });
  it("clampea scores al editar", async () => {
    mockDb.predictions.push(basePred());
    const result = await editarPronostico("pr1", -1, 25);
    expect(result.homeScore).toBe(0);
    expect(result.awayScore).toBe(20);
  });
  it("lanza error si el pronóstico no existe", async () => {
    await expect(editarPronostico("no_existe", 1, 0)).rejects.toThrow("Pronóstico no encontrado");
  });
  it("lanza error si el pronóstico está bloqueado", async () => {
    mockDb.predictions.push(basePred({ locked: true }));
    await expect(editarPronostico("pr1", 1, 0)).rejects.toThrow("Pronóstico bloqueado");
  });
  it("actualiza el updatedAt", async () => {
    mockDb.predictions.push(basePred());
    expect((await editarPronostico("pr1", 2, 1)).updatedAt).toBeTruthy();
  });
});

describe("eliminarPronostico", () => {
  it("elimina el pronóstico del mockDb", async () => {
    mockDb.predictions.push(basePred());
    await eliminarPronostico("pr1");
    expect(mockDb.predictions).toHaveLength(0);
  });
  it("no lanza error si el pronóstico no existe", async () => {
    await expect(eliminarPronostico("no_existe")).resolves.not.toThrow();
  });
  it("solo elimina el pronóstico indicado", async () => {
    mockDb.predictions.push(basePred({ id: "pr1" }), basePred({ id: "pr2" }));
    await eliminarPronostico("pr1");
    expect(mockDb.predictions).toHaveLength(1);
    expect(mockDb.predictions[0].id).toBe("pr2");
  });
});

describe("CLOSE_MINUTES_BEFORE", () => {
  it("es 10 minutos", () => { expect(CLOSE_MINUTES_BEFORE).toBe(10); });
});