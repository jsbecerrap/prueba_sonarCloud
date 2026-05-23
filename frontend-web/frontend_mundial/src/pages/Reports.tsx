import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  LabelList,
} from "recharts";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import ReceiptIcon from "@mui/icons-material/Receipt";
import ConfirmationNumberIcon from "@mui/icons-material/ConfirmationNumber";
import SportsSoccerIcon from "@mui/icons-material/SportsSoccer";
import SwapHorizIcon from "@mui/icons-material/SwapHoriz";
import PeopleIcon from "@mui/icons-material/People";
import EmojiEventsIcon from "@mui/icons-material/EmojiEvents";
import StoreIcon from "@mui/icons-material/Store";
import PersonIcon from "@mui/icons-material/Person";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import InsightsRoundedIcon from "@mui/icons-material/InsightsRounded";
import GroupsRoundedIcon from "@mui/icons-material/GroupsRounded";
import PaymentRoundedIcon from "@mui/icons-material/PaymentRounded";
import CategoryRoundedIcon from "@mui/icons-material/CategoryRounded";
import BarChartRoundedIcon from "@mui/icons-material/BarChartRounded";
import AssessmentRoundedIcon from "@mui/icons-material/AssessmentRounded";

import {
  getReportesCompras,
  getEstadisticasGenerales,
  getPartidosMasApostados,
  getPollaRanking,
  getIngresosPorMetodoPago,
  getEntradasPorPartido,
  getTopUsuariosSouvenir,
  getTopUsuariosEntrada,
} from "../api/reportsApi";
import type {
  ReportesCompras,
  ReportesStats,
  PartidoMasApostado,
  PollaRanking,
  IngresoMetodoPago,
  EntradaPorPartido,
  TopUsuarioSouvenir,
  TopUsuarioEntrada,
} from "../api/reportsApi";

/* ============================================================
 * Paleta y helpers
 * ============================================================ */

const ACCENT_GREEN = "#2EE59D";
const ACCENT_GOLD = "#FFD166";
const ACCENT_BLUE = "#6FA8FF";
const ACCENT_PURPLE = "#B388FF";
const ACCENT_ORANGE = "#FF8A65";
const ACCENT_TEAL = "#4DD0E1";
const ACCENT_PINK = "#FF7AB6";
const ACCENT_LIME = "#C6FF6E";

const CHART_PALETTE = [
  ACCENT_GREEN,
  ACCENT_GOLD,
  ACCENT_BLUE,
  ACCENT_PURPLE,
  ACCENT_ORANGE,
  ACCENT_TEAL,
  ACCENT_PINK,
  ACCENT_LIME,
];

const SURFACE_BG = "rgba(8, 18, 36, 0.55)";
const SURFACE_BORDER = "1px solid rgba(234,242,255,0.10)";
const SURFACE_BORDER_STRONG = "1px solid rgba(234,242,255,0.16)";

function formatCOP(value: number) {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    maximumFractionDigits: 0,
  }).format(value);
}

function formatCompactCOP(value: number) {
  if (value >= 1_000_000) {
    return `$${(value / 1_000_000).toFixed(1)}M`;
  }
  if (value >= 1_000) {
    return `$${(value / 1_000).toFixed(0)}K`;
  }
  return `$${value.toFixed(0)}`;
}

/* ============================================================
 * Tooltip personalizado para Recharts (modo oscuro)
 * ============================================================ */

interface TooltipPayloadItem {
  readonly name?: string;
  readonly value?: number;
  readonly color?: string;
  readonly payload?: { readonly color?: string };
}

interface DarkTooltipProps {
  readonly active?: boolean;
  readonly payload?: TooltipPayloadItem[];
  readonly label?: string;
  readonly formatter?: (v: number) => string;
}

