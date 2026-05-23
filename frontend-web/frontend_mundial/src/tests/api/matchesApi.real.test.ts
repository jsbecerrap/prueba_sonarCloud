import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn() } }));
vi.mock("../../api/mockDb", () => ({ mockDb: { matches: [] } }));

import { getMatches, getMatchesByDate, getLiveMatches } from "../../api/matchesApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);

const backendMatch = (id: number, statusShort: string, cityNull = false) => ({
  fixture: {
    id,
    date: "2026-06-10T20:00:00Z",
    status: { short: statusShort },
    venue: { name: "Estadio Azteca", city: cityNull ? null : "Ciudad de México" },
  },
  teams: {
    home: { id: 1, name: "Argentina", logo: "arg.png" },
    away: { id: 2, name: "Brasil", logo: "bra.png" },
  },
  goals: { home: 1, away: 0 },
});

beforeEach(() => { vi.clearAllMocks(); });

describe("getMatches - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getMatches();
    expect(mockGet).toHaveBeenCalledWith("/api/partidos");
  });
  it("mapea correctamente la respuesta del backend", async () => {
    mockGet.mockResolvedValueOnce([backendMatch(99, "NS")]);
    const result = await getMatches();
    expect(result[0].id).toBe("99");
    expect(result[0].home.name).toBe("Argentina");
    expect(result[0].status).toBe("SCHEDULED");
  });
  it("mapea goles null a 0", async () => {
    mockGet.mockResolvedValueOnce([{
      fixture: { id: 1, date: "2026-06-10T20:00:00Z", status: { short: "NS" }, venue: { name: "Azteca", city: "CDMX" } },
      teams: { home: { id: 1, name: "Colombia", logo: "" }, away: { id: 2, name: "Ecuador", logo: "" } },
      goals: { home: null, away: null },
    }]);
    const result = await getMatches();
    expect(result[0].score?.home).toBe(0);
    expect(result[0].score?.away).toBe(0);
  });
  it("usa estadioCiudad si city es null", async () => {
    mockGet.mockResolvedValueOnce([backendMatch(1, "NS", true)]);
    const result = await getMatches();
    expect(result[0].city).toBe("Ciudad de México");
  });
  it("usa Por confirmar si city es null y estadio no está en el mapa", async () => {
    mockGet.mockResolvedValueOnce([{
      fixture: { id: 1, date: "2026-06-10T20:00:00Z", status: { short: "NS" }, venue: { name: "Estadio Desconocido", city: null } },
      teams: { home: { id: 1, name: "A", logo: "" }, away: { id: 2, name: "B", logo: "" } },
      goals: { home: 0, away: 0 },
    }]);
    const result = await getMatches();
    expect(result[0].city).toBe("Por confirmar");
  });
});

describe("mapStatus - todos los estados", () => {
  const statuses = [
    { short: "1H", expected: "LIVE" },
    { short: "2H", expected: "LIVE" },
    { short: "HT", expected: "LIVE" },
    { short: "ET", expected: "LIVE" },
    { short: "BT", expected: "LIVE" },
    { short: "P", expected: "LIVE" },
    { short: "LIVE", expected: "LIVE" },
    { short: "FT", expected: "FINISHED" },
    { short: "AET", expected: "FINISHED" },
    { short: "PEN", expected: "FINISHED" },
    { short: "NS", expected: "SCHEDULED" },
    { short: "TBD", expected: "PENDING_DATA" },
  ];

  statuses.forEach(({ short, expected }) => {
    it(`mapea ${short} a ${expected}`, async () => {
      mockGet.mockResolvedValueOnce([backendMatch(1, short)]);
      const result = await getMatches();
      expect(result[0].status).toBe(expected);
    });
  });
});

describe("getMatchesByDate - modo real", () => {
  it("llama al endpoint con la fecha correcta", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getMatchesByDate("2026-06-10");
    expect(mockGet).toHaveBeenCalledWith("/api/partidos/fecha/2026-06-10");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendMatch(5, "FT")]);
    const result = await getMatchesByDate("2026-06-10");
    expect(result[0].status).toBe("FINISHED");
  });
});

describe("getLiveMatches - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getLiveMatches();
    expect(mockGet).toHaveBeenCalledWith("/api/partidos/envivo");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendMatch(3, "1H")]);
    const result = await getLiveMatches();
    expect(result[0].status).toBe("LIVE");
  });
  it("genera el code del equipo con las primeras 3 letras", async () => {
    mockGet.mockResolvedValueOnce([backendMatch(1, "1H")]);
    const result = await getLiveMatches();
    expect(result[0].home.code).toBe("ARG");
    expect(result[0].away.code).toBe("BRA");
  });
});