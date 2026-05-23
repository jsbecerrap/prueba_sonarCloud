import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/http", () => ({
  http: { get: vi.fn(), post: vi.fn(), delete: vi.fn() },
}));

import {
  getProductos, getProductosPorCategoria, getProductoPorId,
  getCategorias, getCarrito, agregarAlCarrito, eliminarItemCarrito,
  vaciarCarrito, confirmarOrden, getHistorialOrdenes, getProductosListado,
} from "../../api/storeApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockDelete = vi.mocked(http.delete);

const makeProducto = (id = 1) => ({
  id, nombre: `Producto ${id}`, descripcion: "Desc", precio: 50000,
  stockTotal: 10, imagenUrl: "", activo: true, categoriaNombre: "Cat",
  codigoProducto: null, equipo: null, bandera: null, destacado: false, variantes: [],
});

const makeOrden = () => ({
  id: 1, estado: "PENDIENTE", total: 50000,
  fechaCreacion: new Date().toISOString(), fechaPago: null,
  paymentRef: null, metodoPagoNombre: null, items: [],
});

beforeEach(() => { vi.clearAllMocks(); });

describe("getProductos", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getProductos();
    expect(mockGet).toHaveBeenCalledWith("/api/productos");
  });
  it("retorna la lista de productos", async () => {
    mockGet.mockResolvedValueOnce([makeProducto(1), makeProducto(2)]);
    expect((await getProductos())).toHaveLength(2);
  });
});

describe("getProductosPorCategoria", () => {
  it("llama al endpoint con el categoriaId correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getProductosPorCategoria(3);
    expect(mockGet).toHaveBeenCalledWith("/api/productos?categoriaId=3");
  });
  it("retorna productos de la categoría", async () => {
    mockGet.mockResolvedValueOnce([makeProducto(5)]);
    expect((await getProductosPorCategoria(2))[0].id).toBe(5);
  });
});

describe("getProductoPorId", () => {
  it("llama al endpoint correcto con el id", async () => {
    mockGet.mockResolvedValueOnce(makeProducto(7));
    await getProductoPorId(7);
    expect(mockGet).toHaveBeenCalledWith("/api/productos/7");
  });
  it("retorna el producto correcto", async () => {
    mockGet.mockResolvedValueOnce(makeProducto(7));
    expect((await getProductoPorId(7)).nombre).toBe("Producto 7");
  });
});

describe("getCategorias", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getCategorias();
    expect(mockGet).toHaveBeenCalledWith("/api/categorias");
  });
  it("retorna la lista de categorías", async () => {
    mockGet.mockResolvedValueOnce([{ id: 1, nombre: "Ropa", descripcion: "Desc" }]);
    expect((await getCategorias())[0].nombre).toBe("Ropa");
  });
});

describe("getCarrito", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce(makeOrden());
    await getCarrito();
    expect(mockGet).toHaveBeenCalledWith("/api/ordenes/carrito");
  });
  it("retorna la orden del carrito", async () => {
    mockGet.mockResolvedValueOnce(makeOrden());
    expect((await getCarrito()).estado).toBe("PENDIENTE");
  });
});

describe("agregarAlCarrito", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPost.mockResolvedValueOnce(makeOrden());
    await agregarAlCarrito({ productoId: 1, varianteId: 2, cantidad: 3 });
    expect(mockPost).toHaveBeenCalledWith("/api/ordenes/carrito/agregar", { productoId: 1, varianteId: 2, cantidad: 3 });
  });
  it("retorna la orden actualizada", async () => {
    mockPost.mockResolvedValueOnce({ ...makeOrden(), items: [{ id: 1, productoId: 1, varianteId: 2, productoNombre: "Camiseta", productoImagenUrl: "", especificacion: "M", cantidad: 3, precioUnitario: 50000 }] });
    expect((await agregarAlCarrito({ productoId: 1, varianteId: 2, cantidad: 3 })).items).toHaveLength(1);
  });
});

describe("eliminarItemCarrito", () => {
  it("llama al endpoint correcto con el itemId", async () => {
    mockDelete.mockResolvedValueOnce(makeOrden());
    await eliminarItemCarrito(5);
    expect(mockDelete).toHaveBeenCalledWith("/api/ordenes/carrito/item/5");
  });
  it("retorna la orden actualizada", async () => {
    mockDelete.mockResolvedValueOnce(makeOrden());
    expect((await eliminarItemCarrito(5)).items).toHaveLength(0);
  });
});

describe("vaciarCarrito", () => {
  it("llama al endpoint correcto", async () => {
    mockDelete.mockResolvedValueOnce(makeOrden());
    await vaciarCarrito();
    expect(mockDelete).toHaveBeenCalledWith("/api/ordenes/carrito");
  });
  it("retorna la orden vacía", async () => {
    mockDelete.mockResolvedValueOnce(makeOrden());
    expect((await vaciarCarrito()).items).toHaveLength(0);
  });
});

describe("confirmarOrden", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPost.mockResolvedValueOnce({ ...makeOrden(), estado: "PAGADA" });
    await confirmarOrden({ metodoPagoId: 3 });
    expect(mockPost).toHaveBeenCalledWith("/api/ordenes/carrito/confirmar", { metodoPagoId: 3 });
  });
  it("retorna la orden confirmada", async () => {
    mockPost.mockResolvedValueOnce({ ...makeOrden(), estado: "PAGADA" });
    expect((await confirmarOrden({ metodoPagoId: 3 })).estado).toBe("PAGADA");
  });
});

describe("getHistorialOrdenes", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getHistorialOrdenes();
    expect(mockGet).toHaveBeenCalledWith("/api/ordenes/historial");
  });
  it("retorna el historial de órdenes", async () => {
    mockGet.mockResolvedValueOnce([makeOrden(), makeOrden()]);
    expect((await getHistorialOrdenes())).toHaveLength(2);
  });
});

describe("getProductosListado", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getProductosListado();
    expect(mockGet).toHaveBeenCalledWith("/api/productos/listado");
  });
  it("retorna el listado de productos", async () => {
    mockGet.mockResolvedValueOnce([{ id: 1, nombre: "Camiseta", descripcion: "Desc", precio: 50000, imagenUrl: "", categoriaNombre: "Ropa", equipo: null, bandera: null, destacado: true, stockTotal: 5, tieneVariantes: true, variantes: [] }]);
    expect((await getProductosListado())[0].destacado).toBe(true);
  });
});