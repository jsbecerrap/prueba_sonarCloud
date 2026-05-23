import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() } }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/poolsApi", () => ({ recalcPoolPoints: vi.fn() }));
vi.mock("../../api/mockDb", () => ({ mockDb: { matches: [], predictions: [] } }));
vi.mock("../../api/poolsMockDb", () => ({ poolsMock: [] }));

import {
  adminGetUsuarios, adminRegistrarUsuario, adminEliminarUsuario,
  adminGetPartidos, adminGetPartidosPorFecha, adminGetPartidosEnVivo,
  adminSetMatchStatus, adminPublishResult,
  adminGetCategorias, adminCrearCategoria, adminActualizarCategoria,
  adminDesactivarCategoria, adminReactivarCategoria,
  adminGetProductos, adminCrearProducto, adminActualizarProducto,
  adminEliminarProducto, adminReactivarProducto, adminActivarProductosLote,
  adminSincronizarPartidos, adminGetCapacidadPartidos,
  adminGetApuestas, adminCerrarApuesta, adminEliminarApuesta, adminForzarPuntos,
  adminEnviarNotificacion, adminEnviarMasiva, adminNotificarPorPartido,
  adminGetProductosDeCategoria,
} from "../../api/adminApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockPut = vi.mocked(http.put);
const mockPatch = vi.mocked(http.patch);
const mockDelete = vi.mocked(http.delete);

const backendMatch = () => ({
  fixture: { id: 1, date: "2026-06-10T20:00:00Z", status: { short: "NS" }, venue: { name: "Azteca", city: "CDMX" } },
  teams: { home: { id: 1, name: "Argentina", logo: "" }, away: { id: 2, name: "Brasil", logo: "" } },
  goals: { home: 0, away: 0 },
});

beforeEach(() => { vi.clearAllMocks(); });

describe("adminGetUsuarios - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetUsuarios(); expect(mockGet).toHaveBeenCalledWith("/api/usuarios/listar"); });
  it("retorna la lista de usuarios", async () => { mockGet.mockResolvedValueOnce([{ id: 1, nombre: "Ana" }]); expect((await adminGetUsuarios())[0].nombre).toBe("Ana"); });
});

describe("adminRegistrarUsuario - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPost.mockResolvedValueOnce({ id: 1 });
    await adminRegistrarUsuario({ correoUsuario: "j@b.com", contrasena: "p", nombre: "Juan", apellido: "G", rol: "R" });
    expect(mockPost).toHaveBeenCalledWith("/api/usuarios/admin/registrar", expect.objectContaining({ nombre: "Juan" }));
  });
});

describe("adminEliminarUsuario - modo real", () => {
  it("llama al endpoint correcto", async () => { mockDelete.mockResolvedValueOnce(undefined); await adminEliminarUsuario(1); expect(mockDelete).toHaveBeenCalledWith("/api/usuarios/1"); });
});

describe("adminGetPartidos - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetPartidos(); expect(mockGet).toHaveBeenCalledWith("/api/partidos"); });
  it("mapea la respuesta correctamente", async () => { mockGet.mockResolvedValueOnce([backendMatch()]); expect((await adminGetPartidos())[0].home.name).toBe("Argentina"); });
});

describe("adminGetPartidosPorFecha - modo real", () => {
  it("llama al endpoint correcto con la fecha", async () => { mockGet.mockResolvedValueOnce([]); await adminGetPartidosPorFecha("2026-06-10"); expect(mockGet).toHaveBeenCalledWith("/api/partidos/fecha/2026-06-10"); });
});

describe("adminGetPartidosEnVivo - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetPartidosEnVivo(); expect(mockGet).toHaveBeenCalledWith("/api/partidos/envivo"); });
});

describe("adminSetMatchStatus - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPatch.mockResolvedValueOnce({ id: "m1", home: { name: "A" }, away: { name: "B" }, status: "LIVE" });
    await adminSetMatchStatus("m1", "LIVE");
    expect(mockPatch).toHaveBeenCalledWith("/admin/matches/m1/status", { status: "LIVE" });
  });
  it("retorna el partido actualizado", async () => {
    mockPatch.mockResolvedValueOnce({ id: "m1", home: { name: "A" }, away: { name: "B" }, status: "LIVE" });
    expect((await adminSetMatchStatus("m1", "LIVE")).status).toBe("LIVE");
  });
});

describe("adminPublishResult - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPost.mockResolvedValueOnce({ id: "m1", home: { name: "A" }, away: { name: "B" }, score: { home: 2, away: 1 } });
    await adminPublishResult("m1", 2, 1);
    expect(mockPost).toHaveBeenCalledWith("/admin/matches/m1/result", { homeScore: 2, awayScore: 1 });
  });
});

describe("adminGetCategorias - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetCategorias(); expect(mockGet).toHaveBeenCalledWith("/api/categorias/todas"); });
});

describe("adminCrearCategoria - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPost.mockResolvedValueOnce({ id: 1, nombre: "Ropa", descripcion: "", activo: true });
    await adminCrearCategoria({ nombre: "Ropa" });
    expect(mockPost).toHaveBeenCalledWith("/api/categorias", { nombre: "Ropa" });
  });
});

