// src/api/eventsApi.ts
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

/**
 * En modo real el backend ya registra la auditoría internamente en cada operación.
 * Esta función solo persiste eventos en el mockDb para que el panel de auditoría
 * funcione en modo desarrollo (USE_MOCK=true).
 */
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

export async function getSystemEvents(): Promise<SystemEvent[]> {
  if (USE_MOCK) return [...mockDb.systemEvents];
  try {
    return await http.get<SystemEvent[]>("/api/auditoria/todos");
  } catch {
    return [];
  }
}

export async function getEventosPorUsuario(usuarioId: number): Promise<SystemEvent[]> {
  try {
    return await http.get<SystemEvent[]>(`/api/auditoria/usuario/${usuarioId}`);
  } catch {
    return [];
  }
}

export async function getEventosPorTipo(tipo: string): Promise<SystemEvent[]> {
  try {
    return await http.get<SystemEvent[]>(`/api/auditoria/tipo/${tipo}`);
  } catch {
    return [];
  }
}

export async function getEventosPorEntidad(entidad: string): Promise<SystemEvent[]> {
  try {
    return await http.get<SystemEvent[]>(`/api/auditoria/entidad/${entidad}`);
  } catch {
    return [];
  }
}

export async function getEventosPorFecha(
  fechaInicio: string,
  fechaFin: string
): Promise<SystemEvent[]> {
  try {
    return await http.get<SystemEvent[]>(
      `/api/auditoria/fecha?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`
    );
  } catch {
    return [];
  }
}