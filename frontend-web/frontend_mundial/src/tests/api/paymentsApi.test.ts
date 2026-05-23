import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), patch: vi.fn(), delete: vi.fn() } }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/ticketsApi", () => ({ markTicketAsPaid: vi.fn(), markTicketAsRefunded: vi.fn() }));

import {
  getMyPaymentMethods, addPaymentMethod, setDefaultPaymentMethod,
  deletePaymentMethod, updatePaymentMethod, getMyPaymentTxs,
  createTicketPayment, createCoinsPayment, confirmPaymentTx, refundPaymentTx,
} from "../../api/paymentsApi";
import { markTicketAsPaid, markTicketAsRefunded } from "../../api/ticketsApi";
import { mockDb, paymentsMock } from "../../api/mockDb";
import type { Ticket } from "../../types/ticket";

const mockMarkPaid = vi.mocked(markTicketAsPaid);
const mockMarkRefunded = vi.mocked(markTicketAsRefunded);

const makeTicket = (overrides: Partial<Ticket> = {}): Ticket => ({
  id: "t1", userId: "u1", matchId: "m1", quantity: 1,
  status: "RESERVADA", createdAt: new Date().toISOString(), ...overrides,
});

beforeEach(() => {
  mockDb.payments = [...paymentsMock];
  mockDb.paymentsTx = [];
  mockDb.wallets = [];
  mockDb.tickets = [];
  vi.clearAllMocks();
  mockMarkPaid.mockResolvedValue(true);
  mockMarkRefunded.mockResolvedValue(true);
});

describe("getMyPaymentMethods", () => {
  it("retorna métodos del usuario u1", async () => {
    const result = await getMyPaymentMethods("u1");
    expect(result.every((p) => p.userId === "u1")).toBe(true);
    expect(result.length).toBeGreaterThan(0);
  });
  it("retorna vacío si el usuario no tiene métodos", async () => {
    expect(await getMyPaymentMethods("usuario_sin_metodos")).toHaveLength(0);
  });
  it("no mezcla métodos de diferentes usuarios", async () => {
    const u1 = await getMyPaymentMethods("u1");
    const u2 = await getMyPaymentMethods("u2");
    expect(u1.map((p) => p.id).some((id) => u2.map((p) => p.id).includes(id))).toBe(false);
  });
});

describe("addPaymentMethod", () => {
  it("agrega un método de pago correctamente", async () => {
    const result = await addPaymentMethod("u_nuevo", "CARD", "Visa Test", "4111");
    expect(result.userId).toBe("u_nuevo");
    expect(result.label).toBe("Visa Test");
    expect(result.type).toBe("CARD");
  });
  it("el primer método es isDefault true", async () => {
    expect((await addPaymentMethod("u_nuevo", "PSE", "PSE Test")).isDefault).toBe(true);
  });
  it("el segundo método no es isDefault", async () => {
    await addPaymentMethod("u_nuevo", "CARD", "Primera");
    expect((await addPaymentMethod("u_nuevo", "PSE", "Segunda")).isDefault).toBe(false);
  });
  it("lanza error si label está vacío", async () => {
    await expect(addPaymentMethod("u1", "CARD", "")).rejects.toThrow("label required");
  });
  it("lanza error si label es solo espacios", async () => {
    await expect(addPaymentMethod("u1", "CARD", "   ")).rejects.toThrow("label required");
  });
  it("trimea el label antes de guardar", async () => {
    expect((await addPaymentMethod("u_nuevo", "CARD", "  Mi Visa  ")).label).toBe("Mi Visa");
  });
});

describe("setDefaultPaymentMethod", () => {
  it("cambia el método por defecto correctamente", async () => {
    const pm = await addPaymentMethod("u1", "PSE", "PSE u1");
    expect(await setDefaultPaymentMethod("u1", pm.id)).toBe(true);
    expect(mockDb.payments.find((p) => p.id === pm.id)?.isDefault).toBe(true);
  });
  it("retorna false si el método no pertenece al usuario", async () => {
    expect(await setDefaultPaymentMethod("u1", "pm_u2_card")).toBe(false);
  });
  it("retorna false si el método no existe", async () => {
    expect(await setDefaultPaymentMethod("u1", "no_existe")).toBe(false);
  });
  it("solo un método queda como default después del cambio", async () => {
    await addPaymentMethod("u_test", "CARD", "Primera");
    const segunda = await addPaymentMethod("u_test", "PSE", "Segunda");
    await setDefaultPaymentMethod("u_test", segunda.id);
    const mine = mockDb.payments.filter((p) => p.userId === "u_test");
    expect(mine.filter((p) => p.isDefault)).toHaveLength(1);
    expect(mine.find((p) => p.isDefault)?.id).toBe(segunda.id);
  });
});

