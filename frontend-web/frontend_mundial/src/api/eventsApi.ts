import { USE_MOCK } from "./config";
import { http } from "./http";
import { mockDb } from "./mockDb";
import type { SystemEvent, SystemEventType } from "../types/systemEvent";

export type CreateSystemEventPayload = {
  type: SystemEventType;
  actorId?: string;
  actorName?: string;
  entityType?: string;
  entityId?: string;
  message: string;
  data?: Record<string, unknown>;
};

export async function createSystemEvent(payload: CreateSystemEventPayload): Promise<void> {
  if (!USE_MOCK) return;
  const evento: SystemEvent = {
    id: `mock_${Date.now()}_${Math.random().toString(16).slice(2)}`,
    tipo: payload.type,
    descripcion: payload.message,
    fecha: new Date().toISOString(),
    idCorrelacion: payload.entityId,
    entidadCorrelacion: payload.entityType,
    usuarioId: undefined,
  };
  mockDb.systemEvents.unshift(evento);
}

export async function getSystemEvents(page = 0, size = 50): Promise<SystemEvent[]> {
  if (USE_MOCK) return [...mockDb.systemEvents];
  try {
    const res = await http.get<{ content: SystemEvent[] }>(
      `/api/auditoria/todos?page=${page}&size=${size}`
    );
    return res.content ?? [];
  } catch {
    return [];
  }
}

export async function getEventosPorUsuario(usuarioId: number, page = 0, size = 50): Promise<SystemEvent[]> {
  try {
    const res = await http.get<{ content: SystemEvent[] }>(
      `/api/auditoria/usuario/${usuarioId}?page=${page}&size=${size}`
    );
    return res.content ?? [];
  } catch {
    return [];
  }
}

export async function getEventosPorTipo(tipo: string, page = 0, size = 50): Promise<SystemEvent[]> {
  try {
    const res = await http.get<{ content: SystemEvent[] }>(
      `/api/auditoria/tipo/${tipo}?page=${page}&size=${size}`
    );
    return res.content ?? [];
  } catch {
    return [];
  }
}

export async function getEventosPorEntidad(entidad: string, page = 0, size = 50): Promise<SystemEvent[]> {
  try {
    const res = await http.get<{ content: SystemEvent[] }>(
      `/api/auditoria/entidad/${entidad}?page=${page}&size=${size}`
    );
    return res.content ?? [];
  } catch {
    return [];
  }
}

export async function getEventosPorFecha(
  fechaInicio: string,
  fechaFin: string,
  page = 0,
  size = 50
): Promise<SystemEvent[]> {
  try {
    const res = await http.get<{ content: SystemEvent[] }>(
      `/api/auditoria/fecha?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}&page=${page}&size=${size}`
    );
    return res.content ?? [];
  } catch {
    return [];
  }
}