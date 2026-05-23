import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), patch: vi.fn() } }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/mockDb", () => ({ mockDb: { tickets: [], matches: [] } }));

import {
  getMyTickets, getTicketById, reserveTicket, cancelTicket,
  markTicketAsPaid, markTicketAsRefunded, transferTicket,
  getPartidosConCapacidad, getCuposPorZona,
} from "../../api/ticketsApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockPatch = vi.mocked(http.patch);

const backendEntrada = (overrides: Record<string, any> = {}) => ({
  id: 1, usuarioId: 1, partidoId: 1, cantidad: 2,
  estado: "RESERVADA", fechaCompra: "2026-06-10T20:00:00Z",
  ttlReserva: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
  fechaPago: null, fechaReembolso: null, paymentRef: null,
  categoria: "BARRA", precio: 50000,
  seleccionLocal: "Argentina", seleccionVisitante: "Brasil",
  estadio: "Azteca", fecha: "2026-06-10", ronda: "Fase de grupos",
  sector: "Norte", fila: "A", asientoInicio: 1,
  ...overrides,
});

beforeEach(() => { vi.clearAllMocks(); });

describe("getMyTickets - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getMyTickets("u1");
    expect(mockGet).toHaveBeenCalledWith("/api/entradas/usuario");
  });
  it("mapea la respuesta del backend correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendEntrada()]);
    const result = await getMyTickets("u1");
    expect(result[0].id).toBe("1");
    expect(result[0].status).toBe("RESERVADA");
    expect(result[0].quantity).toBe(2);
  });
  it("mapea campos opcionales correctamente", async () => {
    mockGet.mockResolvedValueOnce([backendEntrada()]);
    const result = await getMyTickets("u1");
    expect(result[0].seleccionLocal).toBe("Argentina");
    expect(result[0].estadio).toBe("Azteca");
    expect(result[0].sector).toBe("Norte");
    expect(result[0].fila).toBe("A");
    expect(result[0].asientoInicio).toBe(1);
  });
  it("convierte RESERVADA a EXPIRADA si ttlReserva ya pasó", async () => {
    mockGet.mockResolvedValueOnce([backendEntrada({ estado: "RESERVADA", ttlReserva: new Date(Date.now() - 1000).toISOString() })]);
    expect((await getMyTickets("u1"))[0].status).toBe("EXPIRADA");
  });
  it("no expira si ttlReserva no ha pasado", async () => {
    mockGet.mockResolvedValueOnce([backendEntrada({ estado: "RESERVADA" })]);
    expect((await getMyTickets("u1"))[0].status).toBe("RESERVADA");
  });
  it("usa BARRA como categoria por defecto si no viene", async () => {
    mockGet.mockResolvedValueOnce([backendEntrada({ categoria: null })]);
    expect((await getMyTickets("u1"))[0].categoria).toBe("BARRA");
  });
  it("usa 0 como precio por defecto si no viene", async () => {
    mockGet.mockResolvedValueOnce([backendEntrada({ precio: null })]);
    expect((await getMyTickets("u1"))[0].price).toBe(0);
  });
  it("retorna array vacío si no hay tickets", async () => {
    mockGet.mockResolvedValueOnce([]);
    expect(await getMyTickets("u1")).toHaveLength(0);
  });
});

describe("getTicketById - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce(backendEntrada());
    await getTicketById("u1", "tk_1");
    expect(mockGet).toHaveBeenCalledWith("/api/entradas/tk_1");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce(backendEntrada({ id: 5, estado: "PAGADA" }));
    const result = await getTicketById("u1", "5");
    expect(result?.id).toBe("5");
    expect(result?.status).toBe("PAGADA");
  });
  it("mapea campos opcionales", async () => {
    mockGet.mockResolvedValueOnce(backendEntrada());
    const result = await getTicketById("u1", "1");
    expect(result?.ronda).toBe("Fase de grupos");
    expect(result?.categoria).toBe("BARRA");
  });
});

