import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Alert, Box, Button, Chip, CircularProgress, Divider, Stack, Typography } from "@mui/material";
import { getTicketById } from "../api/ticketsApi";
import { useApp } from "../context/AppContext";
import type { Ticket } from "../types/ticket";

function formatPrecio(value: number) {
  return `$${value.toLocaleString("es-CO")} COP`;
}

function QRDecorativo({ value }: { value: string }) {
  // QR decorativo generado con patrón SVG basado en el valor
  const hash = value.split("").reduce((acc, c) => acc + c.charCodeAt(0), 0);
  const cells = 10;
  const size = 120;
  const cellSize = size / cells;

  const grid = Array.from({ length: cells }, (_, row) =>
    Array.from({ length: cells }, (_, col) => {
      const seed = (row * cells + col + hash) % 7;
      // esquinas fijas para parecer QR real
      if ((row < 3 && col < 3) || (row < 3 && col >= cells - 3) || (row >= cells - 3 && col < 3)) return true;
      return seed < 3;
    })
  );

  return (
    <Box
      sx={{
        p: 1.5,
        bgcolor: "white",
        borderRadius: 2,
        display: "inline-block",
        boxShadow: "0 4px 20px rgba(0,0,0,0.3)",
      }}
    >
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        {grid.map((row, r) =>
          row.map((filled, c) =>
            filled ? (
              <rect
                key={`${r}-${c}`}
                x={c * cellSize}
                y={r * cellSize}
                width={cellSize - 0.5}
                height={cellSize - 0.5}
                fill="#1a1a1a"
                rx={0.5}
              />
            ) : null
          )
        )}
      </svg>
    </Box>
  );
}

