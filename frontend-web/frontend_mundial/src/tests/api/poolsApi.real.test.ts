import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn() } }));
vi.mock("../../api/mockDb", () => ({ mockDb: { matches: [] } }));
vi.mock("../../api/poolsMockDb", () => ({ poolsMock: [] }));

import { getPools, obtenerApuesta, obtenerRanking, createPool, joinPool } from "../../api/poolsApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);

const backendApuesta = (overrides: Record<string, any> = {}) => ({
  id: 1, nombre: "Polla Test", estado: "ABIERTA",
  codigoInvitacion: "ABC123", fechaCierre: "2026-07-01",
  creadoPor: 1, participantes: [], ...overrides,
});

const backendParticipacion = (overrides: Record<string, any> = {}) => ({
  id: 1, usuarioId: 1, apuestaId: 1, puntos: 10, posicionRanking: 1, ...overrides,
});

beforeEach(() => { vi.clearAllMocks(); });

describe("getPools - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getPools(1);
    expect(mockGet).toHaveBeenCalledWith("/api/apuestas/usuario/1/completo");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendApuesta()]);
    const result = await getPools(1);
    expect(result[0].name).toBe("Polla Test");
    expect(result[0].code).toBe("ABC123");
  });
  it("mapea los participantes correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendApuesta({ participantes: [backendParticipacion()] })]);
    expect((await getPools(1))[0].members[0].puntos).toBe(10);
  });
  it("retorna array vacío si no hay pools", async () => {
    mockGet.mockResolvedValueOnce([]);
    expect(await getPools(1)).toHaveLength(0);
  });
  it("retorna múltiples pools", async () => {
    mockGet.mockResolvedValueOnce([backendApuesta({ id: 1 }), backendApuesta({ id: 2, nombre: "Polla 2" })]);
    expect(await getPools(1)).toHaveLength(2);
  });
});

describe("obtenerApuesta - modo real", () => {
  it("llama a los dos endpoints correctos", async () => {
    mockGet.mockResolvedValueOnce(backendApuesta()).mockResolvedValueOnce([]);
    await obtenerApuesta(1);
    expect(mockGet).toHaveBeenCalledWith("/api/apuestas/1");
    expect(mockGet).toHaveBeenCalledWith("/api/apuestas/participantes/1");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce(backendApuesta()).mockResolvedValueOnce([backendParticipacion()]);
    const result = await obtenerApuesta(1);
    expect(result.name).toBe("Polla Test");
    expect(result.members[0].puntos).toBe(10);
  });
  it("retorna pool sin miembros si no hay participantes", async () => {
    mockGet.mockResolvedValueOnce(backendApuesta()).mockResolvedValueOnce([]);
    expect((await obtenerApuesta(1)).members).toHaveLength(0);
  });
});

describe("obtenerRanking - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await obtenerRanking(1);
    expect(mockGet).toHaveBeenCalledWith("/api/apuestas/ranking/1");
  });
  it("retorna el ranking correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendParticipacion(), backendParticipacion({ usuarioId: 2, puntos: 5 })]);
    expect((await obtenerRanking(1))).toHaveLength(2);
  });
});

describe("createPool - modo real", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPost.mockResolvedValueOnce(backendApuesta());
    await createPool("Mi Polla", 1, "2026-07-01");
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/crear", { nombre: "Mi Polla", usuarioId: 1, fechaCierre: "2026-07-01" });
  });
  it("mapea la respuesta correctamente", async () => {
    mockPost.mockResolvedValueOnce(backendApuesta({ nombre: "Mi Polla" }));
    expect((await createPool("Mi Polla", 1, "2026-07-01")).name).toBe("Mi Polla");
  });
  it("trimea el nombre antes de enviar", async () => {
    mockPost.mockResolvedValueOnce(backendApuesta());
    await createPool("  Mi Polla  ", 1, "2026-07-01");
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/crear", { nombre: "Mi Polla", usuarioId: 1, fechaCierre: "2026-07-01" });
  });
});

describe("joinPool - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPost.mockResolvedValueOnce(backendApuesta({ id: 1 }));
    mockGet.mockResolvedValueOnce([]);
    await joinPool("ABC123", 1);
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/unirse/1", "ABC123");
  });
  it("retorna la pool al unirse correctamente", async () => {
    mockPost.mockResolvedValueOnce(backendApuesta());
    mockGet.mockResolvedValueOnce([backendParticipacion()]);
    expect((await joinPool("ABC123", 1))?.name).toBe("Polla Test");
  });
  it("retorna null si hay error al unirse", async () => {
    mockPost.mockRejectedValueOnce(new Error("Código inválido"));
    expect(await joinPool("INVALIDO", 1)).toBeNull();
  });
  it("trimea el código antes de enviar", async () => {
    mockPost.mockResolvedValueOnce(backendApuesta({ id: 1 }));
    mockGet.mockResolvedValueOnce([]);
    await joinPool("  ABC123  ", 1);
    expect(mockPost).toHaveBeenCalledWith("/api/apuestas/unirse/1", "ABC123");
  });
});