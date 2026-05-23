import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn(), delete: vi.fn() } }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/poolsApi", () => ({ recalcPoolPoints: vi.fn() }));

import {
  adminGetUsuarios, adminRegistrarUsuario, adminEliminarUsuario,
  adminGetMatches, adminGetPartidos, adminGetPartidosPorFecha,
  adminGetPartidosEnVivo, adminCreateMatch, adminSetMatchStatus,
  adminPublishResult, adminGetCategorias, adminCrearCategoria,
  adminActualizarCategoria, adminDesactivarCategoria, adminReactivarCategoria,
  adminGetProductos, adminCrearProducto, adminActualizarProducto,
  adminEliminarProducto, adminReactivarProducto, adminActivarProductosLote,
  adminSincronizarPartidos, adminGetCapacidadPartidos, adminGetApuestas,
  adminCerrarApuesta, adminEliminarApuesta, adminForzarPuntos,
  adminEnviarNotificacion, adminEnviarMasiva, adminNotificarPorPartido,
  adminGetProductosDeCategoria,
} from "../../api/adminApi";
import { mockDb, matchesMock } from "../../api/mockDb";
import { poolsMock } from "../../api/poolsMockDb";

beforeEach(() => {
  mockDb.matches = [...matchesMock];
  mockDb.predictions = [];
  poolsMock[0].members = [
  { user: { id: "u1", name: "Sara", stickers: [], repeated: [] }, points: 0 },
  { user: { id: "u2", name: "Juan", stickers: [], repeated: [] }, points: 0 },
] as any;
});

describe("adminGetUsuarios", () => {
  it("retorna array vacío en mock", async () => {
    expect(await adminGetUsuarios()).toEqual([]);
  });
});

describe("adminRegistrarUsuario", () => {
  it("retorna usuario con los datos enviados", async () => {
    const result = await adminRegistrarUsuario({ correoUsuario: "test@test.com", contrasena: "pass", nombre: "Test", apellido: "User", rol: "ROLE_USUARIO" });
    expect(result.nombre).toBe("Test");
    expect(result.correoUsuario).toBe("test@test.com");
  });

  it("retorna un id numérico", async () => {
    const result = await adminRegistrarUsuario({ correoUsuario: "a@b.com", contrasena: "p", nombre: "A", apellido: "B", rol: "ROLE_USUARIO" });
    expect(typeof result.id).toBe("number");
  });
});

describe("adminEliminarUsuario", () => {
  it("no lanza error en mock", async () => {
    await expect(adminEliminarUsuario(1)).resolves.not.toThrow();
  });
});

describe("adminGetMatches", () => {
  it("retorna los partidos del mockDb", async () => {
    expect(await adminGetMatches()).toHaveLength(4);
  });
});

describe("adminGetPartidos", () => {
  it("retorna los partidos del mockDb", async () => {
    expect(await adminGetPartidos()).toHaveLength(4);
  });
});

describe("adminGetPartidosPorFecha", () => {
  it("filtra partidos por fecha correctamente", async () => {
    const result = await adminGetPartidosPorFecha("2026-06-10");
    expect(result).toHaveLength(1);
    expect(result[0].id).toBe("m1");
  });

  it("retorna vacío si no hay partidos en esa fecha", async () => {
    expect(await adminGetPartidosPorFecha("2099-01-01")).toHaveLength(0);
  });
});

describe("adminGetPartidosEnVivo", () => {
  it("retorna vacío si no hay partidos LIVE", async () => {
    expect(await adminGetPartidosEnVivo()).toHaveLength(0);
  });

  it("retorna solo partidos LIVE", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], status: "LIVE" };
    const result = await adminGetPartidosEnVivo();
    expect(result).toHaveLength(1);
    expect(result[0].id).toBe("m1");
  });
});

