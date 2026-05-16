// src/api/eventsApi.ts
import { http } from "./http";
import type { SystemEvent } from "../types/systemEvent";

export async function getSystemEvents(): Promise<SystemEvent[]> {
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