describe("deletePaymentMethod", () => {
  it("elimina el método correctamente", async () => {
    const pm = await addPaymentMethod("u_del", "CARD", "A eliminar");
    expect(await deletePaymentMethod("u_del", pm.id)).toBe(true);
    expect(mockDb.payments.find((p) => p.id === pm.id)).toBeUndefined();
  });
  it("retorna false si el método no existe", async () => {
    expect(await deletePaymentMethod("u1", "no_existe")).toBe(false);
  });
  it("retorna false si el método no pertenece al usuario", async () => {
    expect(await deletePaymentMethod("u1", "pm_u2_card")).toBe(false);
  });
  it("asigna nuevo default si se elimina el default", async () => {
    const primera = await addPaymentMethod("u_def", "CARD", "Default");
    await addPaymentMethod("u_def", "PSE", "Otra");
    await deletePaymentMethod("u_def", primera.id);
    expect(mockDb.payments.filter((p) => p.userId === "u_def").some((p) => p.isDefault)).toBe(true);
  });
});

describe("updatePaymentMethod", () => {
  it("actualiza label correctamente", async () => {
    const pm = await addPaymentMethod("u_upd", "CARD", "Original");
    expect((await updatePaymentMethod("u_upd", pm.id, undefined, "Nuevo Label")).label).toBe("Nuevo Label");
  });
  it("actualiza details correctamente", async () => {
    const pm = await addPaymentMethod("u_upd", "CARD", "Test", "old");
    expect((await updatePaymentMethod("u_upd", pm.id, undefined, undefined, "new")).details).toBe("new");
  });
  it("lanza error si el método no existe", async () => {
    await expect(updatePaymentMethod("u1", "no_existe", "CARD", "Label")).rejects.toThrow("Método de pago no encontrado");
  });
});

describe("getMyPaymentTxs", () => {
  it("retorna vacío si no hay transacciones", async () => {
    expect(await getMyPaymentTxs("u1")).toHaveLength(0);
  });
  it("retorna solo las transacciones del usuario", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1" }));
    await createTicketPayment("u1", "t1", "pm_u1_card", 50000);
    expect((await getMyPaymentTxs("u1")).every((t) => t.userId === "u1")).toBe(true);
  });
});

describe("createTicketPayment", () => {
  it("crea una transacción PENDING para el ticket", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1" }));
    const tx = await createTicketPayment("u1", "t1", "pm_u1_card", 50000);
    expect(tx.status).toBe("PENDING");
    expect(tx.kind).toBe("TICKET");
    expect(tx.ticketId).toBe("t1");
  });
  it("lanza error si el método de pago no pertenece al usuario", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1" }));
    await expect(createTicketPayment("u1", "t1", "pm_u2_card", 50000)).rejects.toThrow("Método de pago inválido");
  });
  it("lanza error si el ticket no existe", async () => {
    await expect(createTicketPayment("u1", "no_existe", "pm_u1_card", 50000)).rejects.toThrow("Ticket no existe");
  });
  it("lanza error si el ticket no está RESERVADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "t2", userId: "u1", status: "PAGADA" }));
    await expect(createTicketPayment("u1", "t2", "pm_u1_card", 50000)).rejects.toThrow("no está disponible para pago");
  });
});

describe("createCoinsPayment", () => {
  it("crea una transacción PENDING para coins", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    expect(tx.status).toBe("PENDING");
    expect(tx.kind).toBe("COINS");
    expect(tx.coins).toBe(10);
  });
  it("lanza error si método no pertenece al usuario", async () => {
    await expect(createCoinsPayment("u1", 10, "pm_u2_card", 5000)).rejects.toThrow("Método de pago inválido");
  });
  it("lanza error si coins es 0", async () => {
    await expect(createCoinsPayment("u1", 0, "pm_u1_card", 5000)).rejects.toThrow("Coins inválidas");
  });
  it("lanza error si coins es negativo", async () => {
    await expect(createCoinsPayment("u1", -5, "pm_u1_card", 5000)).rejects.toThrow("Coins inválidas");
  });
  it("lanza error si coins no es finito", async () => {
   await expect(createCoinsPayment("u1", Number.NaN, "pm_u1_card", 5000)).rejects.toThrow("Coins inválidas");
  });
});