describe("adminCreateMatch", () => {
  it("crea un partido y lo agrega al mockDb", async () => {
    const initialCount = mockDb.matches.length;
    const result = await adminCreateMatch({ home: { id: "t1", name: "Colombia", code: "COL" }, away: { id: "t2", name: "Ecuador", code: "ECU" }, city: "Bogotá", stadium: "El Campin", startTimeISO: "2026-07-01T20:00:00Z" });
    expect(mockDb.matches.length).toBe(initialCount + 1);
    expect(result.home.name).toBe("Colombia");
  });

  it("asigna status SCHEDULED por defecto", async () => {
    const result = await adminCreateMatch({ home: { id: "t1", name: "Peru", code: "PER" }, away: { id: "t2", name: "Chile", code: "CHI" }, city: "Lima", stadium: "Nacional", startTimeISO: "2026-07-01T20:00:00Z" });
    expect(result.status).toBe("SCHEDULED");
  });

  it("agrega el partido a todas las pools si assignToAllPools es true", async () => {
    const result = await adminCreateMatch({ home: { id: "t1", name: "Peru", code: "PER" }, away: { id: "t2", name: "Chile", code: "CHI" }, city: "Lima", stadium: "Nacional", startTimeISO: "2026-07-01T20:00:00Z", assignToAllPools: true });
  expect((poolsMock[0] as any).matchIds).toContain(result.id);
  });
});

describe("adminSetMatchStatus", () => {
  it("actualiza el status del partido", async () => {
    await adminSetMatchStatus("m1", "LIVE");
    expect(mockDb.matches.find((m) => m.id === "m1")?.status).toBe("LIVE");
  });

  it("lanza error si el partido no existe", async () => {
    await expect(adminSetMatchStatus("no_existe", "LIVE")).rejects.toThrow("Match not found");
  });

  it("sella predicciones cuando el partido pasa a LIVE", async () => {
    mockDb.predictions.push({ id: "pr1", poolId: "p1", userId: "u1", matchId: "m1", homeScore: 1, awayScore: 0, createdAt: new Date().toISOString(), locked: false });
    await adminSetMatchStatus("m1", "LIVE");
    expect(mockDb.predictions[0].locked).toBe(true);
  });

  it("sella predicciones cuando el partido pasa a FINISHED", async () => {
    mockDb.matches[0] = { ...mockDb.matches[0], score: { home: 1, away: 0 } };
    mockDb.predictions.push({ id: "pr1", poolId: "p1", userId: "u1", matchId: "m1", homeScore: 1, awayScore: 0, createdAt: new Date().toISOString(), locked: false });
    await adminSetMatchStatus("m1", "FINISHED");
    expect(mockDb.predictions[0].locked).toBe(true);
  });
});

describe("adminPublishResult", () => {
  it("publica resultado y cambia status a FINISHED", async () => {
    const result = await adminPublishResult("m1", 2, 1);
    expect(result.status).toBe("FINISHED");
    expect(result.score?.home).toBe(2);
    expect(result.score?.away).toBe(1);
  });

  it("lanza error si el partido no existe", async () => {
    await expect(adminPublishResult("no_existe", 1, 0)).rejects.toThrow("Match not found");
  });

  it("clampea scores negativos a 0", async () => {
    const result = await adminPublishResult("m1", -5, -3);
    expect(result.score?.home).toBe(0);
    expect(result.score?.away).toBe(0);
  });

  it("clampea scores mayores a 20 a 20", async () => {
    const result = await adminPublishResult("m1", 99, 50);
    expect(result.score?.home).toBe(20);
    expect(result.score?.away).toBe(20);
  });

  it("acepta score decimal y lo trunca", async () => {
    const result = await adminPublishResult("m1", 2.9, 1.1);
    expect(result.score?.home).toBe(2);
    expect(result.score?.away).toBe(1);
  });
});

describe("adminGetCategorias", () => {
  it("retorna array vacío en mock", async () => { expect(await adminGetCategorias()).toEqual([]); });
});

describe("adminCrearCategoria", () => {
  it("retorna la categoría creada", async () => {
    const result = await adminCrearCategoria({ nombre: "Souvenirs", descripcion: "Productos" });
    expect(result.nombre).toBe("Souvenirs");
    expect(result.activo).toBe(true);
  });

  it("usa string vacío si no se envía descripcion", async () => {
    const result = await adminCrearCategoria({ nombre: "Test" });
    expect(result.descripcion).toBe("");
  });
});

