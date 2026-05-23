import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() } }));
vi.mock("../../api/mockDb", () => ({ mockDb: { predictions: [], matches: [] } }));

import {
  getPredictionsByPool, getMyPredictions, upsertPrediction,
  editarPronostico, eliminarPronostico,
} from "../../api/predictionsApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockPut = vi.mocked(http.put);
const mockDelete = vi.mocked(http.delete);

const backendPronostico = (overrides: Record<string, any> = {}) => ({
  id: 1, resultadoPronosticado: "LOCAL",
  golesLocalPronosticados: 2, golesVisitantePronosticados: 1,
  puntosObtenidos: 3, usuarioId: 1, apuestaId: 1, partidoId: 1,
  ...overrides,
});

beforeEach(() => { vi.clearAllMocks(); });

describe("getPredictionsByPool - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getPredictionsByPool("1");
    expect(mockGet).toHaveBeenCalledWith("/api/apuestas/participantes/1");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendPronostico()]);
    const result = await getPredictionsByPool("1");
    expect(result[0].id).toBe("1");
    expect(result[0].homeScore).toBe(2);
    expect(result[0].awayScore).toBe(1);
    expect(result[0].points).toBe(3);
  });
  it("retorna array vacío si no hay predicciones", async () => {
    mockGet.mockResolvedValueOnce([]);
    expect(await getPredictionsByPool("1")).toHaveLength(0);
  });
  it("mapea múltiples predicciones", async () => {
    mockGet.mockResolvedValueOnce([backendPronostico({ id: 1 }), backendPronostico({ id: 2 })]);
    expect(await getPredictionsByPool("1")).toHaveLength(2);
  });
});

describe("getMyPredictions - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getMyPredictions("1", "u1");
    expect(mockGet).toHaveBeenCalledWith("/api/apuestas/mis-pronosticos/1/u1");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendPronostico()]);
    const result = await getMyPredictions("1", "u1");
    expect(result[0].homeScore).toBe(2);
    expect(result[0].locked).toBe(false);
  });
});

describe("upsertPrediction - modo real", () => {
  it("llama al endpoint correcto con resultado LOCAL", async () => {
    mockPost.mockResolvedValueOnce(backendPronostico());
    await upsertPrediction("1", "1", "1", 2, 1);
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/pronostico", { usuarioId: 1, apuestaId: 1, partidoId: 1, resultadoPronosticado: "LOCAL", golesLocalPronosticados: 2, golesVisitantePronosticados: 1 });
  });
  it("calcula resultado VISITANTE correctamente", async () => {
    mockPost.mockResolvedValueOnce(backendPronostico());
    await upsertPrediction("1", "1", "1", 0, 2);
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/pronostico", expect.objectContaining({ resultadoPronosticado: "VISITANTE" }));
  });
  it("calcula resultado EMPATE correctamente", async () => {
    mockPost.mockResolvedValueOnce(backendPronostico());
    await upsertPrediction("1", "1", "1", 1, 1);
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/pronostico", expect.objectContaining({ resultadoPronosticado: "EMPATE" }));
  });
  it("mapea la respuesta correctamente", async () => {
    mockPost.mockResolvedValueOnce(backendPronostico());
    const result = await upsertPrediction("1", "1", "1", 2, 1);
    expect(result.homeScore).toBe(2);
    expect(result.locked).toBe(false);
  });
});

describe("editarPronostico - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPut.mockResolvedValueOnce(backendPronostico({ golesLocalPronosticados: 3, golesVisitantePronosticados: 0 }));
    await editarPronostico("1", 3, 0);
    expect(mockPut).toHaveBeenCalledWith("/api/apuestas/pronostico/1", expect.objectContaining({ golesLocalPronosticados: 3, resultadoPronosticado: "LOCAL" }));
  });
  it("mapea la respuesta correctamente", async () => {
    mockPut.mockResolvedValueOnce(backendPronostico({ golesLocalPronosticados: 3, golesVisitantePronosticados: 2 }));
    const result = await editarPronostico("1", 3, 2);
    expect(result.homeScore).toBe(3);
    expect(result.awayScore).toBe(2);
  });
  it("calcula EMPATE correctamente al editar", async () => {
    mockPut.mockResolvedValueOnce(backendPronostico());
    await editarPronostico("1", 1, 1);
    expect(mockPut).toHaveBeenCalledWith("/api/apuestas/pronostico/1", expect.objectContaining({ resultadoPronosticado: "EMPATE" }));
  });
});

describe("eliminarPronostico - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockDelete.mockResolvedValueOnce(undefined);
    await eliminarPronostico("1");
    expect(mockDelete).toHaveBeenCalledWith("/api/apuestas/pronostico/1");
  });
  it("no lanza error al eliminar", async () => {
    mockDelete.mockResolvedValueOnce(undefined);
    await expect(eliminarPronostico("1")).resolves.not.toThrow();
  });
});