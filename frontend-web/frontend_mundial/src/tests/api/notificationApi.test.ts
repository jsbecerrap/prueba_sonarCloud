import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn(), put: vi.fn() } }));

import {
  getNotifications,
  createNotification,
  markNotificationRead,
  markAllNotificationsRead,
  deleteNotification,
  getUnreadCount,
  clearAll,
  getNotificationsByDate,
} from "../../api/notificationApi";
import { mockDb } from "../../api/mockDb";
import type { NotificationItem } from "../../types/notification";

const makeNotif = (overrides: Partial<NotificationItem> = {}): NotificationItem => ({
  id: `n_${Date.now()}_${Math.random()}`,
  title: "Titulo test",
  body: "Cuerpo test",
  read: false,
  createdAt: new Date().toISOString(),
  ...overrides,
});

beforeEach(() => {
  mockDb.notifications = [];
});

describe("createNotification", () => {
  it("crea una notificación con title y body correctos", async () => {
    await createNotification("Hola", "Mundo");
    expect(mockDb.notifications).toHaveLength(1);
    expect(mockDb.notifications[0].title).toBe("Hola");
    expect(mockDb.notifications[0].body).toBe("Mundo");
  });

  it("la notificación creada viene como no leída", async () => {
    await createNotification("Test", "Body");
    expect(mockDb.notifications[0].read).toBe(false);
  });

  it("lanza error si title está vacío", async () => {
    await expect(createNotification("", "Body")).rejects.toThrow("title/body required");
  });

  it("lanza error si body está vacío", async () => {
    await expect(createNotification("Title", "")).rejects.toThrow("title/body required");
  });

  it("lanza error si title es solo espacios", async () => {
    await expect(createNotification("   ", "Body")).rejects.toThrow("title/body required");
  });

  it("trimea title y body antes de guardar", async () => {
    await createNotification("  Titulo  ", "  Body  ");
    expect(mockDb.notifications[0].title).toBe("Titulo");
    expect(mockDb.notifications[0].body).toBe("Body");
  });
});

describe("getNotifications", () => {
  it("retorna vacío si no hay notificaciones", async () => {
    const result = await getNotifications();
    expect(result.items).toHaveLength(0);
    expect(result.totalElements).toBe(0);
  });

  it("retorna las notificaciones correctamente", async () => {
    mockDb.notifications.push(makeNotif(), makeNotif());
    const result = await getNotifications();
    expect(result.items).toHaveLength(2);
    expect(result.totalElements).toBe(2);
  });

  it("pagina correctamente con page y size", async () => {
    for (let i = 0; i < 5; i++) mockDb.notifications.push(makeNotif());
    const result = await getNotifications(0, 2);
    expect(result.items).toHaveLength(2);
    expect(result.totalPages).toBe(3);
  });

  it("retorna la segunda página correctamente", async () => {
    for (let i = 0; i < 5; i++) mockDb.notifications.push(makeNotif({ title: `notif-${i}` }));
    const result = await getNotifications(1, 2);
    expect(result.items).toHaveLength(2);
  });

  it("calcula totalPages correctamente", async () => {
    for (let i = 0; i < 7; i++) mockDb.notifications.push(makeNotif());
    const result = await getNotifications(0, 3);
    expect(result.totalPages).toBe(3);
  });
});

describe("markNotificationRead", () => {
  it("marca la notificación como leída", async () => {
    const notif = makeNotif({ id: "n_test" });
    mockDb.notifications.push(notif);
    const result = await markNotificationRead("n_test");
    expect(result).toBe(true);
    expect(mockDb.notifications[0].read).toBe(true);
  });

  it("retorna false si la notificación no existe", async () => {
    const result = await markNotificationRead("no_existe");
    expect(result).toBe(false);
  });
});

describe("markAllNotificationsRead", () => {
  it("marca todas las notificaciones como leídas", async () => {
    mockDb.notifications.push(makeNotif(), makeNotif(), makeNotif());
    await markAllNotificationsRead();
    expect(mockDb.notifications.every((n) => n.read)).toBe(true);
  });

  it("no falla si no hay notificaciones", async () => {
    await expect(markAllNotificationsRead()).resolves.not.toThrow();
  });
});

describe("deleteNotification", () => {
  it("elimina la notificación correctamente", async () => {
    mockDb.notifications.push(makeNotif({ id: "n_del" }));
    const result = await deleteNotification("n_del");
    expect(result).toBe(true);
    expect(mockDb.notifications).toHaveLength(0);
  });

  it("retorna false si la notificación no existe", async () => {
    const result = await deleteNotification("no_existe");
    expect(result).toBe(false);
  });
});

describe("getUnreadCount", () => {
  it("retorna 0 si no hay notificaciones", async () => {
    expect(await getUnreadCount()).toBe(0);
  });

  it("cuenta correctamente las no leídas", async () => {
    mockDb.notifications.push(makeNotif({ read: false }), makeNotif({ read: false }), makeNotif({ read: true }));
    expect(await getUnreadCount()).toBe(2);
  });

  it("retorna 0 si todas están leídas", async () => {
    mockDb.notifications.push(makeNotif({ read: true }), makeNotif({ read: true }));
    expect(await getUnreadCount()).toBe(0);
  });
});

describe("clearAll", () => {
  it("elimina todas las notificaciones", async () => {
    mockDb.notifications.push(makeNotif(), makeNotif(), makeNotif());
    await clearAll();
    expect(mockDb.notifications).toHaveLength(0);
  });

  it("no falla si no hay notificaciones", async () => {
    await expect(clearAll()).resolves.not.toThrow();
  });
});

describe("getNotificationsByDate", () => {
  it("retorna notificaciones dentro del rango de fechas", async () => {
    mockDb.notifications.push(makeNotif({ createdAt: "2026-06-10T10:00:00Z" }));
    mockDb.notifications.push(makeNotif({ createdAt: "2026-06-15T10:00:00Z" }));
    const result = await getNotificationsByDate("2026-06-10", "2026-06-12");
    expect(result.items).toHaveLength(1);
  });

  it("retorna vacío si no hay notificaciones en el rango", async () => {
    mockDb.notifications.push(makeNotif({ createdAt: "2026-07-01T10:00:00Z" }));
    const result = await getNotificationsByDate("2026-06-01", "2026-06-30");
    expect(result.items).toHaveLength(0);
  });

  it("pagina correctamente", async () => {
    for (let i = 0; i < 5; i++) mockDb.notifications.push(makeNotif({ createdAt: "2026-06-10T10:00:00Z" }));
    const result = await getNotificationsByDate("2026-06-10", "2026-06-10", 0, 2);
    expect(result.items).toHaveLength(2);
    expect(result.totalPages).toBe(3);
  });
});