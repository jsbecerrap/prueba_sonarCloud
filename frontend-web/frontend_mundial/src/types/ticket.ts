export type TicketStatus =
  | "RESERVADA"
  | "PAGADA"
  | "CANCELADA"
  | "EXPIRADA"
  | "REEMBOLSADA"
  | "TRANSFERIDA";

export type Ticket = {
  id: string;
  userId: string;
  matchId: string;
  quantity: number;
  status: TicketStatus;
  createdAt: string;
  expiresAt?: string;
  paidAt?: string;
  refundedAt?: string;
  paymentRef?: string;
  categoria?: string;
  price?: number;
 seleccionLocal?: string;
  seleccionVisitante?: string;
  estadio?: string;
  fecha?: string;
  ronda?: string;
  sector?: string;
  fila?: string;
  asientoInicio?: number;
};