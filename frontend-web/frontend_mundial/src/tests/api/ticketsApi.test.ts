import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), patch: vi.fn() } }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));

import {
  getMyTickets, getTicketById, reserveTicket, cancelTicket,
  markTicketAsPaid, markTicketAsRefunded, transferTicket,
  getPartidosConCapacidad, getCuposPorZona,
} from "../../api/ticketsApi";
import { mockDb, matchesMock } from "../../api/mockDb";
import type { Ticket } from "../../types/ticket";

const futureMatch = () => ({ ...matchesMock[0], id: "m_future", status: "SCHEDULED" as const, startTimeISO: new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString() });
const pastMatch = () => ({ ...matchesMock[0], id: "m_past", status: "SCHEDULED" as const, startTimeISO: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString() });
const makeTicket = (overrides: Partial<Ticket> = {}): Ticket => ({ id: "tk_1", userId: "u1", matchId: "m1", quantity: 2, status: "RESERVADA", createdAt: new Date().toISOString(), ...overrides });

beforeEach(() => { mockDb.tickets = []; mockDb.matches = [...matchesMock]; });

describe("getMyTickets", () => {
  it("retorna vacío si no hay tickets", async () => { expect(await getMyTickets("u1")).toHaveLength(0); });
  it("retorna solo los tickets del usuario", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1" }), makeTicket({ id: "t2", userId: "u2" }));
    expect((await getMyTickets("u1"))).toHaveLength(1);
  });
  it("expira automáticamente tickets RESERVADA vencidos", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1", status: "RESERVADA", expiresAt: new Date(Date.now() - 1000).toISOString() }));
    await getMyTickets("u1");
    expect(mockDb.tickets[0].status).toBe("EXPIRADA");
  });
  it("no expira tickets que aún tienen tiempo", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1", status: "RESERVADA", expiresAt: new Date(Date.now() + 10 * 60 * 1000).toISOString() }));
    await getMyTickets("u1");
    expect(mockDb.tickets[0].status).toBe("RESERVADA");
  });
  it("no expira tickets que no son RESERVADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "t1", userId: "u1", status: "PAGADA", expiresAt: new Date(Date.now() - 1000).toISOString() }));
    await getMyTickets("u1");
    expect(mockDb.tickets[0].status).toBe("PAGADA");
  });
});

describe("getTicketById", () => {
  it("retorna el ticket si existe y pertenece al usuario", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1" }));
    expect((await getTicketById("u1", "tk_1"))?.id).toBe("tk_1");
  });
  it("retorna null si el ticket no existe", async () => { expect(await getTicketById("u1", "no_existe")).toBeNull(); });
  it("retorna null si el ticket pertenece a otro usuario", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u2" }));
    expect(await getTicketById("u1", "tk_1")).toBeNull();
  });
});

describe("reserveTicket", () => {
  beforeEach(() => { mockDb.matches.push(futureMatch()); });

  it("reserva un ticket correctamente", async () => {
    const result = await reserveTicket("u1", "m_future", 2);
    expect(result.status).toBe("RESERVADA");
    expect(result.userId).toBe("u1");
  });
  it("agrega el ticket al mockDb", async () => {
    await reserveTicket("u1", "m_future", 2);
    expect(mockDb.tickets).toHaveLength(1);
  });
  it("clampea quantity a máximo 4", async () => { expect((await reserveTicket("u1", "m_future", 10)).quantity).toBe(4); });
  it("clampea quantity a mínimo 1", async () => { expect((await reserveTicket("u1", "m_future", 0)).quantity).toBe(1); });
  it("clampea quantity negativa a 1", async () => { expect((await reserveTicket("u1", "m_future", -5)).quantity).toBe(1); });
  it("asigna expiresAt en ~15 minutos", async () => {
    const before = Date.now();
    const result = await reserveTicket("u1", "m_future", 1);
    const expiresAt = new Date(result.expiresAt!).getTime();
    expect(expiresAt).toBeGreaterThan(before + 14 * 60 * 1000);
    expect(expiresAt).toBeLessThan(before + 16 * 60 * 1000);
  });
  it("lanza error si el partido no existe", async () => { await expect(reserveTicket("u1", "no_existe", 2)).rejects.toThrow("Match not found"); });
  it("lanza error si el partido está FINISHED", async () => {
    mockDb.matches.push({ ...futureMatch(), id: "m_fin", status: "FINISHED" });
    await expect(reserveTicket("u1", "m_fin", 2)).rejects.toThrow("No puedes reservar entradas para este partido.");
  });
  it("lanza error si el partido está LIVE", async () => {
    mockDb.matches.push({ ...futureMatch(), id: "m_live", status: "LIVE" });
    await expect(reserveTicket("u1", "m_live", 2)).rejects.toThrow("No puedes reservar entradas para este partido.");
  });
  it("lanza error si el partido ya comenzó", async () => {
    mockDb.matches.push(pastMatch());
    await expect(reserveTicket("u1", "m_past", 2)).rejects.toThrow("El partido ya comenzó.");
  });
});

