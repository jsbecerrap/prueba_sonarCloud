import { http } from "./http";

export interface ReportesStats {
  totalUsuarios: number;
  totalPartidos: number;
  totalTransacciones: number;
  usuariosActivos: number;
}

export interface ProductoMasVendido {
  productoId: number;
  nombre: string;
  categoria: string;
  cantidadVendida: number;
  ingresoTotal: number;
}

export interface VentasPorCategoria {
  categoria: string;
  cantidadVendida: number;
  ingresoTotal: number;
}

export interface ReportesCompras {
  ingresoTotal: number;
  totalOrdenes: number;
  totalEntradasVendidas: number;
  productosMasVendidos: ProductoMasVendido[];
  ventasPorCategoria: VentasPorCategoria[];
}

export interface PartidoMasApostado {
  partidoId: number;
  local: string;
  visitante: string;
  ronda: string;
  totalPronosticos: number;
}

export interface PollaRanking {
  apuestaId: number;
  nombre: string;
  estado: string;
  totalParticipantes: number;
}

export interface IngresoMetodoPago {
  tipo: string;
  totalOrdenes: number;
  ingresoTotal: number;
}

export interface EntradaPorPartido {
  partidoId: number;
  local: string;
  visitante: string;
  ronda: string;
  estadio: string;
  cantidadVendida: number;
  ingresoTotal: number;
}

export interface TopUsuarioSouvenir {
  usuarioId: number;
  nombre: string;
  apellido: string;
  correo: string;
  totalOrdenes: number;
  totalGastado: number;
}

export interface TopUsuarioEntrada {
  usuarioId: number;
  nombre: string;
  apellido: string;
  correo: string;
  totalEntradas: number;
  totalGastado: number;
}

export async function getEstadisticasGenerales(): Promise<ReportesStats> {
  return http.get<ReportesStats>("/api/reportes/estadisticas-generales");
}

export async function getReportesCompras(): Promise<ReportesCompras> {
  return http.get<ReportesCompras>("/api/reportes/compras");
}

export async function getPartidosMasApostados(): Promise<PartidoMasApostado[]> {
  return http.get<PartidoMasApostado[]>("/api/reportes/partidos-apostados");
}

export async function getPollaRanking(): Promise<PollaRanking[]> {
  return http.get<PollaRanking[]>("/api/reportes/pollas");
}

export async function getIngresosPorMetodoPago(): Promise<IngresoMetodoPago[]> {
  return http.get<IngresoMetodoPago[]>("/api/reportes/metodos-pago");
}

export async function getEntradasPorPartido(): Promise<EntradaPorPartido[]> {
  return http.get<EntradaPorPartido[]>("/api/reportes/entradas-por-partido");
}

export async function getTopUsuariosSouvenir(size = 5): Promise<TopUsuarioSouvenir[]> {
  return http.get<TopUsuarioSouvenir[]>(`/api/reportes/top-souvenir?size=${size}`);
}

export async function getTopUsuariosEntrada(size = 5): Promise<TopUsuarioEntrada[]> {
  return http.get<TopUsuarioEntrada[]>(`/api/reportes/top-entradas?size=${size}`);
}