describe("adminActualizarCategoria - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPut.mockResolvedValueOnce({ id: 1 });
    await adminActualizarCategoria(1, { nombre: "Ropa" });
    expect(mockPut).toHaveBeenCalledWith("/api/categorias/1", { nombre: "Ropa" });
  });
});

describe("adminDesactivarCategoria - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPatch.mockResolvedValueOnce({}); await adminDesactivarCategoria(1); expect(mockPatch).toHaveBeenCalledWith("/api/categorias/1/desactivar"); });
});

describe("adminReactivarCategoria - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPatch.mockResolvedValueOnce({}); await adminReactivarCategoria(1); expect(mockPatch).toHaveBeenCalledWith("/api/categorias/1/reactivar"); });
});

describe("adminGetProductos - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetProductos(); expect(mockGet).toHaveBeenCalledWith("/api/productos/admin/todos"); });
});

describe("adminCrearProducto - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPost.mockResolvedValueOnce({ id: 1 });
    await adminCrearProducto({ nombre: "Camiseta", precio: 50000, categoriaId: 1, variantes: [] });
    expect(mockPost).toHaveBeenCalledWith("/api/productos", expect.objectContaining({ nombre: "Camiseta" }));
  });
});

describe("adminActualizarProducto - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPut.mockResolvedValueOnce({ id: 1 }); await adminActualizarProducto(1, { precio: 60000 }); expect(mockPut).toHaveBeenCalledWith("/api/productos/1", { precio: 60000 }); });
});

describe("adminEliminarProducto - modo real", () => {
  it("llama al endpoint correcto", async () => { mockDelete.mockResolvedValueOnce(undefined); await adminEliminarProducto(1); expect(mockDelete).toHaveBeenCalledWith("/api/productos/1"); });
});

describe("adminReactivarProducto - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPatch.mockResolvedValueOnce(undefined); await adminReactivarProducto(1); expect(mockPatch).toHaveBeenCalledWith("/api/productos/1/reactivar"); });
});

describe("adminActivarProductosLote - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPatch.mockResolvedValueOnce(undefined); await adminActivarProductosLote([1, 2, 3]); expect(mockPatch).toHaveBeenCalledWith("/api/productos/activar-lote", { ids: [1, 2, 3] }); });
});

describe("adminSincronizarPartidos - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce(5); await adminSincronizarPartidos(2018, 2026, "2026-06-10"); expect(mockGet).toHaveBeenCalledWith("/api/partidos/sincronizar/2018/2026/2026-06-10"); });
  it("retorna el número correcto", async () => { mockGet.mockResolvedValueOnce(5); expect(await adminSincronizarPartidos(2018, 2026, "2026-06-10")).toBe(5); });
});

describe("adminGetCapacidadPartidos - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetCapacidadPartidos(); expect(mockGet).toHaveBeenCalledWith("/api/entradas/partidos"); });
});

describe("adminGetApuestas - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetApuestas(); expect(mockGet).toHaveBeenCalledWith("/api/apuestas/todas"); });
});

describe("adminCerrarApuesta - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPost.mockResolvedValueOnce(undefined); await adminCerrarApuesta(1); expect(mockPost).toHaveBeenCalledWith("/api/apuestas/cerrar/1", {}); });
});

describe("adminEliminarApuesta - modo real", () => {
  it("llama al endpoint correcto", async () => { mockDelete.mockResolvedValueOnce(undefined); await adminEliminarApuesta(1); expect(mockDelete).toHaveBeenCalledWith("/api/apuestas/1"); });
});

describe("adminForzarPuntos - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce(undefined); await adminForzarPuntos(1); expect(mockGet).toHaveBeenCalledWith("/api/apuestas/puntos/1"); });
});

describe("adminEnviarNotificacion - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPost.mockResolvedValueOnce(undefined); await adminEnviarNotificacion({ tipo: "INFO", titulo: "T", mensaje: "M", canal: "S" }); expect(mockPost).toHaveBeenCalledWith("/api/notificaciones/enviar", expect.objectContaining({ titulo: "T" })); });
});

describe("adminEnviarMasiva - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPost.mockResolvedValueOnce(undefined); await adminEnviarMasiva({ tipo: "INFO", titulo: "T", mensaje: "M", canal: "S" }); expect(mockPost).toHaveBeenCalledWith("/api/notificaciones/masiva", expect.objectContaining({ titulo: "T" })); });
});

describe("adminNotificarPorPartido - modo real", () => {
  it("llama al endpoint correcto", async () => { mockPost.mockResolvedValueOnce(undefined); await adminNotificarPorPartido(1, { tipo: "INFO", titulo: "T", mensaje: "M", canal: "S" }); expect(mockPost).toHaveBeenCalledWith("/api/notificaciones/partido/1", expect.objectContaining({ titulo: "T" })); });
});

describe("adminGetProductosDeCategoria - modo real", () => {
  it("llama al endpoint correcto", async () => { mockGet.mockResolvedValueOnce([]); await adminGetProductosDeCategoria(1); expect(mockGet).toHaveBeenCalledWith("/api/categorias/1/productos"); });
});