describe("cancelTicket", () => {
  it("cancela un ticket RESERVADA correctamente", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA" }));
    expect(await cancelTicket("u1", "tk_1")).toBe(true);
    expect(mockDb.tickets[0].status).toBe("CANCELADA");
  });
  it("retorna false si el ticket no existe", async () => { expect(await cancelTicket("u1", "no_existe")).toBe(false); });
  it("retorna false si el ticket pertenece a otro usuario", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u2" }));
    expect(await cancelTicket("u1", "tk_1")).toBe(false);
  });
  it("retorna false si el ticket está PAGADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "PAGADA" }));
    expect(await cancelTicket("u1", "tk_1")).toBe(false);
  });
  it("retorna false si el ticket está REEMBOLSADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "REEMBOLSADA" }));
    expect(await cancelTicket("u1", "tk_1")).toBe(false);
  });
  it("retorna false si el ticket está TRANSFERIDA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "TRANSFERIDA" }));
    expect(await cancelTicket("u1", "tk_1")).toBe(false);
  });
  it("retorna true si el ticket ya está CANCELADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "CANCELADA" }));
    expect(await cancelTicket("u1", "tk_1")).toBe(true);
  });
  it("expira el ticket si ya venció antes de cancelar", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA", expiresAt: new Date(Date.now() - 1000).toISOString() }));
    await cancelTicket("u1", "tk_1");
    expect(mockDb.tickets[0].status).toBe("EXPIRADA");
  });
});

describe("markTicketAsPaid", () => {
  it("marca el ticket como PAGADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA" }));
    expect(await markTicketAsPaid("u1", "tk_1", "ref_123")).toBe(true);
    expect(mockDb.tickets[0].status).toBe("PAGADA");
    expect(mockDb.tickets[0].paymentRef).toBe("ref_123");
  });
  it("retorna false si el ticket no existe", async () => { expect(await markTicketAsPaid("u1", "no_existe", "ref")).toBe(false); });
  it("retorna false si el ticket no está RESERVADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "CANCELADA" }));
    expect(await markTicketAsPaid("u1", "tk_1", "ref")).toBe(false);
  });
  it("limpia expiresAt al pagar", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA", expiresAt: new Date(Date.now() + 10 * 60 * 1000).toISOString() }));
    await markTicketAsPaid("u1", "tk_1", "ref");
    expect(mockDb.tickets[0].expiresAt).toBeUndefined();
  });
  it("asigna paidAt al pagar", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA" }));
    await markTicketAsPaid("u1", "tk_1", "ref");
    expect(mockDb.tickets[0].paidAt).toBeTruthy();
  });
});

describe("markTicketAsRefunded", () => {
  it("marca el ticket como REEMBOLSADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "PAGADA" }));
    expect(await markTicketAsRefunded("u1", "tk_1")).toBe(true);
    expect(mockDb.tickets[0].status).toBe("REEMBOLSADA");
  });
  it("retorna false si el ticket no existe", async () => { expect(await markTicketAsRefunded("u1", "no_existe")).toBe(false); });
  it("retorna false si el ticket no está PAGADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA" }));
    expect(await markTicketAsRefunded("u1", "tk_1")).toBe(false);
  });
  it("asigna refundedAt al reembolsar", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "PAGADA" }));
    await markTicketAsRefunded("u1", "tk_1");
    expect(mockDb.tickets[0].refundedAt).toBeTruthy();
  });
});

describe("transferTicket", () => {
  it("transfiere un ticket PAGADA correctamente", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "PAGADA" }));
    expect((await transferTicket("u1", "tk_1", "destino@test.com")).status).toBe("TRANSFERIDA");
  });
  it("lanza error si el ticket no existe", async () => { await expect(transferTicket("u1", "no_existe", "dest@test.com")).rejects.toThrow("Ticket no encontrado"); });
  it("lanza error si el ticket no está PAGADA", async () => {
    mockDb.tickets.push(makeTicket({ id: "tk_1", userId: "u1", status: "RESERVADA" }));
    await expect(transferTicket("u1", "tk_1", "dest@test.com")).rejects.toThrow("Solo se pueden transferir entradas pagadas");
  });
});

describe("getPartidosConCapacidad", () => {
  it("retorna array vacío en mock", async () => { expect(await getPartidosConCapacidad()).toEqual([]); });
});

describe("getCuposPorZona", () => {
  it("retorna array vacío en mock", async () => { expect(await getCuposPorZona("m1")).toEqual([]); });
});