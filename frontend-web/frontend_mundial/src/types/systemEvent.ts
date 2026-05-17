// ─────────────────────────────────────────────────────────────────────────────
// systemEvent.ts
// Tipos de eventos de auditoría — sincronizados con el backend y la BD real
// ─────────────────────────────────────────────────────────────────────────────

// ── Usuarios ──────────────────────────────────────────────────────────────────
export type UsuarioEventType =
  | "USUARIO_REGISTRADO"
  | "USUARIO_ELIMINADO"
  | "USUARIO_ACTUALIZADO"
  | "LOGIN"
  | "AUTH";

// ── Pollas / Apuestas ─────────────────────────────────────────────────────────
export type ApuestaEventType =
  | "APUESTA_CREADA"
  | "APUESTA_UNIRSE"
  | "APUESTA_FINALIZADA"
  | "PRONOSTICO_REGISTRADO"
  | "PRONOSTICO_EDITADO"
  | "PRONOSTICO_ELIMINADO";

// ── Entradas ──────────────────────────────────────────────────────────────────
export type EntradaEventType =
  | "ENTRADA_RESERVADA"
  | "ENTRADA_PAGADA"
  | "ENTRADA_CANCELADA"
  | "ENTRADA_PAGO_FALLIDO"
  | "ENTRADA_REEMBOLSADA"
  | "ENTRADA_REEMBOLSO_FALLIDO"
  | "ENTRADA_EXPIRADA"
  | "ENTRADA_TRANSFERIDA";

// ── Órdenes / Tienda ──────────────────────────────────────────────────────────
export type OrdenEventType =
  | "ORDEN_PAGADA"
  | "ORDEN_CANCELADA"
  | "ITEM_AGREGADO_CARRITO";

// ── Productos y Categorías ────────────────────────────────────────────────────
export type ProductoEventType =
  | "PRODUCTO_CREADO"
  | "PRODUCTO_ACTUALIZADO"
  | "PRODUCTO_DESACTIVADO";

export type CategoriaEventType =
  | "CATEGORIA_CREADA"
  | "CATEGORIA_ACTUALIZADA"
  | "CATEGORIA_DESACTIVADA"
  | "CATEGORIA_REACTIVADA";

// ── Pagos ─────────────────────────────────────────────────────────────────────
export type PagoEventType =
  | "METODO_PAGO_AGREGADO"
  | "METODO_PAGO_ELIMINADO";

// ── Partidos ──────────────────────────────────────────────────────────────────
export type PartidoEventType =
  | "PARTIDO_RESULTADO_ACTUALIZADO"
  | "PARTIDOS_SINCRONIZADOS";

// ── Notificaciones / Push ─────────────────────────────────────────────────────
// Estos tipos vienen de la BD real (no están en el código del backend actual,
// pero el backend los registra en producción).
export type NotificacionEventType =
  | "NOTIFICACION_MASIVA"
  | "NOTIFICACION_PERFIL_ACTUALIZADO"
  | "NOTIFICACION_POR_PARTIDO"
  | "NOTIFICACION_PUNTOS_CALCULADOS"
  | "NOTIFICACION_REGISTRO"
  | "PUSH_FCM_EXITOSO"
  | "PUSH_FCM_FALLIDO";

// ── Sistema ───────────────────────────────────────────────────────────────────
export type SistemaEventType = "SISTEMA";

// ── Tipo union completo ───────────────────────────────────────────────────────
export type SystemEventType =
  | UsuarioEventType
  | ApuestaEventType
  | EntradaEventType
  | OrdenEventType
  | ProductoEventType
  | CategoriaEventType
  | PagoEventType
  | PartidoEventType
  | NotificacionEventType
  | SistemaEventType;

// ── Agrupación por categoría (útil para el filtro del panel admin) ─────────────
export const EVENT_GROUPS: Record<string, SystemEventType[]> = {
  Usuarios:       ["USUARIO_REGISTRADO", "USUARIO_ELIMINADO", "USUARIO_ACTUALIZADO", "LOGIN", "AUTH"],
  Pollas:         ["APUESTA_CREADA", "APUESTA_UNIRSE", "APUESTA_FINALIZADA", "PRONOSTICO_REGISTRADO", "PRONOSTICO_EDITADO", "PRONOSTICO_ELIMINADO"],
  Entradas:       ["ENTRADA_RESERVADA", "ENTRADA_PAGADA", "ENTRADA_CANCELADA", "ENTRADA_PAGO_FALLIDO", "ENTRADA_REEMBOLSADA", "ENTRADA_REEMBOLSO_FALLIDO", "ENTRADA_EXPIRADA", "ENTRADA_TRANSFERIDA"],
  Órdenes:        ["ORDEN_PAGADA", "ORDEN_CANCELADA", "ITEM_AGREGADO_CARRITO"],
  Productos:      ["PRODUCTO_CREADO", "PRODUCTO_ACTUALIZADO", "PRODUCTO_DESACTIVADO"],
  Categorías:     ["CATEGORIA_CREADA", "CATEGORIA_ACTUALIZADA", "CATEGORIA_DESACTIVADA", "CATEGORIA_REACTIVADA"],
  Pagos:          ["METODO_PAGO_AGREGADO", "METODO_PAGO_ELIMINADO"],
  Partidos:       ["PARTIDO_RESULTADO_ACTUALIZADO", "PARTIDOS_SINCRONIZADOS"],
  Notificaciones: ["NOTIFICACION_MASIVA", "NOTIFICACION_PERFIL_ACTUALIZADO", "NOTIFICACION_POR_PARTIDO", "NOTIFICACION_PUNTOS_CALCULADOS", "NOTIFICACION_REGISTRO", "PUSH_FCM_EXITOSO", "PUSH_FCM_FALLIDO"],
  Sistema:        ["SISTEMA"],
};

