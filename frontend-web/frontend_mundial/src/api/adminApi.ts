import { USE_MOCK } from "./config";
import { http } from "./http";
import { mockDb } from "./mockDb";
import { poolsMock } from "./poolsMockDb";
import { createSystemEvent } from "./eventsApi";

import type { Match, MatchStatus, Team } from "../types/match";
import { recalcPoolPoints } from "./poolsApi";

const sleep = (ms: number) => new Promise<void>((r) => setTimeout(r, ms));
const mid = () => `m_${Date.now()}_${Math.random().toString(16).slice(2)}`;

function clampScore(n: number) {
  const x = Number.isFinite(n) ? Math.floor(n) : 0;
  return Math.max(0, Math.min(20, x));
}

function sealPredictionsForMatch(matchId: string) {
  const now = new Date().toISOString();
  for (const pr of mockDb.predictions) {
    if (pr.matchId !== matchId) continue;
    if (!pr.locked) {
      pr.locked = true;
      pr.lockedAt = now;
    }
  }
}

async function recalcAllPools() {
  for (const p of poolsMock) {
    await recalcPoolPoints(p.id);
  }
}

export type UsuarioSistema = {
  id: number;
  correoUsuario: string;
  nombre: string;
  apellido: string;
  rol: string;
};

export type RegistrarUsuarioPayload = {
  correoUsuario: string;
  contrasena: string;
  nombre: string;
  apellido: string;
  rol: string;
};

export async function adminGetUsuarios(): Promise<UsuarioSistema[]> {
  if (!USE_MOCK) {
    return http.get<UsuarioSistema[]>("/api/usuarios/listar");
  }
  await sleep(150);
  return [];
}

export async function adminRegistrarUsuario(payload: RegistrarUsuarioPayload): Promise<UsuarioSistema> {
  if (!USE_MOCK) {
    return http.post<UsuarioSistema>("/api/usuarios/registrar", payload);
  }
  await sleep(200);
  return { id: Date.now(), ...payload };
}

export async function adminEliminarUsuario(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.delete(`/api/usuarios/${id}`);
    return;
  }
  await sleep(150);
}

export async function adminGetMatches(): Promise<Match[]> {
  if (!USE_MOCK) {
    return http.get<Match[]>("/admin/matches");
  }
  await sleep(150);
  return [...mockDb.matches];
}

export async function adminCreateMatch(payload: {
  home: Team;
  away: Team;
  city: string;
  stadium: string;
  startTimeISO: string;
  status?: MatchStatus;
  assignToAllPools?: boolean;
}): Promise<Match> {
  if (!USE_MOCK) {
    const created = await http.post<Match>("/admin/matches", payload);
    await createSystemEvent({
      type: "MATCH_STATUS_CHANGED",
      actorId: "admin",
      actorName: "Admin",
      entityType: "MATCH",
      entityId: created.id,
      message: `Partido creado: ${created.home.name} vs ${created.away.name}`,
      data: { city: created.city, stadium: created.stadium, startTimeISO: created.startTimeISO, status: created.status },
    });
    return created;
  }

  await sleep(200);
  const match: Match = {
    id: mid(),
    home: payload.home,
    away: payload.away,
    city: payload.city,
    stadium: payload.stadium,
    startTimeISO: payload.startTimeISO,
    status: payload.status ?? "SCHEDULED",
  };
  mockDb.matches.push(match);
  const assign = payload.assignToAllPools ?? true;
  if (assign) {
    for (const p of poolsMock) {
      if (!p.matchIds.includes(match.id)) p.matchIds.push(match.id);
    }
  }
  await createSystemEvent({
    type: "MATCH_STATUS_CHANGED",
    actorId: "admin",
    actorName: "Admin",
    entityType: "MATCH",
    entityId: match.id,
    message: `Partido creado: ${match.home.name} vs ${match.away.name}`,
    data: { city: match.city, stadium: match.stadium, startTimeISO: match.startTimeISO, status: match.status, assignToAllPools: assign },
  });
  return match;
}

