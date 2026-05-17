import { useEffect, useRef, useState } from "react";
import {
  Alert,
  Button,
  Chip,
  Paper,
  Stack,
  ToggleButton,
  ToggleButtonGroup,
  Typography,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getMyPaymentTxs } from "../api/paymentsApi";
import { useApp } from "../context/AppContext";
import type { PaymentTx, PaymentTxKind, PaymentTxStatus } from "../types/paymentTx";

const statusLabels: Record<PaymentTxStatus, string> = {
  PENDING: "Pendiente",
  SUCCEEDED: "Aprobado",
  FAILED: "Fallido",
  REFUNDED: "Reembolsado",
};

const statusColors: Record<PaymentTxStatus, "default" | "success" | "error" | "warning"> = {
  PENDING: "warning",
  SUCCEEDED: "success",
  FAILED: "error",
  REFUNDED: "default",
};

const KIND_LABELS: Record<PaymentTxKind, string> = {
  TICKET: "Entradas",
  ORDEN: "Souvenirs",
  COINS: "Monedas",
};

const ALL_KINDS: PaymentTxKind[] = ["TICKET", "ORDEN", "COINS"];

function formatPrecio(value: number, currency: string) {
  return `$${value.toLocaleString("es-CO")} ${currency}`;
}

export default function Transactions() {
  const { user, authLoading } = useApp();
  const navigate = useNavigate();
  const [txs, setTxs] = useState<PaymentTx[]>([]);
  const [loading, setLoading] = useState(true);
  const [filtros, setFiltros] = useState<PaymentTxKind[]>(["TICKET", "ORDEN", "COINS"]);
  const fetchedRef = useRef(false);

  useEffect(() => {
    if (authLoading || !user?.id) return;
    if (fetchedRef.current) return;
    fetchedRef.current = true;
    setLoading(true);
    getMyPaymentTxs(user.id)
      .then((data) => setTxs(data ?? []))
      .finally(() => setLoading(false));
  }, [user?.id, authLoading]);

  const handleFiltros = (_: React.MouseEvent<HTMLElement>, nuevos: PaymentTxKind[]) => {
    if (nuevos.length === 0) return; // no permitir deseleccionar todos
    setFiltros(nuevos);
  };

  const txsFiltradas = txs.filter((tx) => filtros.includes(tx.kind));

  // Mostrar solo los tipos que realmente existen en los datos
  const kindsDisponibles = ALL_KINDS.filter((k) => txs.some((tx) => tx.kind === k));

  if (!user) return <Alert severity="warning">Debes iniciar sesión.</Alert>;

  return (
    <Stack spacing={2}>
      <Stack direction="row" justifyContent="space-between" alignItems="center">
        <Typography variant="h5">Historial de transacciones</Typography>
        <Button variant="outlined" size="small" onClick={() => navigate("/payments")}>
          Métodos de pago
        </Button>
      </Stack>

      <Alert severity="info">
        Aquí puedes ver todas tus transacciones de entradas y souvenirs.
      </Alert>

      {/* Filtros por tipo */}
      {!loading && kindsDisponibles.length > 1 && (
        <ToggleButtonGroup
          value={filtros}
          onChange={handleFiltros}
          size="small"
          color="primary"
        >
          {kindsDisponibles.map((kind) => (
            <ToggleButton key={kind} value={kind}>
              {KIND_LABELS[kind]}
            </ToggleButton>
          ))}
        </ToggleButtonGroup>
      )}

      {loading ? (
        <Typography color="text.secondary">Cargando...</Typography>
      ) : txs.length === 0 ? (
        <Paper sx={{ p: 3 }}>
          <Typography color="text.secondary">No hay transacciones registradas.</Typography>
        </Paper>
      ) : txsFiltradas.length === 0 ? (
        <Paper sx={{ p: 3 }}>
          <Typography color="text.secondary">
            Debes seleccionar al menos un tipo de transacción.
          </Typography>
        </Paper>
      ) : (
        <Stack spacing={1.5}>
          {txsFiltradas.map((tx) => (
            <Paper key={tx.id} variant="outlined" sx={{ p: 2 }}>
              <Stack direction={{ xs: "column", md: "row" }} justifyContent="space-between" spacing={2}>
                <Stack spacing={0.5}>
                  <Stack direction="row" spacing={1} alignItems="center">
                    <Typography sx={{ fontWeight: 800 }}>
                      {tx.kind === "TICKET" ? "Entrada" : tx.kind === "ORDEN" ? "Souvenir" : "Monedas"}
                    </Typography>
                    <Chip
                      label={statusLabels[tx.status]}
                      size="small"
                      color={statusColors[tx.status]}
                    />
                  </Stack>

                  {tx.kind === "TICKET" && (
                    <Typography color="text.secondary">
                      {tx.seleccionLocal} vs {tx.seleccionVisitante}
                      {tx.ronda ? ` · ${tx.ronda}` : ""}
                      {tx.estadio ? ` · ${tx.estadio}` : ""}
                      {tx.cantidadEntradas ? ` · ${tx.cantidadEntradas} entrada(s)` : ""}
                    </Typography>
                  )}

                  {tx.kind === "ORDEN" && tx.items && tx.items.length > 0 && (
                    <Stack spacing={0.3}>
                      {tx.items.map((item, i) => (
                        <Typography key={i} color="text.secondary" variant="body2">
                          {item.categoriaNombre ? `[${item.categoriaNombre}] ` : ""}
                          {item.productoNombre} x{item.cantidad} — ${item.subtotal.toLocaleString()}
                        </Typography>
                      ))}
                    </Stack>
                  )}

                  <Typography color="text.secondary">
                    {formatPrecio(tx.amount, tx.currency)}
                    {tx.metodoPagoLabel ? ` · ${tx.metodoPagoLabel}` : ""}
                  </Typography>

                  <Typography variant="caption" color="text.secondary">
                    Creada: {new Date(tx.createdAt).toLocaleString()}
                    {tx.confirmedAt ? ` · Pagada: ${new Date(tx.confirmedAt).toLocaleString()}` : ""}
                    {tx.providerRef ? ` · Ref: ${tx.providerRef}` : ""}
                  </Typography>
                </Stack>
              </Stack>
            </Paper>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
