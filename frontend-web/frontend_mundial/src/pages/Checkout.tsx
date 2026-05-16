import { useEffect, useMemo, useState } from "react";
import {
  Alert, Avatar, Button, Chip, CircularProgress,
  Collapse, MenuItem, Paper, Stack, TextField, Typography,
} from "@mui/material";
import { useNavigate, useSearchParams } from "react-router-dom";

import {
  createCoinsPayment,
  createTicketPayment,
  getMyPaymentMethods,
} from "../api/paymentsApi";
import { getCarrito, confirmarOrden, type ItemOrdenResponse } from "../api/storeApi";
import { getTicketById } from "../api/ticketsApi";
import { useApp } from "../context/AppContext";
import type { PaymentMethod } from "../types/payment";
import type { Ticket } from "../types/ticket";

type CheckoutKind = "TICKET" | "COINS" | "ORDEN";
type Msg = { text: string; severity: "success" | "error" | "info" } | null;

function formatPrecio(value: number) {
  return `$${value.toLocaleString("es-CO")} COP`;
}

export default function Checkout() {
  const { user } = useApp();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const kind = (searchParams.get("type") ?? "TICKET").toUpperCase() as CheckoutKind;
  const ticketId = searchParams.get("ticketId") ?? "";
  const coins = Number(searchParams.get("coins") ?? "0");
  const amount = Number(searchParams.get("amount") ?? "0");
  const total = Number(searchParams.get("total") ?? "0");

  const [methods, setMethods] = useState<PaymentMethod[]>([]);
  const [methodId, setMethodId] = useState("");
  const [msg, setMsg] = useState<Msg>(null);
  const [loading, setLoading] = useState(false);
  const [cambiandoMetodo, setCambiandoMetodo] = useState(false);
  const [items, setItems] = useState<ItemOrdenResponse[]>([]);
  const [cargandoCarrito, setCargandoCarrito] = useState(false);
  const [ticket, setTicket] = useState<Ticket | null>(null);

  const title = useMemo(() => {
    if (kind === "TICKET") return "Pago de entrada";
    if (kind === "ORDEN") return "Confirmar pedido";
    return "Compra de monedas";
  }, [kind]);

  const defaultMethod = useMemo(
    () => methods.find((m) => m.id === methodId),
    [methods, methodId]
  );

  useEffect(() => {
    if (!user) return;
    getMyPaymentMethods(user.id).then((data) => {
      setMethods(data ?? []);
      setMethodId(data.find((m) => m.isDefault)?.id ?? data[0]?.id ?? "");
    });
  }, [user]);

  useEffect(() => {
    if (kind !== "ORDEN") return;
    setCargandoCarrito(true);
    getCarrito()
      .then((data) => setItems(data?.items ?? []))
      .catch(() => setItems([]))
      .finally(() => setCargandoCarrito(false));
  }, [kind]);

  useEffect(() => {
    if (kind !== "TICKET" || !user || !ticketId) return;
    getTicketById(user.id, ticketId).then((data) => setTicket(data));
  }, [kind, ticketId, user]);

  if (!user) return <Alert severity="warning">Debes iniciar sesión.</Alert>;

  const onCreate = async () => {
    if (!methodId) {
      setMsg({ text: "Selecciona un método de pago.", severity: "error" });
      return;
    }
    try {
      setLoading(true);
      setMsg(null);
     if (kind === "TICKET") {
        await createTicketPayment(user.id, ticketId, methodId, amount);
        navigate(`/entrada/${ticketId}`);
      } else if (kind === "ORDEN") {
        await confirmarOrden({ metodoPagoId: Number(methodId) });
        navigate("/store");
      } else {
        await createCoinsPayment(user.id, coins, methodId, amount);
        navigate("/payments");
      }
    } catch (e) {
      const message = e instanceof Error ? e.message : "Error procesando el pago.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack spacing={2}>
      <Typography variant="h5">{title}</Typography>

      {msg && <Alert severity={msg.severity}>{msg.text}</Alert>}

      {methods.length === 0 && (
        <Alert
          severity="warning"
          action={
            <Button color="inherit" size="small" onClick={() => navigate("/payments")}>
              Agregar
            </Button>
          }
        >
          No tienes métodos de pago. Agrega uno antes de continuar.
        </Alert>
      )}

      {/* RESUMEN */}
      <Paper sx={{ p: 2.5 }}>
        <Typography variant="h6" sx={{ mb: 1.5 }}>Resumen</Typography>

        {kind === "TICKET" && (
          <Stack spacing={1}>
            {ticket ? (
              <>
                <Stack direction="row" justifyContent="space-between">
                  <Typography color="text.secondary">Partido</Typography>
                  <Typography sx={{ fontWeight: 800 }}>
                    {(ticket as any).seleccionLocal} vs {(ticket as any).seleccionVisitante}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography color="text.secondary">Estadio</Typography>
                  <Typography>{(ticket as any).estadio ?? "—"}</Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography color="text.secondary">Fecha</Typography>
                  <Typography>
                    {(ticket as any).fecha
                      ? new Date((ticket as any).fecha).toLocaleDateString("es-CO")
                      : "—"}
                  </Typography>
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography color="text.secondary">Categoría</Typography>
                  <Chip label={ticket.categoria ?? "GENERAL"} size="small" />
                </Stack>
                <Stack direction="row" justifyContent="space-between">
                  <Typography color="text.secondary">Cantidad</Typography>
                  <Typography>{ticket.quantity}</Typography>
                </Stack>
                <Stack
                  direction="row"
                  justifyContent="space-between"
                  sx={{ pt: 1, borderTop: "1px solid rgba(255,255,255,0.1)" }}
                >
                  <Typography variant="h6">Total</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 900 }}>
                    {formatPrecio(amount)}
                  </Typography>
                </Stack>
              </>
            ) : (
              <CircularProgress size={24} />
            )}
          </Stack>
        )}

        {kind === "COINS" && (
          <Stack spacing={0.5}>
            <Typography color="text.secondary">
              Monedas: <strong>{coins}</strong>
            </Typography>
            <Typography color="text.secondary">
              Valor: <strong>{formatPrecio(amount)}</strong>
            </Typography>
          </Stack>
        )}

        {kind === "ORDEN" && (
          cargandoCarrito ? (
            <CircularProgress size={24} />
          ) : (
            <Stack spacing={1.5}>
              {items.map((item) => (
                <Stack key={item.id} direction="row" spacing={1.5} alignItems="center">
                  <Avatar
                    variant="rounded"
                    src={item.productoImagenUrl}
                    alt={item.productoNombre}
                    sx={{ width: 80, height: 80, borderRadius: 1 }}
                  />
                  <Stack spacing={0.25} flex={1}>
                    <Typography sx={{ fontWeight: 800 }}>{item.productoNombre}</Typography>
                    <Typography variant="body2" color="text.secondary">
  {item.especificacion ? `${item.especificacion} · ` : ""}
  {item.cantidad} x {formatPrecio(item.precioUnitario)}
</Typography>
                    {item.categoriaNombre && (
                      <Chip label={item.categoriaNombre} size="small" sx={{ alignSelf: "flex-start" }} />
                    )}
                  </Stack>
                  <Typography sx={{ fontWeight: 900 }}>
                    {formatPrecio(item.precioUnitario * item.cantidad)}
                  </Typography>
                </Stack>
              ))}
              <Stack
                direction="row"
                justifyContent="space-between"
                sx={{ pt: 1, borderTop: "1px solid rgba(255,255,255,0.1)" }}
              >
                <Typography variant="h6">Total</Typography>
                <Typography variant="h6" sx={{ fontWeight: 900 }}>
                  {formatPrecio(total)}
                </Typography>
              </Stack>
            </Stack>
          )
        )}
      </Paper>

      {/* MÉTODO DE PAGO */}
      <Paper sx={{ p: 2.5 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Stack spacing={0.25}>
            <Typography variant="body2" color="text.secondary">Método de pago</Typography>
            <Typography sx={{ fontWeight: 800 }}>
              {defaultMethod
                ? `${defaultMethod.label} · ${defaultMethod.details ?? defaultMethod.type}`
                : "Sin método"}
            </Typography>
          </Stack>
          <Button size="small" variant="outlined" onClick={() => setCambiandoMetodo((v) => !v)}>
            {cambiandoMetodo ? "Cerrar" : "Cambiar"}
          </Button>
        </Stack>

        <Collapse in={cambiandoMetodo}>
          <TextField
            select
            label="Selecciona otro método"
            value={methodId}
            onChange={(e) => { setMethodId(e.target.value); setCambiandoMetodo(false); }}
            fullWidth
            sx={{ mt: 2 }}
            disabled={loading}
          >
            {methods.map((m) => (
              <MenuItem key={m.id} value={m.id}>
                {m.label} · {m.details ?? m.type} {m.isDefault ? "· predeterminado" : ""}
              </MenuItem>
            ))}
          </TextField>
        </Collapse>
      </Paper>

      {/* ACCIONES */}
      <Stack direction={{ xs: "column", sm: "row" }} spacing={1}>
        <Button
          variant="contained"
          onClick={onCreate}
          disabled={loading || methods.length === 0}
        >
          {loading ? "Procesando..." : "Confirmar pago"}
        </Button>
        <Button
          variant="outlined"
          onClick={() => navigate(kind === "ORDEN" ? "/cart" : "/tickets")}
          disabled={loading}
        >
          Volver
        </Button>
      </Stack>
    </Stack>
  );
}