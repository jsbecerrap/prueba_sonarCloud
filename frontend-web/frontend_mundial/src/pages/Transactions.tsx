import { useCallback, useEffect, useState } from "react";
import { Alert, Button, Chip, Paper, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getMyPaymentTxs } from "../api/paymentsApi";
import { useApp } from "../context/AppContext";
import type { PaymentTx, PaymentTxStatus } from "../types/paymentTx";

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

function formatPrecio(value: number, currency: string) {
  return `$${value.toLocaleString("es-CO")} ${currency}`;
}

export default function Transactions() {
  const { user } = useApp();
  const navigate = useNavigate();
  const [txs, setTxs] = useState<PaymentTx[]>([]);
  const [loading, setLoading] = useState(true);

  const refresh = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    const data = await getMyPaymentTxs(user.id);
    setTxs(data ?? []);
    setLoading(false);
  }, [user]);

  useEffect(() => { void refresh(); }, [refresh]);

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

      {loading ? (
        <Typography color="text.secondary">Cargando...</Typography>
      ) : txs.length === 0 ? (
        <Paper sx={{ p: 3 }}>
          <Typography color="text.secondary">No hay transacciones registradas.</Typography>
        </Paper>
      ) : (
        <Stack spacing={1.5}>
          {txs.map((tx) => (
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