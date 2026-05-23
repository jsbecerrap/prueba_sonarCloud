import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import StadiumMap from "../components/StadiumMap";
import { getMatches } from "../api/matchesApi";
import { cancelTicket, getMyTickets, reserveTicket, transferTicket, getPartidosConCapacidad, markTicketAsRefunded, getCuposPorZona } from "../api/ticketsApi";
import type { PartidoCapacidad, CuposZona } from "../api/ticketsApi";
import { useApp } from "../context/AppContext";
import type { Match } from "../types/match";
import type { Ticket } from "../types/ticket";
import { validatePositiveNumber } from "../utils/validation";

type Msg = { text: string; severity: "success" | "error" | "info" } | null;



function statusLabel(status: Ticket["status"]) {
  const labels: Record<Ticket["status"], string> = {
    RESERVADA: "Reservada",
    PAGADA: "Pagada",
    CANCELADA: "Cancelada",
    EXPIRADA: "Expirada",
    REEMBOLSADA: "Reembolsada",
    TRANSFERIDA: "Transferida",
  };
  return labels[status];
}

export default function Tickets() {
  const { user } = useApp();
  const navigate = useNavigate();

  const [items, setItems] = useState<Ticket[]>([]);
  const [matches, setMatches] = useState<Match[]>([]);
  const [partidosCapacidad, setPartidosCapacidad] = useState<PartidoCapacidad[]>([]);
  const [selectedMatchId, setSelectedMatchId] = useState("");
  const [quantity, setQuantity] = useState(1);
 const [categoria, setCategoria] = useState<string>("BARRA");
 const [sector, setSector] = useState<string>("Norte");
  const [fila, setFila] = useState<string>("A");

  const [quantityError, setQuantityError] = useState("");
  const [msg, setMsg] = useState<Msg>(null);
  const [loading, setLoading] = useState(false);

  const [filterSeleccion, setFilterSeleccion] = useState("");
  const [filterCiudad, setFilterCiudad] = useState("");
  const [filterEstadio, setFilterEstadio] = useState("");

  const [transferDialogOpen, setTransferDialogOpen] = useState(false);
  const [transferTicketId, setTransferTicketId] = useState("");
 const [correoDestino, setCorreoDestino] = useState("");
const [cuposZona, setCuposZona] = useState<CuposZona[]>([]);

  const matchById = useMemo(() => new Map(matches.map((match) => [match.id, match])), [matches]);

 

  const selecciones = useMemo(() => {
    const set = new Set<string>();
    partidosCapacidad.forEach((p) => {
      if (p.local) set.add(p.local);
      if (p.visitante) set.add(p.visitante);
    });
    return Array.from(set).sort((a, b) => a.localeCompare(b));
  }, [partidosCapacidad]);

  const ciudades = useMemo(() => {
    const set = new Set<string>();
    partidosCapacidad.forEach((p) => {
      if (p.ciudad) set.add(p.ciudad);
    });
    return Array.from(set).sort((a, b) => a.localeCompare(b));
  }, [partidosCapacidad]);

  const estadios = useMemo(() => {
    const set = new Set<string>();
    partidosCapacidad.forEach((p) => {
      if (p.estadio) set.add(p.estadio);
    });
    return Array.from(set).sort((a, b) => a.localeCompare(b));
  }, [partidosCapacidad]);

 

  const filteredPartidos = useMemo(() => {
    return partidosCapacidad.filter((p) => {
      if (filterSeleccion && p.local !== filterSeleccion && p.visitante !== filterSeleccion) return false;
      if (filterCiudad && p.ciudad !== filterCiudad) return false;
      if (filterEstadio && p.estadio !== filterEstadio) return false;
      return true;
    });
  }, [partidosCapacidad, filterSeleccion, filterCiudad, filterEstadio]);
const partidoSeleccionado = useMemo(
  () => filteredPartidos.find((p) => String(p.id) === selectedMatchId) ?? null,
  [filteredPartidos, selectedMatchId]
);
const PRECIO_POR_RONDA: Record<string, number> = {
    "Group Stage - 1": 50000, "Group Stage - 2": 50000, "Group Stage - 3": 50000,
    "Round of 32": 80000, "Round of 16": 100000,
    "Quarter-finals": 150000, "Semi-finals": 200000,
    "3rd Place Final": 180000, "Final": 300000,
  };
  const SELECCIONES_TOP = new Set(["France","Spain","Argentina","England","Portugal","Brazil","Netherlands","Morocco","Belgium","Germany","Colombia"]);
 const MULTIPLICADOR: Record<string, number> = { BARRA: 1, GENERAL: 2, PALCO: 3.5, ESQUINA: 0.7 };

  const calcularPrecio = (ronda: string | undefined, cat: string) => {
    const base = PRECIO_POR_RONDA[ronda ?? ""] ?? 50000;
    const hayTop = partidoSeleccionado
      ? SELECCIONES_TOP.has(partidoSeleccionado.local) || SELECCIONES_TOP.has(partidoSeleccionado.visitante)
      : false;
    return base * (hayTop ? 1.5 : 1) * (MULTIPLICADOR[cat] ?? 1);
  };

 const formatPrecio = (v: number) => `$${v.toLocaleString("es-CO")} COP`;
const cuposDeZona = (zona: string) =>
  cuposZona.find((c) => c.zona === zona)?.disponibles ?? null;
  const refresh = useCallback(async () => {
    if (!user) return;
    const [ticketsData, matchesData, partidosData] = await Promise.all([
      getMyTickets(user.id),
      getMatches(),
      getPartidosConCapacidad(),
    ]);
    setItems(ticketsData ?? []);
    setMatches(matchesData ?? []);
    setPartidosCapacidad(partidosData ?? []);
  setSelectedMatchId((current) => {
  const id = current || String(partidosData[0]?.id) || "";
  if (id) void getCuposPorZona(id).then(setCuposZona);
  return id;
});
  }, [user]);

useEffect(() => {
  void refresh();
}, [refresh]);

useEffect(() => {
  if (!selectedMatchId) return;
  void getCuposPorZona(selectedMatchId).then(setCuposZona);
}, [selectedMatchId]);

const summary = useMemo(
  () => ({
    total: items.length,
    reserved: items.filter((t) => t.status === "RESERVADA").length,
    paid: items.filter((t) => t.status === "PAGADA").length,
    refunded: items.filter((t) => t.status === "REEMBOLSADA").length,
    cancelled: items.filter((t) => t.status === "CANCELADA").length,
    expired: items.filter((t) => t.status === "EXPIRADA").length,
    transferred: items.filter((t) => t.status === "TRANSFERIDA").length,
  }),
  [items]
);

  if (!user) {
    return (
      <Stack spacing={2}>
        <Typography variant="h5">Entradas</Typography>
        <Alert severity="warning">Debes iniciar sesión para ver tus entradas.</Alert>
      </Stack>
    );
  }

  const onReserve = async () => {
    const nextQuantityError = validatePositiveNumber(quantity, "La cantidad", 1, 4);
    setQuantityError(nextQuantityError);

    if (!selectedMatchId) {
      setMsg({ text: "Selecciona un partido.", severity: "error" });
      return;
    }

    if (nextQuantityError) return;

    try {
      setLoading(true);
      setMsg(null);
await reserveTicket(user.id, selectedMatchId, quantity, categoria, sector, fila);
      setMsg({ text: "Reserva creada. Tienes 15 minutos para pagarla.", severity: "success" });
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "No se pudo reservar la entrada.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  const onRefund = async (ticketId: string) => {
    if (!confirm("¿Seguro que quieres reembolsar esta entrada?")) return;
    try {
      setLoading(true);
      await markTicketAsRefunded(String(user.id), ticketId);
      setMsg({ text: "Entrada reembolsada correctamente.", severity: "success" });
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "Error reembolsando la entrada.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  const onCancel = async (ticketId: string) => {
    try {
      setLoading(true);
      const ok = await cancelTicket(user.id, ticketId);
      setMsg(
        ok
          ? { text: "Reserva cancelada.", severity: "success" }
          : { text: "No se pudo cancelar esta entrada.", severity: "error" }
      );
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "Error cancelando la entrada.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  const onOpenTransfer = (ticketId: string) => {
    setTransferTicketId(ticketId);
    setCorreoDestino("");
    setTransferDialogOpen(true);
  };

  const onTransfer = async () => {
    if (!correoDestino.trim()) {
      setMsg({ text: "Ingresa el correo del destinatario.", severity: "error" });
      return;
    }
    try {
      setLoading(true);
      setTransferDialogOpen(false);
      await transferTicket(user.id, transferTicketId, correoDestino.trim());
      setMsg({ text: "Entrada transferida correctamente.", severity: "success" });
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "Error transfiriendo la entrada.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack spacing={2}>
      <Typography variant="h5">Entradas</Typography>

      <Alert severity="info">
        Reserva entradas por partido, confirma el pago en sandbox y conserva evidencia de cada transacción.
      </Alert>

      {msg && <Alert severity={msg.severity}>{msg.text}</Alert>}

      <Paper sx={{ p: 2.5 }}>
        <Typography variant="h6">Nueva reserva</Typography>

        <Stack direction={{ xs: "column", md: "row" }} spacing={1.5} sx={{ mt: 2 }}>
          <TextField
            select
            label="Selección"
            value={filterSeleccion}
            onChange={(e) => { setFilterSeleccion(e.target.value); setSelectedMatchId(""); }}
            fullWidth
          >
            <MenuItem value="">Todas</MenuItem>
            {selecciones.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
          </TextField>
          <TextField
            select
            label="Ciudad"
            value={filterCiudad}
            onChange={(e) => { setFilterCiudad(e.target.value); setSelectedMatchId(""); }}
            fullWidth
          >
            <MenuItem value="">Todas</MenuItem>
            {ciudades.map((c) => <MenuItem key={c} value={c}>{c}</MenuItem>)}
          </TextField>
          <TextField
            select
            label="Estadio"
            value={filterEstadio}
            onChange={(e) => { setFilterEstadio(e.target.value); setSelectedMatchId(""); }}
            fullWidth
          >
            <MenuItem value="">Todos</MenuItem>
            {estadios.map((e) => <MenuItem key={e} value={e}>{e}</MenuItem>)}
          </TextField>
        </Stack>

        <Stack direction={{ xs: "column", md: "row" }} spacing={1.5} sx={{ mt: 1.5 }}>
          <TextField
            select
            label="Partido"
            value={selectedMatchId}
            onChange={(e) => setSelectedMatchId(e.target.value)}
            disabled={loading}
            fullWidth
          >
            {filteredPartidos.length === 0 && (
              <MenuItem value="" disabled>No hay partidos disponibles</MenuItem>
            )}
            {filteredPartidos.map((p) => (
              <MenuItem key={p.id} value={String(p.id)}>
                {p.local} vs {p.visitante} · {p.ciudad} · {p.capacidadDisponible.toLocaleString()} cupos
              </MenuItem>
            ))}
          </TextField>
          <TextField
            label="Cantidad"
            type="number"
            value={quantity}
            onChange={(e) => setQuantity(Number(e.target.value))}
            error={Boolean(quantityError)}
            helperText={quantityError || "Máximo 4 entradas por reserva."}
            slotProps={{ htmlInput: { min: 1, max: 4 } }}
            disabled={loading}
            sx={{ minWidth: { md: 180 } }}
          />
        </Stack>

        {/* Mapa visual del estadio + selección de zona */}
        <Box sx={{ mt: 2 }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
            Selecciona una zona
          </Typography>

         {/* Mini mapa SVG del estadio - óvalo continuo */}
 <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
  <StadiumMap
    categoria={categoria}
    sector={sector}
    onSelect={(cat, sec) => { setCategoria(cat); setSector(sec); }}
  />
</Box>

{/* Tarjetas de zona */}
<Stack direction={{ xs: "column", sm: "row" }} spacing={1.5}>
           {(["BARRA", "GENERAL", "PALCO", "ESQUINA"] as const).map((cat) => {
              const precio = calcularPrecio(partidoSeleccionado?.ronda, cat);
              const colores: Record<string, { border: string; bg: string; text: string; sectores: string[] }> = {
              BARRA:   { border: "#64b464", bg: "rgba(100,180,100,0.08)", text: "#64b464", sectores: ["Norte", "Sur"] },
                GENERAL: { border: "#ffb400", bg: "rgba(255,180,0,0.08)",   text: "#ffb400", sectores: ["Oriental", "Occidental"] },
                PALCO:   { border: "#b464ff", bg: "rgba(180,100,255,0.08)", text: "#b464ff", sectores: ["Occidental VIP"] },
              ESQUINA: { border: "#2196f3", bg: "rgba(33,150,243,0.08)", text: "#2196f3", sectores: ["Noroccidental", "Nororiental", "Suroccidental", "Suroriental"] },
              };
              const c = colores[cat];
              const selected = categoria === cat;
              return (
                <Box key={cat} onClick={() => { setCategoria(cat); setSector(c.sectores[0]); }}
                  sx={{
                    flex: 1, p: 2, borderRadius: 2, cursor: "pointer", textAlign: "center",
                    border: `2px solid ${selected ? c.border : "rgba(255,255,255,0.1)"}`,
                    bgcolor: selected ? c.bg : "rgba(255,255,255,0.03)",
                    transition: "all 0.2s",
                    "&:hover": { border: `2px solid ${c.border}`, bgcolor: c.bg },
                  }}
                >
                  <Typography sx={{ fontWeight: 800, color: selected ? c.text : "text.primary", fontSize: 13 }}>
                    {cat}
                  </Typography>
                  <Typography sx={{ fontWeight: 900, fontSize: 16, color: c.text, mt: 0.5 }}>
                    {formatPrecio(precio)}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
  {c.sectores.join(" · ")}
</Typography>
{cuposDeZona(cat) !== null && (
  <Typography variant="caption" sx={{ color: c.text, display: "block", mt: 0.5, fontWeight: 700 }}>
    {cuposDeZona(cat)!.toLocaleString("es-CO")} cupos
  </Typography>
)}
                </Box>
              );
            })}
          </Stack>

        {/* Selector de sector */}
          {(() => {
            const sectoresPorCategoria = categoria === "BARRA" ? ["Norte", "Sur"] : categoria === "GENERAL" ? ["Oriental", "Occidental"] : categoria === "ESQUINA" ? ["Noroccidental", "Nororiental", "Suroccidental", "Suroriental"] : ["Occidental VIP"];
            return categoria && (
            <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} sx={{ mt: 2 }}>
              <TextField
                select
                label="Sector"
                value={sector}
                onChange={(e) => setSector(e.target.value)}
                disabled={loading}
                fullWidth
              >
{sectoresPorCategoria.map((s) => (
                  <MenuItem key={s} value={s}>{s}</MenuItem>
                ))}
              </TextField>
            </Stack>);})()}
          

          {categoria && (
            <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: "block" }}>
              La fila y los asientos se asignan automáticamente según disponibilidad.
            </Typography>
          )}

          {selectedMatchId && (
            <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: "block" }}>
              Total estimado: <strong>{formatPrecio(calcularPrecio(partidoSeleccionado?.ronda, categoria) * quantity)}</strong>
            </Typography>
          )}
        </Box>

        <Box sx={{ mt: 2 }}>
          <Button variant="contained" onClick={onReserve} disabled={loading || filteredPartidos.length === 0}>
            Reservar
          </Button>
        </Box>
      </Paper>

      <Paper sx={{ p: 2.5 }}>
        <Typography variant="h6">Resumen</Typography>
        <Typography color="text.secondary">
         Total: {summary.total} · Reservadas: {summary.reserved} · Pagadas: {summary.paid} · Reembolsadas: {summary.refunded} · Canceladas: {summary.cancelled} · Expiradas: {summary.expired} · Transferidas: {summary.transferred}
        </Typography>
      </Paper>

      <Paper sx={{ p: 2.5 }}>
        <Typography variant="h6">Mis entradas</Typography>

        {items.length === 0 ? (
          <Typography color="text.secondary" sx={{ mt: 1 }}>
            Aún no tienes entradas.
          </Typography>
        ) : (
          <Stack spacing={1.5} sx={{ mt: 2 }}>
            {items.map((ticket) => {
              const match = matchById.get(ticket.matchId);
              // fallback: buscar en partidosCapacidad si match no está en la API externa
              const partido = partidosCapacidad.find((p) => String(p.id) === ticket.matchId);
              return (
                <Paper key={ticket.id} variant="outlined" sx={{ p: 2 }}>
                  <Stack direction={{ xs: "column", md: "row" }} justifyContent="space-between" spacing={2}>
                    <Stack spacing={0.5}>
                      <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap" alignItems="center">
                        <Typography sx={{ fontWeight: 800 }}>
                          {match
                            ? `${match.home.name} vs ${match.away.name}`
                            : partido
                            ? `${partido.local} vs ${partido.visitante}`
                            : ticket.matchId}
                        </Typography>
                        <Chip label={statusLabel(ticket.status)} size="small" variant="outlined" />
                      </Stack>
                      <Typography color="text.secondary">
                        Cantidad: {ticket.quantity}
                        {ticket.categoria ? ` · ${ticket.categoria}` : ""}
                        {ticket.sector ? ` · ${ticket.sector}` : ""}
                        {ticket.fila ? ` · Fila ${ticket.fila}` : ""}
                      </Typography>
                      {(match || partido) && (
                        <Typography color="text.secondary">
                          {match ? match.city : partido?.ciudad} · {match ? match.stadium : partido?.estadio}
                        </Typography>
                      )}
                      <Typography variant="caption" color="text.secondary">
                        Creada: {new Date(ticket.createdAt).toLocaleString()}
                        {ticket.expiresAt ? ` · Expira: ${new Date(ticket.expiresAt).toLocaleString()}` : ""}
                        {ticket.paymentRef ? ` · Ref: ${ticket.paymentRef}` : ""}
                      </Typography>
                    </Stack>

                    <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
                      {ticket.status === "RESERVADA" && (
                        <>
                          <Button
                            variant="contained"
                           onClick={() => navigate(`/checkout?type=TICKET&ticketId=${ticket.id}&amount=${ticket.price ?? 0}`)}
                          >
                            Pagar
                          </Button>
                          <Button variant="outlined" color="error" onClick={() => onCancel(ticket.id)}>
                            Cancelar
                          </Button>
                        </>
                      )}
                      {ticket.status === "PAGADA" && (
                        <>
                         <Button variant="outlined" onClick={() => navigate(`/entrada/${ticket.id}`)}>
                            Ver entrada
                          </Button>
                          <Button variant="outlined" color="warning" onClick={() => onOpenTransfer(ticket.id)}>
                            Transferir
                          </Button>
                          <Button variant="outlined" color="error" onClick={() => onRefund(ticket.id)}>
                            Reembolsar
                          </Button>
                        </>
                      )}
                    </Stack>
                  </Stack>
                </Paper>
              );
            })}
          </Stack>
        )}
      </Paper>

      <Dialog open={transferDialogOpen} onClose={() => setTransferDialogOpen(false)}>
        <DialogTitle>Transferir entrada</DialogTitle>
        <DialogContent>
          <TextField
            label="Correo del destinatario"
            value={correoDestino}
            onChange={(e) => setCorreoDestino(e.target.value)}
            fullWidth
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTransferDialogOpen(false)}>Cancelar</Button>
          <Button variant="contained" onClick={onTransfer} disabled={loading}>
            Transferir
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}