export async function adminSetMatchStatus(matchId: string, status: MatchStatus): Promise<Match> {
  if (!USE_MOCK) {
    const match = await http.patch<Match>(`/admin/matches/${matchId}/status`, { status });
    await createSystemEvent({
      type: "MATCH_CREATED",
      actorId: "admin",
      actorName: "Admin",
      entityType: "MATCH",
      entityId: match.id,
      message: `Estado actualizado: ${match.home.name} vs ${match.away.name} → ${status}`,
      data: { status },
    });
    return match;
  }

  await sleep(150);
  const match = mockDb.matches.find((m) => m.id === matchId);
  if (!match) throw new Error("Match not found");
  match.status = status;
  if (status === "LIVE" || status === "FINISHED") sealPredictionsForMatch(matchId);
  if (status === "FINISHED" && match.score) await recalcAllPools();
  await createSystemEvent({
    type: "MATCH_CREATED",
    actorId: "admin",
    actorName: "Admin",
    entityType: "MATCH",
    entityId: match.id,
    message: `Estado actualizado: ${match.home.name} vs ${match.away.name} → ${status}`,
    data: { status },
  });
  return match;
}

export async function adminPublishResult(matchId: string, home: number, away: number): Promise<Match> {
  if (!USE_MOCK) {
    const match = await http.post<Match>(`/admin/matches/${matchId}/result`, { homeScore: home, awayScore: away });
    await createSystemEvent({
      type: "MATCH_RESULT_PUBLISHED",
      actorId: "admin",
      actorName: "Admin",
      entityType: "MATCH",
      entityId: match.id,
      message: `Resultado publicado: ${match.home.name} ${match.score?.home ?? 0} - ${match.score?.away ?? 0} ${match.away.name}`,
      data: { home: match.score?.home ?? 0, away: match.score?.away ?? 0 },
    });
    return match;
  }

  await sleep(200);
  const match = mockDb.matches.find((m) => m.id === matchId);
  if (!match) throw new Error("Match not found");
  const h = clampScore(home);
  const a = clampScore(away);
  match.score = { home: h, away: a };
  match.status = "FINISHED";
  sealPredictionsForMatch(matchId);
  await recalcAllPools();
  await createSystemEvent({
    type: "MATCH_RESULT_PUBLISHED",
    actorId: "admin",
    actorName: "Admin",
    entityType: "MATCH",
    entityId: match.id,
    message: `Resultado publicado: ${match.home.name} ${h} - ${a} ${match.away.name}`,
    data: { home: h, away: a },
  });
  return match;
}
export type Categoria = {
  id: number;
  nombre: string;
  descripcion: string;
};

export type CategoriaPayload = {
  nombre: string;
  descripcion?: string;
};

export type Producto = {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  imagenUrl: string;
  activo: boolean;
  categoriaNombre: string;
};

export type ProductoPayload = {
  nombre: string;
  descripcion?: string;
  precio: number;
  stock: number;
  imagenUrl?: string;
  categoriaId: number;
};

export type ProductoActualizarPayload = {
  precio?: number;
  stock?: number;
  imagenUrl?: string;
  descripcion?: string;
};

export async function adminGetCategorias(): Promise<Categoria[]> {
  if (!USE_MOCK) {
    return http.get<Categoria[]>("/api/categorias");
  }
  await sleep(150);
  return [];
}

export async function adminCrearCategoria(payload: CategoriaPayload): Promise<Categoria> {
  if (!USE_MOCK) {
    return http.post<Categoria>("/api/categorias", payload);
  }
  await sleep(200);
  return { id: Date.now(), nombre: payload.nombre, descripcion: payload.descripcion ?? "" };
}

export async function adminActualizarCategoria(id: number, payload: CategoriaPayload): Promise<Categoria> {
  if (!USE_MOCK) {
    return http.put<Categoria>(`/api/categorias/${id}`, payload);
  }
  await sleep(200);
  return { id, nombre: payload.nombre, descripcion: payload.descripcion ?? "" };
}

export async function adminEliminarCategoria(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.delete(`/api/categorias/${id}`);
    return;
  }
  await sleep(150);
}

export async function adminGetProductos(): Promise<Producto[]> {
  if (!USE_MOCK) {
    return http.get<Producto[]>("/api/productos/admin/todos");
  }
  await sleep(150);
  return [];
}

export async function adminCrearProducto(payload: ProductoPayload): Promise<Producto> {
  if (!USE_MOCK) {
    return http.post<Producto>("/api/productos", payload);
  }
  await sleep(200);
  return { id: Date.now(), nombre: payload.nombre, descripcion: payload.descripcion ?? "", precio: payload.precio, stock: payload.stock, imagenUrl: payload.imagenUrl ?? "", activo: true, categoriaNombre: "" };
}