describe("confirmPaymentTx", () => {
  it("confirma tx SUCCEEDED si el método no tiene FAIL ni PENDING", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    const result = await confirmPaymentTx("u1", tx.id);
    expect(result.status).toBe("SUCCEEDED");
    expect(result.confirmedAt).toBeTruthy();
  });
  it("acredita coins al wallet si la tx es de tipo COINS", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    await confirmPaymentTx("u1", tx.id);
    expect(mockDb.wallets.find((w) => w.userId === "u1")?.coins).toBe(10);
  });
  it("marca el ticket como pagado si la tx es de tipo TICKET", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1" }));
    const tx = await createTicketPayment("u1", "t1", "pm_u1_card", 50000);
    await confirmPaymentTx("u1", tx.id);
    expect(mockMarkPaid).toHaveBeenCalledWith("u1", "t1", expect.any(String));
  });
  it("marca como FAILED si el método contiene FAIL", async () => {
    const pm = await addPaymentMethod("u1", "CARD", "FAIL card", "FAIL details");
    const tx = await createCoinsPayment("u1", 5, pm.id, 2500);
    const result = await confirmPaymentTx("u1", tx.id);
    expect(result.status).toBe("FAILED");
    expect(result.failReason).toBe("SIMULATED_FAIL");
  });
  it("deja como PENDING si el método contiene PENDING", async () => {
    const pm = await addPaymentMethod("u1", "CARD", "PENDING card");
    const tx = await createCoinsPayment("u1", 5, pm.id, 2500);
    expect((await confirmPaymentTx("u1", tx.id)).status).toBe("PENDING");
  });
  it("lanza error si la tx no existe", async () => {
    await expect(confirmPaymentTx("u1", "no_existe")).rejects.toThrow("Tx no existe");
  });
  it("retorna la tx tal cual si ya no está PENDING", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    await confirmPaymentTx("u1", tx.id);
    expect((await confirmPaymentTx("u1", tx.id)).status).toBe("SUCCEEDED");
  });
});

describe("refundPaymentTx", () => {
  it("reembolsa una tx SUCCEEDED correctamente", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    await confirmPaymentTx("u1", tx.id);
    const result = await refundPaymentTx("u1", tx.id);
    expect(result.status).toBe("REFUNDED");
    expect(result.refundAt).toBeTruthy();
  });
  it("descuenta coins del wallet al reembolsar tx de COINS", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    await confirmPaymentTx("u1", tx.id);
    await refundPaymentTx("u1", tx.id);
    expect(mockDb.wallets.find((w) => w.userId === "u1")?.coins).toBe(0);
  });
  it("llama markTicketAsRefunded si la tx es de tipo TICKET", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1" }));
    const tx = await createTicketPayment("u1", "t1", "pm_u1_card", 50000);
    await confirmPaymentTx("u1", tx.id);
    await refundPaymentTx("u1", tx.id);
    expect(mockMarkRefunded).toHaveBeenCalledWith("u1", "t1");
  });
  it("lanza error si la tx no existe", async () => {
    await expect(refundPaymentTx("u1", "no_existe")).rejects.toThrow("Tx no existe");
  });
  it("lanza error si la tx no está SUCCEEDED", async () => {
    const tx = await createCoinsPayment("u1", 10, "pm_u1_card", 5000);
    await expect(refundPaymentTx("u1", tx.id)).rejects.toThrow("Solo puedes reembolsar pagos exitosos");
  });
  it("no deja coins negativas al reembolsar", async () => {
    const tx = await createCoinsPayment("u1", 100, "pm_u1_card", 5000);
    await confirmPaymentTx("u1", tx.id);
    const wallet = mockDb.wallets.find((w) => w.userId === "u1")!;
    wallet.coins = 5;
    await refundPaymentTx("u1", tx.id);
    expect(wallet.coins).toBe(0);
  });
});