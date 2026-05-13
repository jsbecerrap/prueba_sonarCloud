export type PaymentTxStatus =
  | "PENDING"
  | "SUCCEEDED"
  | "FAILED"
  | "REFUNDED";

export type PaymentProvider =
  | "MOCK_STRIPE"
  | "MOCK_WIREMOCK";

export type PaymentTxKind =
  | "TICKET"
  | "COINS"
  | "ORDEN";

export type PaymentTxItem = {
  productoNombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  categoriaNombre?: string;
};

export type PaymentTx = {
  id: string;
  userId: string;
  kind: PaymentTxKind;

  
  ticketId?: string;
  seleccionLocal?: string;
  seleccionVisitante?: string;
  ronda?: string;
  estadio?: string;
  fechaPartido?: string;
  cantidadEntradas?: number;

  
  items?: PaymentTxItem[];

  coins?: number;
  paymentMethodId: string;
  amount: number;
  currency: "COP" | "USD";
  status: PaymentTxStatus;
  createdAt: string;
  confirmedAt?: string;
  refundAt?: string;
  provider: PaymentProvider;
  providerRef: string;
  failReason?: string;
  metodoPagoLabel?: string;
};