describe("reserveTicket - modo real", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPost.mockResolvedValueOnce(backendEntrada());
    await reserveTicket("u1", "m1", 2, "BARRA", "Norte");
    expect(mockPost).toHaveBeenCalledWith("/api/entradas/reservar", { partidoId: "m1", cantidad: 2, categoria: "BARRA", sector: "Norte" });
  });
  it("mapea la respuesta correctamente", async () => {
    mockPost.mockResolvedValueOnce(backendEntrada({ estado: "RESERVADA" }));
    expect((await reserveTicket("u1", "m1", 2)).status).toBe("RESERVADA");
  });
  it("usa BARRA y Norte como valores por defecto", async () => {
    mockPost.mockResolvedValueOnce(backendEntrada());
    await reserveTicket("u1", "m1", 1);
    expect(mockPost).toHaveBeenCalledWith("/api/entradas/reservar", { partidoId: "m1", cantidad: 1, categoria: "BARRA", sector: "Norte" });
  });
});

describe("cancelTicket - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    await cancelTicket("u1", "tk_1");
    expect(mockPatch).toHaveBeenCalledWith("/api/entradas/cancelar/tk_1");
  });
  it("retorna true al cancelar", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    expect(await cancelTicket("u1", "tk_1")).toBe(true);
  });
});

describe("markTicketAsPaid - modo real", () => {
  it("llama al endpoint correcto con paymentRef", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    await markTicketAsPaid("u1", "tk_1", "ref_123");
    expect(mockPatch).toHaveBeenCalledWith("/api/entradas/pagar/tk_1?paymentRef=ref_123");
  });
  it("retorna true al pagar", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    expect(await markTicketAsPaid("u1", "tk_1", "ref")).toBe(true);
  });
});

describe("markTicketAsRefunded - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    await markTicketAsRefunded("u1", "tk_1");
    expect(mockPatch).toHaveBeenCalledWith("/api/entradas/reembolsar/tk_1");
  });
  it("retorna true al reembolsar", async () => {
    mockPatch.mockResolvedValueOnce(undefined);
    expect(await markTicketAsRefunded("u1", "tk_1")).toBe(true);
  });
});

describe("transferTicket - modo real", () => {
  it("llama al endpoint correcto con correoDestino", async () => {
    mockPatch.mockResolvedValueOnce({ id: "tk_1", status: "TRANSFERIDA" });
    await transferTicket("u1", "tk_1", "destino@test.com");
    expect(mockPatch).toHaveBeenCalledWith("/api/entradas/transferir/tk_1", { correoDestino: "destino@test.com" });
  });
  it("retorna la respuesta del backend", async () => {
    mockPatch.mockResolvedValueOnce({ id: "tk_1", status: "TRANSFERIDA" });
    expect((await transferTicket("u1", "tk_1", "dest@test.com") as any).status).toBe("TRANSFERIDA");
  });
});

describe("getPartidosConCapacidad - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getPartidosConCapacidad();
    expect(mockGet).toHaveBeenCalledWith("/api/entradas/partidos");
  });
  it("retorna la lista de partidos", async () => {
    mockGet.mockResolvedValueOnce([{ id: "m1", local: "ARG", visitante: "BRA", estadio: "Azteca", ciudad: "CDMX", capacidadDisponible: 100 }]);
    expect((await getPartidosConCapacidad())[0].local).toBe("ARG");
  });
});

describe("getCuposPorZona - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce([]);
    await getCuposPorZona("m1");
    expect(mockGet).toHaveBeenCalledWith("/api/entradas/cupos-zona/m1");
  });
  it("retorna la lista de cupos", async () => {
    mockGet.mockResolvedValueOnce([{ zona: "Norte", limite: 100, vendidos: 50, disponibles: 50 }]);
    expect((await getCuposPorZona("m1"))[0].zona).toBe("Norte");
  });
});