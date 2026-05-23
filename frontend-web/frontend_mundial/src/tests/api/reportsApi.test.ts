import { describe, it, expect, vi } from "vitest";

vi.mock("../../api/http", () => ({ http: { get: vi.fn() } }));

import {
  getEstadisticasGenerales, getReportesCompras, getPartidosMasApostados,
  getPollaRanking, getIngresosPorMetodoPago, getEntradasPorPartido,
  getTopUsuariosSouvenir, getTopUsuariosEntrada,
} from "../../api/reportsApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);

describe("getEstadisticasGenerales", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce({ totalUsuarios: 10, totalPartidos: 5, totalTransacciones: 20, usuariosActivos: 8 });
    const result = await getEstadisticasGenerales();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/estadisticas-generales");
    expect(result.totalUsuarios).toBe(10);
  });
  it("retorna la estructura correcta", async () => {
    mockGet.mockResolvedValueOnce({ totalUsuarios: 0, totalPartidos: 0, totalTransacciones: 0, usuariosActivos: 0 });
    const result = await getEstadisticasGenerales();
    expect(result).toHaveProperty("totalUsuarios");
    expect(result).toHaveProperty("totalPartidos");
    expect(result).toHaveProperty("totalTransacciones");
    expect(result).toHaveProperty("usuariosActivos");
  });
});

describe("getReportesCompras", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce({ ingresoTotal: 0, totalOrdenes: 0, totalEntradasVendidas: 0, productosMasVendidos: [], ventasPorCategoria: [] });
    await getReportesCompras();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/compras");
  });
  it("retorna la estructura correcta", async () => {
    mockGet.mockResolvedValueOnce({ ingresoTotal: 500000, totalOrdenes: 10, totalEntradasVendidas: 5, productosMasVendidos: [], ventasPorCategoria: [] });
    const result = await getReportesCompras();
    expect(result.ingresoTotal).toBe(500000);
    expect(Array.isArray(result.productosMasVendidos)).toBe(true);
  });
});

describe("getPartidosMasApostados", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getPartidosMasApostados();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/partidos-apostados");
  });
  it("retorna un array", async () => {
    mockGet.mockResolvedValueOnce([{ partidoId: 1, local: "ARG", visitante: "BRA", ronda: "Grupos", totalPronosticos: 50 }]);
    expect((await getPartidosMasApostados())[0].totalPronosticos).toBe(50);
  });
});

describe("getPollaRanking", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getPollaRanking();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/pollas");
  });
  it("retorna un array", async () => {
    mockGet.mockResolvedValueOnce([{ apuestaId: 1, nombre: "Polla test", estado: "ABIERTA", totalParticipantes: 5 }]);
    expect((await getPollaRanking())[0].nombre).toBe("Polla test");
  });
});

describe("getIngresosPorMetodoPago", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getIngresosPorMetodoPago();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/metodos-pago");
  });
  it("retorna un array con la estructura correcta", async () => {
    mockGet.mockResolvedValueOnce([{ tipo: "CARD", totalOrdenes: 10, ingresoTotal: 500000 }]);
    expect((await getIngresosPorMetodoPago())[0].ingresoTotal).toBe(500000);
  });
});

describe("getEntradasPorPartido", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getEntradasPorPartido();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/entradas-por-partido");
  });
  it("retorna un array con la estructura correcta", async () => {
    mockGet.mockResolvedValueOnce([{ partidoId: 1, local: "ARG", visitante: "BRA", ronda: "Final", estadio: "Azteca", cantidadVendida: 100, ingresoTotal: 5000000 }]);
    expect((await getEntradasPorPartido())[0].estadio).toBe("Azteca");
  });
});

describe("getTopUsuariosSouvenir", () => {
  it("llama al endpoint con size por defecto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getTopUsuariosSouvenir();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/top-souvenir?size=5");
  });
  it("llama al endpoint con size personalizado", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getTopUsuariosSouvenir(10);
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/top-souvenir?size=10");
  });
  it("retorna un array con la estructura correcta", async () => {
    mockGet.mockResolvedValueOnce([{ usuarioId: 1, nombre: "Sara", apellido: "López", correo: "sara@test.com", totalOrdenes: 5, totalGastado: 250000 }]);
    expect((await getTopUsuariosSouvenir())[0].totalGastado).toBe(250000);
  });
});

describe("getTopUsuariosEntrada", () => {
  it("llama al endpoint con size por defecto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getTopUsuariosEntrada();
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/top-entradas?size=5");
  });
  it("llama al endpoint con size personalizado", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getTopUsuariosEntrada(3);
    expect(mockGet).toHaveBeenCalledWith("/api/reportes/top-entradas?size=3");
  });
  it("retorna un array con la estructura correcta", async () => {
    mockGet.mockResolvedValueOnce([{ usuarioId: 2, nombre: "Juan", apellido: "García", correo: "juan@test.com", totalEntradas: 8, totalGastado: 400000 }]);
    expect((await getTopUsuariosEntrada())[0].totalEntradas).toBe(8);
  });
});