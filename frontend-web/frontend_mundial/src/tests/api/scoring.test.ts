import { describe, it, expect } from "vitest";
import { scorePrediction } from "../../api/scoring";
import type { Prediction } from "../../types/prediction";
import type { Match } from "../../types/match";

const basePred = (): Prediction => ({
  id: "pr1", poolId: "p1", userId: "u1", matchId: "m1",
  homeScore: 0, awayScore: 0,
  createdAt: new Date().toISOString(), locked: false,
});

const baseMatch = (score?: { home: number; away: number }): Match => ({
  id: "m1",
  home: { id: "t1", name: "Argentina", code: "ARG" },
  away: { id: "t2", name: "Brasil", code: "BRA" },
  stadium: "Azteca", city: "Ciudad de México",
  startTimeISO: "2026-06-10T20:00:00Z", status: "FINISHED", score,
});

describe("scorePrediction", () => {
  it("retorna 0 si no hay score", () => expect(scorePrediction(basePred(), baseMatch())).toBe(0));
  it("retorna 3 marcador exacto victoria", () => expect(scorePrediction({ ...basePred(), homeScore: 2, awayScore: 1 }, baseMatch({ home: 2, away: 1 }))).toBe(3));
  it("retorna 3 marcador exacto empate", () => expect(scorePrediction({ ...basePred(), homeScore: 0, awayScore: 0 }, baseMatch({ home: 0, away: 0 }))).toBe(3));
  it("retorna 1 acertó local sin marcador exacto", () => expect(scorePrediction({ ...basePred(), homeScore: 1, awayScore: 0 }, baseMatch({ home: 3, away: 1 }))).toBe(1));
  it("retorna 1 acertó visitante sin marcador exacto", () => expect(scorePrediction({ ...basePred(), homeScore: 0, awayScore: 2 }, baseMatch({ home: 1, away: 3 }))).toBe(1));
  it("retorna 1 acertó empate sin marcador exacto", () => expect(scorePrediction({ ...basePred(), homeScore: 1, awayScore: 1 }, baseMatch({ home: 2, away: 2 }))).toBe(1));
  it("retorna 0 predijo local ganó visitante", () => expect(scorePrediction({ ...basePred(), homeScore: 2, awayScore: 0 }, baseMatch({ home: 0, away: 1 }))).toBe(0));
  it("retorna 0 predijo empate ganó local", () => expect(scorePrediction({ ...basePred(), homeScore: 1, awayScore: 1 }, baseMatch({ home: 2, away: 0 }))).toBe(0));
  it("retorna 0 predijo visitante fue empate", () => expect(scorePrediction({ ...basePred(), homeScore: 0, awayScore: 1 }, baseMatch({ home: 1, away: 1 }))).toBe(0));
});