function DarkTooltip({ active, payload, label, formatter }: DarkTooltipProps) {
  if (!active || !payload || payload.length === 0) return null;
  return (
    <Box
      sx={{
        backgroundColor: "rgba(8, 18, 36, 0.95)",
        backdropFilter: "blur(14px)",
        border: SURFACE_BORDER_STRONG,
        borderRadius: 2,
        px: 2,
        py: 1.5,
        boxShadow: "0 12px 30px rgba(0,0,0,.5)",
        minWidth: 160,
      }}
    >
      {label && (
        <Typography variant="caption" sx={{ color: "rgba(234,242,255,0.7)", fontWeight: 700, display: "block", mb: 0.5 }}>
          {label}
        </Typography>
      )}
      {payload.map((entry, i) => (
        <Stack key={i} direction="row" alignItems="center" spacing={1} sx={{ mt: i > 0 ? 0.5 : 0 }}>
          <Box
            sx={{
              width: 10,
              height: 10,
              borderRadius: "50%",
              backgroundColor: entry.color ?? entry.payload?.color ?? ACCENT_GREEN,
            }}
          />
          <Typography variant="body2" sx={{ color: "rgba(234,242,255,0.9)", fontWeight: 600 }}>
            {entry.name}:{" "}
            <Box component="span" sx={{ color: ACCENT_GREEN, ml: 0.5 }}>
              {formatter ? formatter(entry.value ?? 0) : (entry.value ?? 0).toLocaleString()}
            </Box>
          </Typography>
        </Stack>
      ))}
    </Box>
  );
}

/* ============================================================
 * Componentes reutilizables
 * ============================================================ */

interface KpiCardProps {
  readonly label: string;
  readonly value: string;
  readonly icon: React.ReactNode;
  readonly color: string;
  readonly size?: "lg" | "md";
}

