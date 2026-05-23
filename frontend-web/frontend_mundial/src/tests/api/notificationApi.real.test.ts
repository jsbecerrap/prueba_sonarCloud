import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), put: vi.fn() } }));
vi.mock("../../api/mockDb", () => ({ mockDb: { notifications: [] } }));

import {
  getNotifications, createNotification, markNotificationRead,
  markAllNotificationsRead, deleteNotification, getUnreadCount,
  clearAll, registrarFcmToken, getNotificationsByDate,
} from "../../api/notificationApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockPut = vi.mocked(http.put);

const backendNotif = (overrides: Record<string, any> = {}) => ({
  id: 1, titulo: "Título test", mensaje: "Mensaje test",
  leida: false, fecha: "2026-06-10T10:00:00Z", ...overrides,
});

beforeEach(() => { vi.clearAllMocks(); });

describe("getNotifications - modo real", () => {
  it("llama al endpoint correcto con page y size por defecto", async () => {
    mockGet.mockResolvedValueOnce({ content: [], totalPages: 0, totalElements: 0 });
    await getNotifications();
    expect(mockGet).toHaveBeenCalledWith("/api/notificaciones?page=0&size=20");
  });
  it("llama al endpoint con page y size personalizados", async () => {
    mockGet.mockResolvedValueOnce({ content: [], totalPages: 0, totalElements: 0 });
    await getNotifications(2, 5);
    expect(mockGet).toHaveBeenCalledWith("/api/notificaciones?page=2&size=5");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce({ content: [backendNotif()], totalPages: 1, totalElements: 1 });
    const result = await getNotifications();
    expect(result.items[0].id).toBe("1");
    expect(result.items[0].title).toBe("Título test");
    expect(result.items[0].read).toBe(false);
    expect(result.totalPages).toBe(1);
  });
  it("mapea leida correctamente", async () => {
    mockGet.mockResolvedValueOnce({ content: [backendNotif({ leida: true })], totalPages: 1, totalElements: 1 });
    expect((await getNotifications()).items[0].read).toBe(true);
  });
});

describe("createNotification - modo real", () => {
  it("llama al endpoint correcto con los datos", async () => {
    mockPost.mockResolvedValueOnce(undefined);
    await createNotification("Hola", "Mundo");
    expect(mockPost).toHaveBeenCalledWith("/api/notificaciones/enviar", { tipo: "INFO", titulo: "Hola", mensaje: "Mundo", canal: "SISTEMA", usuarioId: null });
  });
  it("trimea title y body antes de enviar", async () => {
    mockPost.mockResolvedValueOnce(undefined);
    await createNotification("  Hola  ", "  Mundo  ");
    expect(mockPost).toHaveBeenCalledWith("/api/notificaciones/enviar", expect.objectContaining({ titulo: "Hola", mensaje: "Mundo" }));
  });
  it("no lanza error al crear", async () => {
    mockPost.mockResolvedValueOnce(undefined);
    await expect(createNotification("T", "B")).resolves.not.toThrow();
  });
});

describe("markNotificationRead - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await markNotificationRead("n_1");
    expect(mockPut).toHaveBeenCalledWith("/api/notificaciones/n_1/leida");
  });
  it("retorna true", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    expect(await markNotificationRead("n_1")).toBe(true);
  });
});

describe("markAllNotificationsRead - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await markAllNotificationsRead();
    expect(mockPut).toHaveBeenCalledWith("/api/notificaciones/leidas");
  });
  it("no lanza error", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await expect(markAllNotificationsRead()).resolves.not.toThrow();
  });
});

describe("deleteNotification - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await deleteNotification("n_1");
    expect(mockPut).toHaveBeenCalledWith("/api/notificaciones/n_1/leida");
  });
  it("retorna true", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    expect(await deleteNotification("n_1")).toBe(true);
  });
});

describe("getUnreadCount - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockGet.mockResolvedValueOnce({ total: 5 });
    await getUnreadCount();
    expect(mockGet).toHaveBeenCalledWith("/api/notificaciones/sin-leer/conteo");
  });
  it("retorna el total correcto", async () => {
    mockGet.mockResolvedValueOnce({ total: 7 });
    expect(await getUnreadCount()).toBe(7);
  });
});

describe("clearAll - modo real", () => {
  it("llama al endpoint correcto", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await clearAll();
    expect(mockPut).toHaveBeenCalledWith("/api/notificaciones/leidas");
  });
  it("no lanza error", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await expect(clearAll()).resolves.not.toThrow();
  });
});

describe("registrarFcmToken - modo real", () => {
  it("llama al endpoint correcto con el token", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await registrarFcmToken("fcm_token_123");
    expect(mockPut).toHaveBeenCalledWith("/api/usuarios/fcm-token", { fcmToken: "fcm_token_123" });
  });
  it("no lanza error", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    await expect(registrarFcmToken("token")).resolves.not.toThrow();
  });
});

describe("getNotificationsByDate - modo real", () => {
  it("llama al endpoint correcto con los parámetros", async () => {
    mockGet.mockResolvedValueOnce({ content: [], totalPages: 0, totalElements: 0 });
    await getNotificationsByDate("2026-06-01", "2026-06-30");
    expect(mockGet).toHaveBeenCalledWith("/api/notificaciones/buscar?desde=2026-06-01&hasta=2026-06-30&page=0&size=20");
  });
  it("mapea la respuesta correctamente", async () => {
    mockGet.mockResolvedValueOnce({ content: [backendNotif()], totalPages: 1, totalElements: 1 });
    expect((await getNotificationsByDate("2026-06-01", "2026-06-30")).items[0].title).toBe("Título test");
  });
  it("llama con page y size personalizados", async () => {
    mockGet.mockResolvedValueOnce({ content: [], totalPages: 0, totalElements: 0 });
    await getNotificationsByDate("2026-06-01", "2026-06-30", 1, 5);
    expect(mockGet).toHaveBeenCalledWith("/api/notificaciones/buscar?desde=2026-06-01&hasta=2026-06-30&page=1&size=5");
  });
});