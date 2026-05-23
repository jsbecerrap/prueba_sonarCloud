// ── Usuarios ──────────────────────────────────────────────────────────────────
export type UsuarioEventType =
  | "USUARIO_REGISTRADO"
  | "USUARIO_ELIMINADO"
  | "USUARIO_ACTUALIZADO"
  | "AUTH_LOGIN"
  | "AUTH_LOGOUT"
  | "USER_REGISTERED";

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
  | "ENTRADA_TRANSFERIDA"
  | "TICKET_RESERVED"
  | "TICKET_CANCELLED";

// ── Órdenes / Tienda ──────────────────────────────────────────────────────────
export type OrdenEventType =
  | "ORDEN_PAGADA"
  | "ORDEN_CANCELADA"
  | "ITEM_AGREGADO_CARRITO";

// ── Productos ─────────────────────────────────────────────────────────────────
export type ProductoEventType =
  | "PRODUCTO_CREADO"
  | "PRODUCTO_ACTUALIZADO"
  | "PRODUCTO_DESACTIVADO";

// ── Categorías ────────────────────────────────────────────────────────────────
export type CategoriaEventType =
  | "CATEGORIA_CREADA"
  | "CATEGORIA_ACTUALIZADA"
  | "CATEGORIA_DESACTIVADA"
  | "CATEGORIA_REACTIVADA";

// ── Métodos de pago ───────────────────────────────────────────────────────────
export type PagoEventType =
  | "METODO_PAGO_AGREGADO"
  | "METODO_PAGO_ELIMINADO"
  | "PAYMENT_CREATED"
  | "PAYMENT_FAILED"
  | "PAYMENT_CONFIRMED"
  | "PAYMENT_REFUNDED";
// ── Partidos ──────────────────────────────────────────────────────────────────
export type PartidoEventType =
  | "PARTIDO_RESULTADO_ACTUALIZADO"
  | "PARTIDOS_SINCRONIZADOS"
  | "MATCH_STATUS_CHANGED"
  | "MATCH_CREATED"
  | "MATCH_RESULT_PUBLISHED";

// ── Notificaciones (operador) ─────────────────────────────────────────────────
export type NotificacionEventType =
  | "NOTIFICACION_MASIVA"
  | "NOTIFICACION_POR_PARTIDO";

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
  | NotificacionEventType;

// ── Agrupación por categoría ──────────────────────────────────────────────────
export const EVENT_GROUPS: Record<string, SystemEventType[]> = {
  Usuarios:       ["USUARIO_REGISTRADO", "USUARIO_ELIMINADO", "USUARIO_ACTUALIZADO"],
  Pollas:         ["APUESTA_CREADA", "APUESTA_UNIRSE", "APUESTA_FINALIZADA", "PRONOSTICO_REGISTRADO", "PRONOSTICO_EDITADO", "PRONOSTICO_ELIMINADO"],
  Entradas:       ["ENTRADA_RESERVADA", "ENTRADA_PAGADA", "ENTRADA_CANCELADA", "ENTRADA_PAGO_FALLIDO", "ENTRADA_REEMBOLSADA", "ENTRADA_REEMBOLSO_FALLIDO", "ENTRADA_EXPIRADA", "ENTRADA_TRANSFERIDA"],
  Órdenes:        ["ORDEN_PAGADA", "ORDEN_CANCELADA", "ITEM_AGREGADO_CARRITO"],
  Productos:      ["PRODUCTO_CREADO", "PRODUCTO_ACTUALIZADO", "PRODUCTO_DESACTIVADO"],
  Categorías:     ["CATEGORIA_CREADA", "CATEGORIA_ACTUALIZADA", "CATEGORIA_DESACTIVADA", "CATEGORIA_REACTIVADA"],
  Pagos:          ["METODO_PAGO_AGREGADO", "METODO_PAGO_ELIMINADO"],
  Partidos:       ["PARTIDO_RESULTADO_ACTUALIZADO", "PARTIDOS_SINCRONIZADOS"],
  Notificaciones: ["NOTIFICACION_MASIVA", "NOTIFICACION_POR_PARTIDO"],
};

