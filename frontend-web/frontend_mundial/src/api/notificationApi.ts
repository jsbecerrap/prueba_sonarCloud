import { USE_MOCK } from "./config";
import { http } from "./http";
import { mockDb } from "./mockDb";
import type { NotificationItem } from "../types/notification";

const sleep = (ms = 200) => new Promise((r) => setTimeout(r, ms));
const nid = () => `n_${Date.now()}_${Math.random().toString(16).slice(2)}`;

export async function getNotifications(page = 0, size = 20): Promise<{ items: NotificationItem[]; totalPages: number; totalElements: number }> {
  if (!USE_MOCK) {
    const data = await http.get<{
      content: { id: number; titulo: string; mensaje: string; leida: boolean; fecha: string }[];
      totalPages: number;
      totalElements: number;
    }>(`/api/notificaciones?page=${page}&size=${size}`);
    return {
      items: data.content.map((n) => ({ id: String(n.id), title: n.titulo, body: n.mensaje, read: n.leida, createdAt: n.fecha })),
      totalPages: data.totalPages,
      totalElements: data.totalElements,
    };
  }
  await sleep();
  const all = mockDb.notifications.slice().sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1));
  const start = page * size;
  return { items: all.slice(start, start + size), totalPages: Math.ceil(all.length / size), totalElements: all.length };
}

export async function createNotification(title: string, body: string): Promise<void> {
  if (!USE_MOCK) {
    await http.post<void>("/api/notificaciones/enviar", {
      tipo: "INFO",
      titulo: title.trim(),
      mensaje: body.trim(),
      canal: "SISTEMA",
      usuarioId: null, 
    });
    return;
  }
  await sleep();
  const t = title.trim();
  const b = body.trim();
  if (!t || !b) throw new Error("title/body required");
  const item: NotificationItem = {
    id: nid(), title: t, body: b, read: false, createdAt: new Date().toISOString(),
  };
  mockDb.notifications.unshift(item);
}

export async function markNotificationRead(id: string): Promise<boolean> {
  if (!USE_MOCK) {
    await http.put<void>(`/api/notificaciones/${id}/leida`);
    return true;
  }
  await sleep();
  const n = mockDb.notifications.find((x) => x.id === id);
  if (!n) return false;
  n.read = true;
  return true;
}

export async function markAllNotificationsRead(): Promise<void> {
  if (!USE_MOCK) {
    await http.put<void>("/api/notificaciones/leidas");
    return;
  }
  await sleep();
  mockDb.notifications.forEach((n) => { n.read = true; });
}
export async function deleteNotification(id: string): Promise<boolean> {
  if (!USE_MOCK) {
    await http.put<void>(`/api/notificaciones/${id}/leida`);
    return true;
  }
  await sleep();
  const idx = mockDb.notifications.findIndex((x) => x.id === id);
  if (idx === -1) return false;
  mockDb.notifications.splice(idx, 1);
  return true;
}
export async function getUnreadCount(): Promise<number> {
  if (!USE_MOCK) {
    const data = await http.get<{ total: number }>("/api/notificaciones/sin-leer/conteo");
    return data.total;
  }
  await sleep();
  return mockDb.notifications.filter((n) => !n.read).length;
}
export async function clearAll(): Promise<void> {
  if (!USE_MOCK) {
    await http.put<void>("/api/notificaciones/leidas");
    return;
  }
  await sleep();
  mockDb.notifications.length = 0;
}

export async function registrarFcmToken(fcmToken: string): Promise<void> {
  if (!USE_MOCK) {
    await http.put<void>("/api/usuarios/fcm-token", { fcmToken });
    return;
  }
  await sleep();
}
export async function getNotificationsByDate(desde: string, hasta: string, page = 0, size = 20): Promise<{ items: NotificationItem[]; totalPages: number; totalElements: number }> {
  if (!USE_MOCK) {
    const data = await http.get<{
      content: { id: number; titulo: string; mensaje: string; leida: boolean; fecha: string }[];
      totalPages: number;
      totalElements: number;
    }>(`/api/notificaciones/buscar?desde=${desde}&hasta=${hasta}&page=${page}&size=${size}`);
    return {
      items: data.content.map((n) => ({ id: String(n.id), title: n.titulo, body: n.mensaje, read: n.leida, createdAt: n.fecha })),
      totalPages: data.totalPages,
      totalElements: data.totalElements,
    };
  }
  await sleep();
  const filtered = mockDb.notifications
    .filter((n) => n.createdAt >= desde && n.createdAt <= hasta + "T23:59:59")
    .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1));
  const start = page * size;
  return { items: filtered.slice(start, start + size), totalPages: Math.ceil(filtered.length / size), totalElements: filtered.length };
}