function AsientosVisual({ inicio, cantidad, fila, sector }: { inicio: number; cantidad: number; fila: string; sector: string }) {
  const asientos = Array.from({ length: cantidad }, (_, i) => inicio + i);
  return (
    <Stack spacing={1}>
      <Typography variant="caption" color="text.secondary" sx={{ textTransform: "uppercase", letterSpacing: 1 }}>
        Sector {sector} · Fila {fila} · {asientos.length === 1 ? `Asiento ${asientos[0]}` : `Asientos ${asientos[0]}–${asientos[asientos.length - 1]}`}
      </Typography>
      <Stack direction="row" spacing={0.75} flexWrap="wrap">
        {asientos.map((a) => (
          <Box
            key={a}
            sx={{
              width: 36,
              height: 36,
              borderRadius: 1,
              bgcolor: "rgba(100,220,120,0.15)",
              border: "1.5px solid rgba(100,220,120,0.5)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography sx={{ fontSize: 11, fontWeight: 700, color: "#64dc78" }}>{a}</Typography>
          </Box>
        ))}
      </Stack>
    </Stack>
  );
}

export default function EntradaDigital() {
  const { ticketId } = useParams<{ ticketId: string }>();
  const { user } = useApp();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user || !ticketId) return;
    getTicketById(user.id, ticketId)
      .then((data) => {
        if (!data) setError("No se encontró la entrada.");
        else setTicket(data);
      })
      .catch(() => setError("Error cargando la entrada."))
      .finally(() => setLoading(false));
  }, [user, ticketId]);

  if (loading) {
    return (
      <Stack alignItems="center" justifyContent="center" sx={{ minHeight: 300 }}>
        <CircularProgress />
      </Stack>
    );
  }

  if (error || !ticket) {
    return <Alert severity="error">{error || "Entrada no encontrada."}</Alert>;
  }

  const fechaFormateada = ticket.fecha
    ? new Date(ticket.fecha).toLocaleDateString("es-CO", { weekday: "long", year: "numeric", month: "long", day: "numeric" })
    : "—";

  const horaFormateada = ticket.fecha
    ? new Date(ticket.fecha).toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit" })
    : "—";

  const asientoInicio = ticket.asientoInicio ?? 1;
  const fila = ticket.fila ?? "C";
  const sector = ticket.sector ?? "Norte";

  const categoriaColor: Record<string, string> = {
    BARRA: "#64b464",
    GENERAL: "#ffb400",
    PALCO: "#b464ff",
  };
  const color = categoriaColor[ticket.categoria ?? "BARRA"] ?? "#64b464";

  return (
    <Stack spacing={3} sx={{ maxWidth: 600, mx: "auto" }}>
      <Stack direction="row" alignItems="center" spacing={1}>
        <Typography variant="h5" sx={{ fontWeight: 800 }}>
          Mi Entrada
        </Typography>
        <Chip
          label="✓ Pagada"
          size="small"
          sx={{ bgcolor: "rgba(100,220,120,0.15)", color: "#64dc78", fontWeight: 700, border: "1px solid rgba(100,220,120,0.3)" }}
        />
      </Stack>

      {/* Tarjeta principal tipo entrada física */}
      <Box
        sx={{
          borderRadius: 3,
          overflow: "hidden",
          border: "1px solid rgba(255,255,255,0.08)",
          background: "linear-gradient(135deg, rgba(255,255,255,0.05) 0%, rgba(255,255,255,0.02) 100%)",
          boxShadow: `0 8px 32px rgba(0,0,0,0.4), inset 0 1px 0 rgba(255,255,255,0.08)`,
        }}
      >
        {/* Header con color de categoría */}
        <Box sx={{ bgcolor: color, px: 3, py: 1.5 }}>
          <Typography sx={{ fontWeight: 900, fontSize: 11, letterSpacing: 2, color: "rgba(0,0,0,0.7)", textTransform: "uppercase" }}>
            Mundial 2026 · Entrada Oficial
          </Typography>
        </Box>

        {/* Contenido principal */}
        <Box sx={{ p: 3 }}>
          <Stack direction={{ xs: "column", sm: "row" }} justifyContent="space-between" spacing={3}>
            {/* Info del partido */}
            <Stack spacing={2} flex={1}>
              <Stack>
                <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                  Partido
                </Typography>
                <Typography sx={{ fontWeight: 900, fontSize: 20, lineHeight: 1.2 }}>
                  {ticket.seleccionLocal ?? "—"} <span style={{ color, fontWeight: 400 }}>vs</span> {ticket.seleccionVisitante ?? "—"}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={3}>
                <Stack>
                  <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                    Fecha
                  </Typography>
                  <Typography sx={{ fontWeight: 700, fontSize: 13 }}>{fechaFormateada}</Typography>
                </Stack>
                <Stack>
                  <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                    Hora
                  </Typography>
                  <Typography sx={{ fontWeight: 700, fontSize: 13 }}>{horaFormateada}</Typography>
                </Stack>
              </Stack>

              <Stack>
                <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                  Estadio
                </Typography>
                <Typography sx={{ fontWeight: 700, fontSize: 13 }}>{ticket.estadio ?? "—"}</Typography>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Stack>
                  <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                    Zona
                  </Typography>
                  <Chip
                    label={ticket.categoria ?? "—"}
                    size="small"
                    sx={{ bgcolor: `${color}22`, color, border: `1px solid ${color}55`, fontWeight: 700, width: "fit-content" }}
                  />
                </Stack>
                <Stack>
                  <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                    Cantidad
                  </Typography>
                  <Typography sx={{ fontWeight: 800, fontSize: 18, color }}>{ticket.quantity}</Typography>
                </Stack>
              </Stack>
            </Stack>

            {/* QR */}
            <Stack alignItems="center" justifyContent="center" spacing={1}>
              <QRDecorativo value={`${ticket.id}-${ticket.matchId}-${ticket.userId}`} />
             
            </Stack>
          </Stack>

          {/* Línea punteada separadora */}
          <Box sx={{ my: 2.5, borderTop: "1.5px dashed rgba(255,255,255,0.1)", position: "relative" }}>
            <Box sx={{
              position: "absolute", left: -24, top: -10, width: 20, height: 20,
              borderRadius: "50%", bgcolor: "background.default"
            }} />
            <Box sx={{
              position: "absolute", right: -24, top: -10, width: 20, height: 20,
              borderRadius: "50%", bgcolor: "background.default"
            }} />
          </Box>

          {/* Asientos */}
          <AsientosVisual
            inicio={asientoInicio}
            cantidad={ticket.quantity}
            fila={fila}
            sector={sector}
          />

          <Divider sx={{ my: 2, borderColor: "rgba(255,255,255,0.06)" }} />

          {/* Footer */}
          <Stack direction="row" justifyContent="space-between" alignItems="center">
            <Stack>
              <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                Total pagado
              </Typography>
              <Typography sx={{ fontWeight: 900, fontSize: 20, color }}>
                {formatPrecio(ticket.price ?? 0)}
              </Typography>
            </Stack>
            {ticket.paymentRef && (
              <Stack alignItems="flex-end">
                <Typography variant="caption" color="text.secondary" sx={{ letterSpacing: 1, textTransform: "uppercase" }}>
                  Ref. pago
                </Typography>
                <Typography variant="caption" sx={{ fontFamily: "monospace", fontSize: 10, color: "text.secondary" }}>
                  {ticket.paymentRef.slice(0, 20)}...
                </Typography>
              </Stack>
            )}
          </Stack>
        </Box>
      </Box>

      {/* Botones */}
      <Stack direction="row" spacing={1}>
        <Button variant="contained" onClick={() => navigate("/tickets")}>
          Mis entradas
        </Button>
        <Button variant="outlined" onClick={() => navigate("/home")}>
          Inicio
        </Button>
      </Stack>
    </Stack>
  );
}