function KpiCard({ label, value, icon, color, size = "md" }: KpiCardProps) {
  const isLg = size === "lg";
  return (
    <Card
      sx={{
        backgroundColor: SURFACE_BG,
        backdropFilter: "blur(14px)",
        border: SURFACE_BORDER,
        borderRadius: 3,
        overflow: "hidden",
        position: "relative",
        transition: "transform .2s ease, border-color .2s ease",
        "&:hover": {
          transform: "translateY(-2px)",
          borderColor: color,
        },
        "&::before": {
          content: '""',
          position: "absolute",
          top: 0,
          left: 0,
          right: 0,
          height: 3,
          background: `linear-gradient(90deg, ${color}, transparent)`,
        },
      }}
    >
      <CardContent sx={{ display: "flex", alignItems: "center", gap: 2, py: isLg ? 3 : 2.5 }}>
        <Box
          sx={{
            color,
            fontSize: isLg ? 48 : 36,
            display: "grid",
            placeItems: "center",
            width: isLg ? 64 : 52,
            height: isLg ? 64 : 52,
            borderRadius: 2,
            background: `${color}15`,
            border: `1px solid ${color}30`,
          }}
        >
          {icon}
        </Box>
        <Box sx={{ minWidth: 0 }}>
          <Typography
            variant="caption"
            sx={{
              color: "rgba(234,242,255,0.6)",
              textTransform: "uppercase",
              letterSpacing: 0.8,
              fontWeight: 700,
              fontSize: 11,
            }}
          >
            {label}
          </Typography>
          <Typography
            variant={isLg ? "h3" : "h5"}
            sx={{
              fontWeight: 900,
              color: "rgba(234,242,255,0.95)",
              lineHeight: 1.1,
              mt: 0.5,
              wordBreak: "break-word",
            }}
          >
            {value}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

interface SectionHeaderProps {
  readonly title: string;
  readonly subtitle?: string;
  readonly icon?: React.ReactNode;
  readonly accent?: string;
}
function SectionHeader({ title, subtitle, icon, accent = ACCENT_GREEN }: SectionHeaderProps) {
  return (
    <Stack direction="row" alignItems="center" spacing={1.5} sx={{ mb: 2.5 }}>
      {icon && (
        <Box
          sx={{
            display: "grid",
            placeItems: "center",
            width: 40,
            height: 40,
            borderRadius: 2,
            background: `${accent}18`,
            border: `1px solid ${accent}40`,
            color: accent,
            fontSize: 22,
          }}
        >
          {icon}
        </Box>
      )}
      <Box>
        <Typography variant="h5" sx={{ fontWeight: 900, color: "rgba(234,242,255,0.95)", lineHeight: 1.1 }}>
          {title}
        </Typography>
        {subtitle && (
          <Typography variant="body2" sx={{ color: "rgba(234,242,255,0.55)", mt: 0.25 }}>
            {subtitle}
          </Typography>
        )}
      </Box>
    </Stack>
  );
}

function PanelCard({ children, accent }: { children: React.ReactNode; accent?: string }) {
  return (
    <Paper
      elevation={0}
      sx={{
        backgroundColor: SURFACE_BG,
        backdropFilter: "blur(14px)",
        border: SURFACE_BORDER,
        borderRadius: 3,
        overflow: "hidden",
        ...(accent && {
          borderTop: `3px solid ${accent}`,
        }),
      }}
    >
      {children}
    </Paper>
  );
}

function PanelTitle({
  children,
  icon,
  accent,
}: {
  children: React.ReactNode;
  icon?: React.ReactNode;
  accent?: string;
}) {
  return (
    <Stack
      direction="row"
      alignItems="center"
      spacing={1.5}
      sx={{
        px: 3,
        py: 2,
        borderBottom: SURFACE_BORDER,
      }}
    >
      {icon && (
        <Box sx={{ color: accent ?? ACCENT_GREEN, fontSize: 22, display: "flex" }}>
          {icon}
        </Box>
      )}
      <Typography variant="h6" sx={{ fontWeight: 800, color: "rgba(234,242,255,0.92)" }}>
        {children}
      </Typography>
    </Stack>
  );
}

/* Estilos compartidos para tablas en modo oscuro */
const tableHeaderSx = {
  "& .MuiTableCell-head": {
    backgroundColor: "rgba(234,242,255,0.04)",
    color: "rgba(234,242,255,0.65)",
    fontWeight: 700,
    fontSize: 12,
    textTransform: "uppercase",
    letterSpacing: 0.6,
    borderBottom: SURFACE_BORDER_STRONG,
  },
};

const tableBodySx = {
  "& .MuiTableCell-body": {
    color: "rgba(234,242,255,0.85)",
    borderBottom: "1px solid rgba(234,242,255,0.05)",
  },
  "& .MuiTableRow-root:hover": {
    backgroundColor: "rgba(46,229,157,0.04) !important",
  },
};

function RankCell({ index }: { index: number }) {
  if (index === 0) {
    return <EmojiEventsIcon sx={{ color: ACCENT_GOLD, fontSize: 20, verticalAlign: "middle" }} />;
  }
  if (index === 1) {
    return (
      <Typography variant="body2" sx={{ color: "rgba(234,242,255,0.65)", fontWeight: 700 }}>
        {index + 1}
      </Typography>
    );
  }
  if (index === 2) {
    return (
      <Typography variant="body2" sx={{ color: "rgba(234,242,255,0.55)", fontWeight: 700 }}>
        {index + 1}
      </Typography>
    );
  }
  return (
    <Typography variant="body2" sx={{ color: "rgba(234,242,255,0.4)" }}>
      {index + 1}
    </Typography>
  );
}

function MoneyText({ value }: { value: number }) {
  return (
    <Typography variant="body2" sx={{ color: ACCENT_GREEN, fontWeight: 700 }}>
      {formatCOP(value)}
    </Typography>
  );
}

/* ============================================================
 * Página principal
 * ============================================================ */

export default function Reports() {
  const [compras, setCompras] = useState<ReportesCompras | null>(null);
  const [stats, setStats] = useState<ReportesStats | null>(null);
  const [partidos, setPartidos] = useState<PartidoMasApostado[]>([]);
  const [pollas, setPollas] = useState<PollaRanking[]>([]);
  const [metodosPago, setMetodosPago] = useState<IngresoMetodoPago[]>([]);
  const [entradasPartido, setEntradasPartido] = useState<EntradaPorPartido[]>([]);
  const [topSouvenir, setTopSouvenir] = useState<TopUsuarioSouvenir[]>([]);
  const [topEntrada, setTopEntrada] = useState<TopUsuarioEntrada[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      getEstadisticasGenerales(),
      getReportesCompras(),
      getPartidosMasApostados(),
      getPollaRanking(),
      getIngresosPorMetodoPago(),
      getEntradasPorPartido(),
      getTopUsuariosSouvenir(5),
      getTopUsuariosEntrada(5),
    ])
      .then(
        ([
          statsData,
          comprasData,
          partidosData,
          pollasData,
          metodosData,
          entradasData,
          souvenirData,
          entradaData,
        ]) => {
          setStats(statsData);
          setCompras(comprasData);
          setPartidos(partidosData);
          setPollas(pollasData);
          setMetodosPago(metodosData);
          setEntradasPartido(entradasData);
          setTopSouvenir(souvenirData);
          setTopEntrada(entradaData);
        }
      )
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (error) return <Alert severity="error">{error}</Alert>;

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={6}>
        <CircularProgress sx={{ color: ACCENT_GREEN }} />
      </Box>
    );
  }

  /* ============ Datos derivados para los gráficos ============ */

  const categoriaChartData =
    compras?.ventasPorCategoria.map((c, i) => ({
      name: c.categoria,
      value: c.ingresoTotal,
      cantidad: c.cantidadVendida,
      color: CHART_PALETTE[i % CHART_PALETTE.length],
    })) ?? [];

  const metodoChartData = metodosPago.map((m, i) => ({
    name: m.tipo,
    value: m.ingresoTotal,
    ordenes: m.totalOrdenes,
    color: CHART_PALETTE[i % CHART_PALETTE.length],
  }));

  // Recharts pinta barras horizontales de abajo hacia arriba, así que ordenamos ascendente
  // para que el #1 quede arriba visualmente
  const productosChartData = compras
    ? [...compras.productosMasVendidos]
        .sort((a, b) => a.ingresoTotal - b.ingresoTotal)
        .map((p) => ({
          name: p.nombre.length > 18 ? p.nombre.substring(0, 18) + "..." : p.nombre,
          fullName: p.nombre,
          ingreso: p.ingresoTotal,
          unidades: p.cantidadVendida,
          categoria: p.categoria,
        }))
    : [];

  return (
    <Box sx={{ p: { xs: 1, md: 1 } }}>
      {/* ============ HEADER ============ */}
      <Box
        sx={{
          mb: 5,
          p: { xs: 3, md: 4 },
          borderRadius: 3,
          background:
            "linear-gradient(135deg, rgba(46,229,157,0.12) 0%, rgba(255,209,102,0.08) 50%, rgba(8,18,36,0.4) 100%)",
          border: SURFACE_BORDER,
          position: "relative",
          overflow: "hidden",
        }}
      >
        <Box
          sx={{
            position: "absolute",
            top: -40,
            right: -40,
            width: 200,
            height: 200,
            borderRadius: "50%",
            background: "radial-gradient(circle, rgba(46,229,157,0.18) 0%, transparent 70%)",
          }}
        />
        <Stack direction="row" alignItems="center" spacing={2} sx={{ position: "relative" }}>
          <Box
            sx={{
              width: 56,
              height: 56,
              borderRadius: 2.5,
              display: "grid",
              placeItems: "center",
              background: "linear-gradient(135deg, rgba(46,229,157,.42) 0%, rgba(255,209,102,.28) 100%)",
              border: SURFACE_BORDER_STRONG,
              boxShadow: "0 12px 30px rgba(0,0,0,.35)",
              color: "rgba(234,242,255,0.95)",
              fontSize: 28,
            }}
          >
            <AssessmentRoundedIcon fontSize="inherit" />
          </Box>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 900, color: "rgba(234,242,255,0.98)", lineHeight: 1.1 }}>
              Reportes
            </Typography>
            <Typography variant="body1" sx={{ color: "rgba(234,242,255,0.6)", mt: 0.5 }}>
              Estadísticas generales, ventas, pollas y actividad del torneo
            </Typography>
          </Box>
        </Stack>
      </Box>

      {/* ============ ZONA 1: PULSO DEL NEGOCIO ============ */}
      {(stats || compras) && (
        <Box mb={6}>
          <SectionHeader
            title="Pulso del negocio"
            subtitle="Los números que mueven el torneo"
            icon={<TrendingUpRoundedIcon />}
            accent={ACCENT_GREEN}
          />

          {compras && (
            <Box mb={3}>
              <KpiCard
                label="Ingreso total de souvenirs"
                value={formatCOP(compras.ingresoTotal)}
                icon={<AttachMoneyIcon fontSize="inherit" />}
                color={ACCENT_GREEN}
                size="lg"
              />
            </Box>
          )}

          <Box
            sx={{
              display: "grid",
              gap: 2.5,
              gridTemplateColumns: {
                xs: "1fr",
                sm: "1fr 1fr",
                md: "repeat(3, 1fr)",
                lg: "repeat(6, 1fr)",
              },
            }}
          >
            {compras && (
              <>
                <KpiCard
                  label="Órdenes pagadas"
                  value={compras.totalOrdenes.toLocaleString()}
                  icon={<ReceiptIcon fontSize="inherit" />}
                  color={ACCENT_BLUE}
                />
                <KpiCard
                  label="Entradas vendidas"
                  value={compras.totalEntradasVendidas.toLocaleString()}
                  icon={<ConfirmationNumberIcon fontSize="inherit" />}
                  color={ACCENT_ORANGE}
                />
              </>
            )}
            {stats && (
              <>
                <KpiCard
                  label="Total usuarios"
                  value={stats.totalUsuarios.toLocaleString()}
                  icon={<PeopleIcon fontSize="inherit" />}
                  color={ACCENT_PURPLE}
                />
                <KpiCard
                  label="Usuarios activos"
                  value={stats.usuariosActivos.toLocaleString()}
                  icon={<PersonIcon fontSize="inherit" />}
                  color={ACCENT_TEAL}
                />
                <KpiCard
                  label="Partidos"
                  value={stats.totalPartidos.toLocaleString()}
                  icon={<SportsSoccerIcon fontSize="inherit" />}
                  color={ACCENT_GOLD}
                />
                <KpiCard
                  label="Transacciones"
                  value={stats.totalTransacciones.toLocaleString()}
                  icon={<SwapHorizIcon fontSize="inherit" />}
                  color={ACCENT_GREEN}
                />
              </>
            )}
          </Box>
        </Box>
      )}

      {/* ============ ZONA 2: VENTAS Y PRODUCTOS ============ */}
      <Box mb={6}>
        <SectionHeader
          title="Ventas y productos"
          subtitle="Qué se vende y cuánto está generando"
          icon={<InsightsRoundedIcon />}
          accent={ACCENT_GOLD}
        />

        {/* Donut categorías + Donut métodos de pago */}
        <Box
          sx={{
            display: "grid",
            gap: 3,
            gridTemplateColumns: { xs: "1fr", md: "1fr 1fr" },
            mb: 3,
          }}
        >
          {/* GRÁFICO 1: Donut Ventas por Categoría */}
          {categoriaChartData.length > 0 && (
            <PanelCard accent={ACCENT_PURPLE}>
              <PanelTitle icon={<CategoryRoundedIcon />} accent={ACCENT_PURPLE}>
                Ventas por categoría de souvenir
              </PanelTitle>
              <Box sx={{ p: 3 }}>
                <Box sx={{ width: "100%", height: 320 }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={categoriaChartData}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        innerRadius={70}
                        outerRadius={110}
                        paddingAngle={3}
                        stroke="rgba(8,18,36,0.6)"
                        strokeWidth={2}
                      >
                        {categoriaChartData.map((entry, i) => (
                          <Cell key={i} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip content={<DarkTooltip formatter={formatCOP} />} />
                      <Legend
                        verticalAlign="bottom"
                        height={36}
                        iconType="circle"
                        formatter={(value) => (
                          <span style={{ color: "rgba(234,242,255,0.85)", fontSize: 12, fontWeight: 600 }}>
                            {value}
                          </span>
                        )}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </Box>
              </Box>
            </PanelCard>
          )}

          {/* GRÁFICO 2: Donut Métodos de Pago */}
          {metodoChartData.length > 0 && (
            <PanelCard accent={ACCENT_BLUE}>
              <PanelTitle icon={<PaymentRoundedIcon />} accent={ACCENT_BLUE}>
                Ingresos por método de pago
              </PanelTitle>
              <Box sx={{ p: 3 }}>
                <Box sx={{ width: "100%", height: 320 }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={metodoChartData}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        innerRadius={70}
                        outerRadius={110}
                        paddingAngle={3}
                        stroke="rgba(8,18,36,0.6)"
                        strokeWidth={2}
                      >
                        {metodoChartData.map((entry, i) => (
                          <Cell key={i} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip content={<DarkTooltip formatter={formatCOP} />} />
                      <Legend
                        verticalAlign="bottom"
                        height={36}
                        iconType="circle"
                        formatter={(value) => (
                          <span style={{ color: "rgba(234,242,255,0.85)", fontSize: 12, fontWeight: 600 }}>
                            {value}
                          </span>
                        )}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </Box>
              </Box>
            </PanelCard>
          )}
        </Box>

        {/* GRÁFICO 3: Barras horizontales Top 5 Productos */}
        {productosChartData.length > 0 && (
          <PanelCard accent={ACCENT_GOLD}>
            <PanelTitle icon={<BarChartRoundedIcon />} accent={ACCENT_GOLD}>
              Top 5 productos más vendidos por ingreso
            </PanelTitle>
            <Box sx={{ p: 3 }}>
              <Box sx={{ width: "100%", height: 340 }}>
                <ResponsiveContainer width="100%" height="100%">
                <BarChart
  data={productosChartData}
  layout="vertical"
  margin={{ top: 10, right: 60, left: 0, bottom: 10 }}
>
                    <CartesianGrid strokeDasharray="3 3" stroke="rgba(234,242,255,0.06)" horizontal={false} />
                    <XAxis
                      type="number"
                      stroke="rgba(234,242,255,0.5)"
                      tick={{ fill: "rgba(234,242,255,0.65)", fontSize: 11 }}
                      tickFormatter={formatCompactCOP}
                    />
                    <YAxis
  type="category"
  dataKey="name"
  stroke="rgba(234,242,255,0.5)"
  tick={{ fill: "rgba(234,242,255,0.85)", fontSize: 11, fontWeight: 600 }}
  width={140}
/>
                    <Tooltip
                      content={<DarkTooltip formatter={formatCOP} />}
                      cursor={{ fill: "rgba(255,209,102,0.06)" }}
                    />
                    <Bar dataKey="ingreso" name="Ingreso" radius={[0, 6, 6, 0]} fill={ACCENT_GOLD}>
                      {productosChartData.map((_, i) => (
                        <Cell key={i} fill={CHART_PALETTE[i % CHART_PALETTE.length]} />
                      ))}
                      <LabelList
                        dataKey="unidades"
                        position="right"
                        formatter={(value: React.ReactNode) => `${value} uds`}
                        style={{ fill: "rgba(234,242,255,0.85)", fontSize: 11, fontWeight: 700 }}
                      />
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </Box>
          </PanelCard>
        )}
      </Box>

      {/* ============ ZONA 3: PARTIDOS Y POLLAS ============ */}
      {(entradasPartido.length > 0 || partidos.length > 0 || pollas.length > 0) && (
        <Box mb={6}>
          <SectionHeader
            title="Partidos y pollas"
            subtitle="Actividad de entradas, pronósticos y participación"
            icon={<SportsSoccerIcon />}
            accent={ACCENT_ORANGE}
          />

          {/* Entradas por partido - ancho completo */}
          {entradasPartido.length > 0 && (
            <Box mb={3}>
              <PanelCard accent={ACCENT_ORANGE}>
                <PanelTitle icon={<ConfirmationNumberIcon />} accent={ACCENT_ORANGE}>
                  Entradas vendidas por partido
                </PanelTitle>
                <TableContainer>
                  <Table size="small" sx={{ ...tableHeaderSx, ...tableBodySx }}>
                    <TableHead>
                      <TableRow>
                        <TableCell sx={{ width: 50 }}>#</TableCell>
                        <TableCell>Partido</TableCell>
                        <TableCell>Ronda</TableCell>
                        <TableCell>Estadio</TableCell>
                        <TableCell align="right">Entradas</TableCell>
                        <TableCell align="right">Ingreso</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {entradasPartido.map((e: EntradaPorPartido, i: number) => (
                        <TableRow key={e.partidoId} hover>
                          <TableCell>
                            <RankCell index={i} />
                          </TableCell>
                          <TableCell>
                            <Typography
                              variant="body2"
                              sx={{ fontWeight: 600, color: "rgba(234,242,255,0.92)" }}
                            >
                              {e.local}{" "}
                              <Box component="span" sx={{ color: "rgba(234,242,255,0.4)", mx: 0.5 }}>
                                vs
                              </Box>{" "}
                              {e.visitante}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={e.ronda}
                              size="small"
                              sx={{
                                backgroundColor: "rgba(255,138,101,0.12)",
                                color: ACCENT_ORANGE,
                                border: `1px solid ${ACCENT_ORANGE}30`,
                                fontWeight: 600,
                              }}
                            />
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2" sx={{ color: "rgba(234,242,255,0.6)" }}>
                              {e.estadio}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">{e.cantidadVendida.toLocaleString()}</TableCell>
                          <TableCell align="right">
                            <MoneyText value={e.ingresoTotal} />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </PanelCard>
            </Box>
          )}

          {/* Partidos apostados + Ranking pollas en grid */}
          <Box
            sx={{
              display: "grid",
              gap: 3,
              gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
            }}
          >
            {partidos.length > 0 && (
              <PanelCard accent={ACCENT_TEAL}>
                <PanelTitle icon={<SportsSoccerIcon />} accent={ACCENT_TEAL}>
                  Partidos más apostados
                </PanelTitle>
                <TableContainer>
                  <Table size="small" sx={{ ...tableHeaderSx, ...tableBodySx }}>
                    <TableHead>
                      <TableRow>
                        <TableCell sx={{ width: 50 }}>#</TableCell>
                        <TableCell>Partido</TableCell>
                        <TableCell>Ronda</TableCell>
                        <TableCell align="right">Pronósticos</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {partidos.map((p: PartidoMasApostado, i: number) => (
                        <TableRow key={p.partidoId} hover>
                          <TableCell>
                            <RankCell index={i} />
                          </TableCell>
                          <TableCell>
                            <Typography
                              variant="body2"
                              sx={{ fontWeight: 600, color: "rgba(234,242,255,0.92)" }}
                            >
                              {p.local}{" "}
                              <Box component="span" sx={{ color: "rgba(234,242,255,0.4)", mx: 0.5 }}>
                                vs
                              </Box>{" "}
                              {p.visitante}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={p.ronda}
                              size="small"
                              sx={{
                                backgroundColor: "rgba(77,208,225,0.12)",
                                color: ACCENT_TEAL,
                                border: `1px solid ${ACCENT_TEAL}30`,
                                fontWeight: 600,
                              }}
                            />
                          </TableCell>
                          <TableCell align="right">
                            <Typography variant="body2" sx={{ fontWeight: 800, color: ACCENT_TEAL }}>
                              {p.totalPronosticos.toLocaleString()}
                            </Typography>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </PanelCard>
            )}

            {pollas.length > 0 && (
              <PanelCard accent={ACCENT_PURPLE}>
                <PanelTitle icon={<GroupsRoundedIcon />} accent={ACCENT_PURPLE}>
                  Ranking de pollas
                </PanelTitle>
                <TableContainer>
                  <Table size="small" sx={{ ...tableHeaderSx, ...tableBodySx }}>
                    <TableHead>
                      <TableRow>
                        <TableCell sx={{ width: 50 }}>#</TableCell>
                        <TableCell>Polla</TableCell>
                        <TableCell>Estado</TableCell>
                        <TableCell align="right">Participantes</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {pollas.map((p: PollaRanking, i: number) => {
                        const estadoColor =
                          p.estado === "abierta"
                            ? ACCENT_GREEN
                            : p.estado === "cerrada"
                            ? "#9aa5b8"
                            : ACCENT_GOLD;
                        return (
                          <TableRow key={p.apuestaId} hover>
                            <TableCell>
                              <RankCell index={i} />
                            </TableCell>
                            <TableCell>
                              <Typography
                                variant="body2"
                                sx={{ fontWeight: 600, color: "rgba(234,242,255,0.92)" }}
                              >
                                {p.nombre}
                              </Typography>
                            </TableCell>
                            <TableCell>
                              <Chip
                                label={p.estado}
                                size="small"
                                sx={{
                                  backgroundColor: `${estadoColor}15`,
                                  color: estadoColor,
                                  border: `1px solid ${estadoColor}40`,
                                  fontWeight: 700,
                                  textTransform: "capitalize",
                                }}
                              />
                            </TableCell>
                            <TableCell align="right">
                              <Typography variant="body2" sx={{ fontWeight: 800, color: ACCENT_PURPLE }}>
                                {p.totalParticipantes}
                              </Typography>
                            </TableCell>
                          </TableRow>
                        );
                      })}
                    </TableBody>
                  </Table>
                </TableContainer>
              </PanelCard>
            )}
          </Box>
        </Box>
      )}

      {/* ============ ZONA 4: USUARIOS DESTACADOS ============ */}
      {(topSouvenir.length > 0 || topEntrada.length > 0) && (
        <Box mb={4}>
          <SectionHeader
            title="Usuarios destacados"
            subtitle="Quiénes están gastando más en la plataforma"
            icon={<EmojiEventsIcon />}
            accent={ACCENT_GOLD}
          />

          <Box
            sx={{
              display: "grid",
              gap: 3,
              gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
            }}
          >
            {topSouvenir.length > 0 && (
              <PanelCard accent={ACCENT_GREEN}>
                <PanelTitle icon={<StoreIcon />} accent={ACCENT_GREEN}>
                  Top 5 compradores de souvenirs
                </PanelTitle>
                <TableContainer>
                  <Table size="small" sx={{ ...tableHeaderSx, ...tableBodySx }}>
                    <TableHead>
                      <TableRow>
                        <TableCell sx={{ width: 50 }}>#</TableCell>
                        <TableCell>Usuario</TableCell>
                        <TableCell>Correo</TableCell>
                        <TableCell align="right">Órdenes</TableCell>
                        <TableCell align="right">Total</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {topSouvenir.map((u: TopUsuarioSouvenir, i: number) => (
                        <TableRow key={u.usuarioId} hover>
                          <TableCell>
                            <RankCell index={i} />
                          </TableCell>
                          <TableCell>
                            <Stack direction="row" alignItems="center" spacing={1}>
                              <Box
                                sx={{
                                  width: 28,
                                  height: 28,
                                  borderRadius: "50%",
                                  background: `linear-gradient(135deg, ${ACCENT_GREEN}40, ${ACCENT_GOLD}30)`,
                                  border: `1px solid ${ACCENT_GREEN}40`,
                                  display: "grid",
                                  placeItems: "center",
                                  color: ACCENT_GREEN,
                                  fontWeight: 800,
                                  fontSize: 12,
                                }}
                              >
                                {u.nombre.charAt(0)}
                                {u.apellido.charAt(0)}
                              </Box>
                              <Typography
                                variant="body2"
                                sx={{ fontWeight: 600, color: "rgba(234,242,255,0.92)" }}
                              >
                                {u.nombre} {u.apellido}
                              </Typography>
                            </Stack>
                          </TableCell>
                          <TableCell>
                            <Typography
                              variant="body2"
                              sx={{ color: "rgba(234,242,255,0.55)", fontSize: 12 }}
                            >
                              {u.correo}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">{u.totalOrdenes}</TableCell>
                          <TableCell align="right">
                            <MoneyText value={u.totalGastado} />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </PanelCard>
            )}

            {topEntrada.length > 0 && (
              <PanelCard accent={ACCENT_ORANGE}>
                <PanelTitle icon={<ConfirmationNumberIcon />} accent={ACCENT_ORANGE}>
                  Top 5 compradores de entradas
                </PanelTitle>
                <TableContainer>
                  <Table size="small" sx={{ ...tableHeaderSx, ...tableBodySx }}>
                    <TableHead>
                      <TableRow>
                        <TableCell sx={{ width: 50 }}>#</TableCell>
                        <TableCell>Usuario</TableCell>
                        <TableCell>Correo</TableCell>
                        <TableCell align="right">Entradas</TableCell>
                        <TableCell align="right">Total</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {topEntrada.map((u: TopUsuarioEntrada, i: number) => (
                        <TableRow key={u.usuarioId} hover>
                          <TableCell>
                            <RankCell index={i} />
                          </TableCell>
                          <TableCell>
                            <Stack direction="row" alignItems="center" spacing={1}>
                              <Box
                                sx={{
                                  width: 28,
                                  height: 28,
                                  borderRadius: "50%",
                                  background: `linear-gradient(135deg, ${ACCENT_ORANGE}40, ${ACCENT_GOLD}30)`,
                                  border: `1px solid ${ACCENT_ORANGE}40`,
                                  display: "grid",
                                  placeItems: "center",
                                  color: ACCENT_ORANGE,
                                  fontWeight: 800,
                                  fontSize: 12,
                                }}
                              >
                                {u.nombre.charAt(0)}
                                {u.apellido.charAt(0)}
                              </Box>
                              <Typography
                                variant="body2"
                                sx={{ fontWeight: 600, color: "rgba(234,242,255,0.92)" }}
                              >
                                {u.nombre} {u.apellido}
                              </Typography>
                            </Stack>
                          </TableCell>
                          <TableCell>
                            <Typography
                              variant="body2"
                              sx={{ color: "rgba(234,242,255,0.55)", fontSize: 12 }}
                            >
                              {u.correo}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">{u.totalEntradas}</TableCell>
                          <TableCell align="right">
                            <MoneyText value={u.totalGastado} />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </PanelCard>
            )}
          </Box>
        </Box>
      )}
    </Box>
  );
}
