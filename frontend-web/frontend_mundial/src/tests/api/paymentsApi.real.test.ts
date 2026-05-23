import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), patch: vi.fn(), delete: vi.fn() } }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/ticketsApi", () => ({ markTicketAsPaid: vi.fn(), markTicketAsRefunded: vi.fn() }));
vi.mock("../../api/mockDb", () => ({ mockDb: { payments: [], paymentsTx: [], wallets: [] } }));

import {
  getMyPaymentMethods, addPaymentMethod, setDefaultPaymentMethod,
  deletePaymentMethod, updatePaymentMethod, getMyPaymentTxs,
  confirmPaymentTx, refundPaymentTx,
} from "../../api/paymentsApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockPatch = vi.mocked(http.patch);
const mockDelete = vi.mocked(http.delete);

beforeEach(() => { vi.clearAllMocks(); });

describe("getMyPaymentMethods - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getMyPaymentMethods("u1");
    expect(mockGet).toHaveBeenCalledWith("/payments");
  });
  it("retorna la lista de métodos de pago", async () => {
    mockGet.mockResolvedValueOnce([{ id: "pm_1", label: "Visa" }]);
    expect((await getMyPaymentMethods("u1"))[0].label).toBe("Visa");
  });
});

describe("addPaymentMethod - modo real", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPost.mockResolvedValueOnce({ id: "pm_1", label: "Visa" });
    await addPaymentMethod("u1", "CARD", "Visa", "4111");
    expect(mockPost).toHaveBeenCalledWith("/payments", { type: "CARD", label: "Visa", details: "4111" });
  });
  it("retorna el método de pago creado", async () => {
    mockPost.mockResolvedValueOnce({ id: "pm_1", label: "Visa" });
    expect((await addPaymentMethod("u1", "CARD", "Visa")).label).toBe("Visa");
  });
  it("envía details undefined si no se proporciona", async () => {
    mockPost.mockResolvedValueOnce({ id: "pm_1", label: "PSE" });
    await addPaymentMethod("u1", "PSE", "PSE Test");
    expect(mockPost).toHaveBeenCalledWith("/payments", { type: "PSE", label: "PSE Test", details: undefined });
  });
});

describe("setDefaultPaymentMethod - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    await setDefaultPaymentMethod("u1", "pm_1");
    expect(mockPatch).toHaveBeenCalledWith("/payments/pm_1/default", { userId: "u1" });
  });
  it("retorna true", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    expect(await setDefaultPaymentMethod("u1", "pm_1")).toBe(true);
  });
});

describe("deletePaymentMethod - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockDelete.mockResolvedValueOnce(undefined);
    await deletePaymentMethod("u1", "pm_1");
    expect(mockDelete).toHaveBeenCalledWith("/payments/pm_1");
  });
  it("retorna true", async () => {
    mockDelete.mockResolvedValueOnce(undefined);
    expect(await deletePaymentMethod("u1", "pm_1")).toBe(true);
  });
});

describe("updatePaymentMethod - modo real", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPatch.mockResolvedValueOnce({ id: "pm_1", label: "Nueva Visa" });
    await updatePaymentMethod("u1", "pm_1", "CARD", "Nueva Visa", "4444");
    expect(mockPatch).toHaveBeenCalledWith("/payments/pm_1", { type: "CARD", label: "Nueva Visa", details: "4444" });
  });
  it("retorna el método actualizado", async () => {
    mockPatch.mockResolvedValueOnce({ id: "pm_1", label: "Nueva Visa" });
    expect((await updatePaymentMethod("u1", "pm_1", undefined, "Nueva Visa")).label).toBe("Nueva Visa");
  });
});

describe("getMyPaymentTxs - modo real", () => {
  it("llama a los endpoints de órdenes y entradas", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await getMyPaymentTxs("u1");
    expect(mockGet).toHaveBeenCalledWith("/api/ordenes/historial/liviano");
    expect(mockGet).toHaveBeenCalledWith("/api/entradas/usuario");
  });
  it("mapea órdenes correctamente", async () => {
    mockGet.mockResolvedValueOnce([{ id: 1, items: [], total: 50000, estado: "PAGADA", fechaCreacion: "2026-06-10T10:00:00Z", fechaPago: null, paymentRef: null, metodoPagoLabel: null }]).mockResolvedValueOnce([]);
    const result = await getMyPaymentTxs("u1");
    expect(result[0].kind).toBe("ORDEN");
    expect(result[0].status).toBe("SUCCEEDED");
    expect(result[0].amount).toBe(50000);
  });
  it("mapea entradas pagadas correctamente", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([{ id: 2, usuarioId: 1, partidoId: 1, cantidad: 2, estado: "PAGADA", precio: 100000, fechaCompra: "2026-06-10T10:00:00Z", fechaPago: null, paymentRef: null }]);
    expect((await getMyPaymentTxs("u1"))[0].kind).toBe("TICKET");
  });
  it("mapea entradas reembolsadas correctamente", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([{ id: 3, usuarioId: 1, partidoId: 1, cantidad: 1, estado: "REEMBOLSADA", precio: 50000, fechaCompra: "2026-06-10T10:00:00Z", paymentRef: null }]);
    expect((await getMyPaymentTxs("u1"))[0].status).toBe("REFUNDED");
  });
  it("filtra entradas que no son PAGADA ni REEMBOLSADA", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([{ id: 4, estado: "RESERVADA", precio: 50000, fechaCompra: "2026-06-10T10:00:00Z" }]);
    expect(await getMyPaymentTxs("u1")).toHaveLength(0);
  });
  it("ordena por fecha descendente", async () => {
    mockGet.mockResolvedValueOnce([
      { id: 1, items: [], total: 1000, estado: "PAGADA", fechaCreacion: "2026-06-11T10:00:00Z", fechaPago: null, paymentRef: null, metodoPagoLabel: null },
      { id: 2, items: [], total: 2000, estado: "PAGADA", fechaCreacion: "2026-06-09T10:00:00Z", fechaPago: null, paymentRef: null, metodoPagoLabel: null },
    ]).mockResolvedValueOnce([]);
    expect((await getMyPaymentTxs("u1"))[0].amount).toBe(1000);
  });
});

describe("confirmPaymentTx - modo real", () => {
  it("retorna una tx con status SUCCEEDED", async () => {
    const result = await confirmPaymentTx("u1", "tx_123");
    expect(result.status).toBe("SUCCEEDED");
    expect(result.id).toBe("tx_123");
  });
  it("retorna userId correcto", async () => {
    expect((await confirmPaymentTx("u1", "tx_123")).userId).toBe("u1");
  });
});

describe("refundPaymentTx - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPost.mockResolvedValueOnce({ id: "tx_123", status: "REFUNDED" });
    await refundPaymentTx("u1", "tx_123");
    expect(mockPost).toHaveBeenCalledWith("/payments/txs/tx_123/refund", { userId: "u1" });
  });
  it("retorna la tx reembolsada", async () => {
    mockPost.mockResolvedValueOnce({ id: "tx_123", status: "REFUNDED" });
    expect((await refundPaymentTx("u1", "tx_123") as any).status).toBe("REFUNDED");
  });
});