describe("adminActualizarCategoria", () => {
  it("retorna la categoría actualizada con el id correcto", async () => {
    const result = await adminActualizarCategoria(5, { nombre: "Actualizada" });
    expect(result.id).toBe(5);
    expect(result.nombre).toBe("Actualizada");
  });
});

describe("adminDesactivarCategoria", () => {
  it("retorna categoria con activo false", async () => {
    const result = await adminDesactivarCategoria(1);
    expect(result.categoria.activo).toBe(false);
    expect(result.productosAfectados).toEqual([]);
  });
});

describe("adminReactivarCategoria", () => {
  it("retorna categoria con activo true", async () => {
    expect((await adminReactivarCategoria(1)).categoria.activo).toBe(true);
  });
});

describe("adminGetProductos", () => {
  it("retorna array vacío en mock", async () => { expect(await adminGetProductos()).toEqual([]); });
});

describe("adminCrearProducto", () => {
  it("retorna producto con datos correctos", async () => {
    const result = await adminCrearProducto({ nombre: "Camiseta", precio: 50000, categoriaId: 1, variantes: [{ especificacion: "M", stock: 10 }] });
    expect(result.nombre).toBe("Camiseta");
    expect(result.precio).toBe(50000);
    expect(result.activo).toBe(true);
  });
});

describe("adminActualizarProducto", () => {
  it("retorna producto con id correcto", async () => {
    const result = await adminActualizarProducto(3, { precio: 99000 });
    expect(result.id).toBe(3);
    expect(result.precio).toBe(99000);
  });
});

describe("adminEliminarProducto", () => {
  it("no lanza error en mock", async () => { await expect(adminEliminarProducto(1)).resolves.not.toThrow(); });
});

describe("adminReactivarProducto", () => {
  it("no lanza error en mock", async () => { await expect(adminReactivarProducto(1)).resolves.not.toThrow(); });
});

describe("adminActivarProductosLote", () => {
  it("no lanza error en mock", async () => { await expect(adminActivarProductosLote([1, 2, 3])).resolves.not.toThrow(); });
});

describe("adminSincronizarPartidos", () => {
  it("retorna 0 en mock", async () => { expect(await adminSincronizarPartidos(2018, 2026, "2026-06-10")).toBe(0); });
});

describe("adminGetCapacidadPartidos", () => {
  it("retorna array vacío en mock", async () => { expect(await adminGetCapacidadPartidos()).toEqual([]); });
});

describe("adminGetApuestas", () => {
  it("retorna array vacío en mock", async () => { expect(await adminGetApuestas()).toEqual([]); });
});

describe("adminCerrarApuesta", () => {
  it("no lanza error en mock", async () => { await expect(adminCerrarApuesta(1)).resolves.not.toThrow(); });
});

describe("adminEliminarApuesta", () => {
  it("no lanza error en mock", async () => { await expect(adminEliminarApuesta(1)).resolves.not.toThrow(); });
});

describe("adminForzarPuntos", () => {
  it("no lanza error en mock", async () => { await expect(adminForzarPuntos(1)).resolves.not.toThrow(); });
});

describe("adminEnviarNotificacion", () => {
  it("no lanza error en mock", async () => { await expect(adminEnviarNotificacion({ tipo: "INFO", titulo: "Test", mensaje: "Msg", canal: "SISTEMA" })).resolves.not.toThrow(); });
});

describe("adminEnviarMasiva", () => {
  it("no lanza error en mock", async () => { await expect(adminEnviarMasiva({ tipo: "INFO", titulo: "Test", mensaje: "Msg", canal: "SISTEMA" })).resolves.not.toThrow(); });
});

describe("adminNotificarPorPartido", () => {
  it("no lanza error en mock", async () => { await expect(adminNotificarPorPartido(1, { tipo: "INFO", titulo: "Test", mensaje: "Msg", canal: "SISTEMA" })).resolves.not.toThrow(); });
});

describe("adminGetProductosDeCategoria", () => {
  it("retorna array vacío en mock", async () => { expect(await adminGetProductosDeCategoria(1)).toEqual([]); });
});