export async function adminActualizarProducto(id: number, payload: ProductoActualizarPayload): Promise<Producto> {
  if (!USE_MOCK) {
    return http.put<Producto>(`/api/productos/${id}`, payload);
  }
  await sleep(200);
  return { id, nombre: "", descripcion: payload.descripcion ?? "", precio: payload.precio ?? 0, stock: payload.stock ?? 0, imagenUrl: payload.imagenUrl ?? "", activo: true, categoriaNombre: "" };
}

export async function adminEliminarProducto(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.delete(`/api/productos/${id}`);
    return;
  }
  await sleep(150);
}

export async function adminReactivarProducto(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.patch(`/api/productos/${id}/reactivar`);
    return;
  }
  await sleep(150);
}
export type PartidoCapacidad = {
  partidoId: number;
  entradasReservadas: number;
  entradasPagadas: number;
  capacidadTotal: number;
};

export async function adminGetPartidos(): Promise<Match[]> {
  if (!USE_MOCK) {
    return http.get<Match[]>("/api/partidos");
  }
  await sleep(150);
  return [...mockDb.matches];
}

export async function adminGetPartidosEnVivo(): Promise<Match[]> {
  if (!USE_MOCK) {
    return http.get<Match[]>("/api/partidos/envivo");
  }
  await sleep(150);
  return mockDb.matches.filter((m) => m.status === "LIVE");
}

export async function adminGetPartidosPorFecha(fecha: string): Promise<Match[]> {
  if (!USE_MOCK) {
    return http.get<Match[]>(`/api/partidos/fecha/${fecha}`);
  }
  await sleep(150);
  return mockDb.matches.filter((m) => m.startTimeISO.startsWith(fecha));
}

export async function adminSincronizarPartidos(liga: number, temporada: number, fecha: string): Promise<number> {
  if (!USE_MOCK) {
    return http.get<number>(`/api/partidos/sincronizar/${liga}/${temporada}/${fecha}`);
  }
  await sleep(300);
  return 0;
}

export async function adminGetCapacidadPartidos(): Promise<PartidoCapacidad[]> {
  if (!USE_MOCK) {
    return http.get<PartidoCapacidad[]>("/api/entradas/partidos");
  }
  await sleep(150);
  return [];
}
export type Apuesta = {
  id: number;
  nombre: string;
  estado: string;
  codigoInvitacion: string;
  fechaCierre?: string;
  creadoPor: number;
};

export async function adminGetApuestas(): Promise<Apuesta[]> {
  if (!USE_MOCK) {
    return http.get<Apuesta[]>("/api/apuestas/todas");
  }
  await sleep(150);
  return [];
}

export async function adminCerrarApuesta(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.post(`/api/apuestas/cerrar/${id}`, {});
    return;
  }
  await sleep(150);
}

export async function adminEliminarApuesta(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.delete(`/api/apuestas/${id}`);
    return;
  }
  await sleep(150);
}

export async function adminForzarPuntos(id: number): Promise<void> {
  if (!USE_MOCK) {
    await http.get(`/api/apuestas/puntos/${id}`);
    return;
  }
  await sleep(150);
}
export type NotificacionRequest = {
  tipo: string;
  titulo: string;
  mensaje: string;
  canal: string;
  usuarioId?: number;
};

export type NotificacionMasivaRequest = {
  tipo: string;
  titulo: string;
  mensaje: string;
  canal: string;
  usuarioIds?: number[];
};

export async function adminEnviarNotificacion(payload: NotificacionRequest): Promise<void> {
  if (!USE_MOCK) {
    await http.post("/api/notificaciones/enviar", payload);
    return;
  }
  await sleep(200);
}

export async function adminEnviarMasiva(payload: NotificacionMasivaRequest): Promise<void> {
  if (!USE_MOCK) {
    await http.post("/api/notificaciones/masiva", payload);
    return;
  }
  await sleep(200);
}

export async function adminNotificarPorPartido(
  partidoId: number,
  payload: Omit<NotificacionRequest, "usuarioId">
): Promise<void> {
  if (!USE_MOCK) {
    await http.post(`/api/notificaciones/partido/${partidoId}`, payload);
    return;
  }
  await sleep(200);
}