// ── Labels en español ─────────────────────────────────────────────────────────
export const EVENT_LABELS: Record<SystemEventType, string> = {
  USUARIO_REGISTRADO:            "Usuario registrado",
  USUARIO_ELIMINADO:             "Usuario eliminado",
  USUARIO_ACTUALIZADO:           "Usuario actualizado",
  APUESTA_CREADA:                "Polla creada",
  APUESTA_UNIRSE:                "Unirse a polla",
  APUESTA_FINALIZADA:            "Polla finalizada",
  PRONOSTICO_REGISTRADO:         "Pronóstico registrado",
  PRONOSTICO_EDITADO:            "Pronóstico editado",
  PRONOSTICO_ELIMINADO:          "Pronóstico eliminado",
  ENTRADA_RESERVADA:             "Entrada reservada",
  ENTRADA_PAGADA:                "Entrada pagada",
  ENTRADA_CANCELADA:             "Entrada cancelada",
  ENTRADA_PAGO_FALLIDO:          "Pago de entrada fallido",
  ENTRADA_REEMBOLSADA:           "Entrada reembolsada",
  ENTRADA_REEMBOLSO_FALLIDO:     "Reembolso fallido",
  ENTRADA_EXPIRADA:              "Reserva expirada",
  ENTRADA_TRANSFERIDA:           "Entrada transferida",
  ORDEN_PAGADA:                  "Orden pagada",
  ORDEN_CANCELADA:               "Orden cancelada",
  ITEM_AGREGADO_CARRITO:         "Ítem agregado al carrito",
  PRODUCTO_CREADO:               "Producto creado",
  PRODUCTO_ACTUALIZADO:          "Producto actualizado",
  PRODUCTO_DESACTIVADO:          "Producto desactivado",
  CATEGORIA_CREADA:              "Categoría creada",
  CATEGORIA_ACTUALIZADA:         "Categoría actualizada",
  CATEGORIA_DESACTIVADA:         "Categoría desactivada",
  CATEGORIA_REACTIVADA:          "Categoría reactivada",
  METODO_PAGO_AGREGADO:          "Método de pago agregado",
  METODO_PAGO_ELIMINADO:         "Método de pago eliminado",
  PARTIDO_RESULTADO_ACTUALIZADO: "Resultado de partido actualizado",
  PARTIDOS_SINCRONIZADOS:        "Partidos sincronizados",
NOTIFICACION_MASIVA:           "Notificación masiva",
  NOTIFICACION_POR_PARTIDO:      "Notificación por partido",
  AUTH_LOGIN:                    "Inicio de sesión",
  AUTH_LOGOUT:                   "Cierre de sesión",
  USER_REGISTERED:               "Usuario registrado (auth)",
  TICKET_RESERVED:               "Entrada reservada",
  TICKET_CANCELLED:              "Entrada cancelada",
  PAYMENT_CREATED:               "Pago creado",
  PAYMENT_FAILED:                "Pago fallido",
  PAYMENT_CONFIRMED:             "Pago confirmado",
  PAYMENT_REFUNDED:              "Pago reembolsado",
  MATCH_STATUS_CHANGED:          "Estado de partido actualizado",
  MATCH_CREATED:                 "Partido creado",
  MATCH_RESULT_PUBLISHED:        "Resultado publicado",
};

// ── Modelo del evento ─────────────────────────────────────────────────────────
export type SystemEvent = {
  id: string;
  tipo: SystemEventType | string;
  descripcion: string;
  fecha: string;
  idCorrelacion?: string;
  entidadCorrelacion?: string;
  usuarioId?: number;
};

// ── Helper ────────────────────────────────────────────────────────────────────
export function getEventLabel(tipo: string): string {
  return EVENT_LABELS[tipo as SystemEventType] ?? tipo;
}