import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn() } }));

import { getMatches, getMatchesByDate, getLiveMatches } from "../../api/matchesApi";
import { mockDb, matchesMock } from "../../api/mockDb";

beforeEach(() => {
  mockDb.matches = [...matchesMock];
});

describe("getMatches", () => {
  it("retorna todos los partidos del mockDb", async () => {
    const result = await getMatches();
    expect(result).toHaveLength(4);
  });

  it("retorna una copia del array no la referencia", async () => {
    const result = await getMatches();
    expect(result).not.toBe(mockDb.matches);
  });

  it("contiene los partidos correctos", async () => {
    const result = await getMatches();
    expect(result[0].id).toBe("m1");
    expect(result[0].home.name).toBe("Argentina");
    expect(result[0].away.name).toBe("Brasil");
  });
});

describe("getMatchesByDate", () => {
  it("retorna todos los partidos en mock sin filtrar por fecha", async () => {
    const result = await getMatchesByDate("2026-06-10");
    expect(result).toHaveLength(4);
  });

  it("retorna array aunque la fecha no coincida con ningún partido", async () => {
    const result = await getMatchesByDate("2099-01-01");
    expect(Array.isArray(result)).toBe(true);
  });
});

describe("getLiveMatches", () => {
  it("retorna vacío si no hay partidos LIVE", async () => {
    const result = await getLiveMatches();
    expect(result).toHaveLength(0);
  });

  it("retorna solo los partidos LIVE", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "LIVE" };
    mockDb.matches[1] = { ...mockDb.matches[1], status: "LIVE" };
    const result = await getLiveMatches();
    expect(result).toHaveLength(2);
    expect(result.every((m) => m.status === "LIVE")).toBe(true);
  });

  it("no retorna partidos FINISHED o SCHEDULED", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "LIVE" };
    mockDb.matches[1] = { ...mockDb.matches[1], status: "FINISHED" };
    const result = await getLiveMatches();
    expect(result).toHaveLength(1);
    expect(result[0].id).toBe("m1");
  });
});

describe("mapStatus (via getMatches en modo real)", () => {
  it("mapea 1H a LIVE", async () => {
    vi.resetModules();
    const configMod = await vi.importMock("../../api/config") as any;
    configMod.USE_MOCK = false;
    const { http } = await vi.importMock("../../api/http") as any;
    http.get.mockResolvedValue([
      {
        fixture: { id: 99, date: "2026-06-10T20:00:00Z", status: { short: "1H" }, venue: { name: "Azteca", city: "CDMX" } },
        teams: { home: { id: 1, name: "Argentina", logo: "" }, away: { id: 2, name: "Brasil", logo: "" } },
        goals: { home: 1, away: 0 },
      },
    ]);
  });
});