// ── Labels en español para el panel admin ─────────────────────────────────────
export const EVENT_LABELS: Record<SystemEventType, string> = {
  // Usuarios
  USUARIO_REGISTRADO:            "Usuario registrado",
  USUARIO_ELIMINADO:             "Usuario eliminado",
  USUARIO_ACTUALIZADO:           "Usuario actualizado",
  LOGIN:                         "Inicio de sesión",
  AUTH:                          "Autenticación",

  // Pollas
  APUESTA_CREADA:                "Polla creada",
  APUESTA_UNIRSE:                "Unirse a polla",
  APUESTA_FINALIZADA:            "Polla finalizada",
  PRONOSTICO_REGISTRADO:         "Pronóstico registrado",
  PRONOSTICO_EDITADO:            "Pronóstico editado",
  PRONOSTICO_ELIMINADO:          "Pronóstico eliminado",

  // Entradas
  ENTRADA_RESERVADA:             "Entrada reservada",
  ENTRADA_PAGADA:                "Entrada pagada",
  ENTRADA_CANCELADA:             "Entrada cancelada",
  ENTRADA_PAGO_FALLIDO:          "Pago de entrada fallido",
  ENTRADA_REEMBOLSADA:           "Entrada reembolsada",
  ENTRADA_REEMBOLSO_FALLIDO:     "Reembolso fallido",
  ENTRADA_EXPIRADA:              "Reserva expirada",
  ENTRADA_TRANSFERIDA:           "Entrada transferida",

  // Órdenes
  ORDEN_PAGADA:                  "Orden pagada",
  ORDEN_CANCELADA:               "Orden cancelada",
  ITEM_AGREGADO_CARRITO:         "Ítem agregado al carrito",

  // Productos
  PRODUCTO_CREADO:               "Producto creado",
  PRODUCTO_ACTUALIZADO:          "Producto actualizado",
  PRODUCTO_DESACTIVADO:          "Producto desactivado",

  // Categorías
  CATEGORIA_CREADA:              "Categoría creada",
  CATEGORIA_ACTUALIZADA:         "Categoría actualizada",
  CATEGORIA_DESACTIVADA:         "Categoría desactivada",
  CATEGORIA_REACTIVADA:          "Categoría reactivada",

  // Pagos
  METODO_PAGO_AGREGADO:          "Método de pago agregado",
  METODO_PAGO_ELIMINADO:         "Método de pago eliminado",

  // Partidos
  PARTIDO_RESULTADO_ACTUALIZADO: "Resultado de partido actualizado",
  PARTIDOS_SINCRONIZADOS:        "Partidos sincronizados",

  // Notificaciones / Push
  NOTIFICACION_MASIVA:           "Notificación masiva",
  NOTIFICACION_PERFIL_ACTUALIZADO:"Notif. perfil actualizado",
  NOTIFICACION_POR_PARTIDO:      "Notif. por partido",
  NOTIFICACION_PUNTOS_CALCULADOS:"Notif. puntos calculados",
  NOTIFICACION_REGISTRO:         "Notif. registro de usuario",
  PUSH_FCM_EXITOSO:              "Push enviado (FCM)",
  PUSH_FCM_FALLIDO:              "Push fallido (FCM)",

  // Sistema
  SISTEMA:                       "Evento de sistema",
};

// ── Modelo del evento ─────────────────────────────────────────────────────────
export type SystemEvent = {
  id: string;
  tipo: SystemEventType | string; // string como fallback para tipos futuros no mapeados
  descripcion: string;
  fecha: string;               // ISO 8601
  idCorrelacion?: string;
  entidadCorrelacion?: string;
  usuarioId?: number;
};

// ── Helper: label seguro (nunca undefined) ────────────────────────────────────
export function getEventLabel(tipo: string): string {
  return EVENT_LABELS[tipo as SystemEventType] ?? tipo;
}