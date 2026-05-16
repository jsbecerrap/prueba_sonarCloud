import {
  Box,
  Button,
  Checkbox,
  Chip,
  CircularProgress,
  Alert,
  FormControlLabel,
  InputAdornment,
  Paper,
  Slider,
  Snackbar,
  Stack,
  TextField,
  Typography,
  Divider,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import { useEffect, useMemo, useState } from "react";

import { useNavigate } from "react-router-dom";

import { bannerImages } from "../theme/bannerImages";
import { getProductos, getCategorias, agregarAlCarrito } from "../api/storeApi";
import type { ProductoResponse, CategoriaResponse } from "../api/storeApi";

function formatPrecio(value: number) {
  return `$${value.toLocaleString("es-CO")} COP`;
}
function ProductCard({
  product,
  onAdd,
  loading,
  quantity,
  onChangeQty,
  varianteSeleccionada,
  onChangeVariante,
}: {

  product: ProductoResponse;
  onAdd: (product: ProductoResponse, qty: number) => void;
  loading: boolean;
  quantity: number;
  onChangeQty: (productId: number, delta: number, maxStock: number) => void;
  varianteSeleccionada: number | null;
  onChangeVariante: (productId: number, varianteId: number) => void;
}) {
  return (
    <Paper
      variant="outlined"
      sx={{
        p: 1.5,
        display: "flex",
        flexDirection: "column",
        gap: 1,
        backgroundColor: product.destacado ? "rgba(40, 28, 4, 0.78)" : undefined,
        borderColor: product.destacado ? "rgba(255, 209, 102, 0.42)" : undefined,
      }}
    >
    <Box
        sx={{
  height: 290,
  borderRadius: 1.5,
  backgroundImage: `url(${product.imagenUrl})`,
  backgroundSize: "cover",
  backgroundPosition: "center",
  mb: 1.5,
}}
      />
      <Stack direction="row" spacing={0.75} useFlexGap flexWrap="wrap">
        <Chip label={product.categoriaNombre} size="small" />
        {product.equipo && (
          <Chip
            label={`${product.bandera ?? ""} ${product.equipo}`}
            size="small"
            variant="outlined"
          />
        )}
       
        {product.destacado && (
          <Chip label=" Destacado" size="small" color="warning" />
        )}
      </Stack>

      <Typography sx={{ fontWeight: 900, fontSize: "0.95rem" }}>
        {product.nombre}
      </Typography>
      <Typography color="text.secondary" variant="body2" sx={{ flexGrow: 1 }}>
        {product.descripcion}
      </Typography>
      <Typography variant="h6" sx={{ fontWeight: 900 }}>
        {formatPrecio(product.precio)}
      </Typography>
{product.variantes.length > 1 && (
  <Stack direction="row" spacing={0.75} flexWrap="wrap" useFlexGap>
    {product.variantes.map((v) => (
      <Chip
        key={v.id}
        label={v.especificacion ?? "Única"}
        size="small"
        variant={varianteSeleccionada === v.id ? "filled" : "outlined"}
        color={varianteSeleccionada === v.id ? "primary" : "default"}
        disabled={v.stock === 0}
        onClick={() => onChangeVariante(product.id, v.id)}
        sx={{ cursor: "pointer" }}
      />
    ))}
  </Stack>
)}
      {(() => {
        const variante = product.variantes.find((v) => v.id === varianteSeleccionada) ?? product.variantes[0];
        const stockVariante = variante?.stock ?? 0;
        if (product.stockTotal === 0) return <Button variant="outlined" disabled size="small">Sin stock</Button>;
        if (quantity === 0) return (
          <Button variant="contained" size="small"
            disabled={loading || (!varianteSeleccionada && product.variantes.length > 1)}
            onClick={() => onChangeQty(product.id, 1, stockVariante)}>
            Agregar al carrito
          </Button>
        );
        return (
          <Stack spacing={0.75}>
            <Stack direction="row" spacing={1} alignItems="center">
              <Button variant="outlined" size="small"
                sx={{ minWidth: 32, width: 32, height: 32, borderRadius: "50%", p: 0 }}
                onClick={() => onChangeQty(product.id, -1, stockVariante)}>−</Button>
              <Typography sx={{ fontWeight: 900, minWidth: 20, textAlign: "center" }}>{quantity}</Typography>
              <Button variant="outlined" size="small"
                sx={{ minWidth: 32, width: 32, height: 32, borderRadius: "50%", p: 0 }}
                disabled={quantity >= stockVariante}
                onClick={() => onChangeQty(product.id, 1, stockVariante)}>+</Button>
              <Typography variant="caption" color="text.secondary">máx. {stockVariante}</Typography>
            </Stack>
            <Button variant="contained" size="small" disabled={loading} onClick={() => onAdd(product, quantity)}>Confirmar</Button>
          </Stack>
        );
      })()}
    </Paper>
  );
}

export default function Store() {
  const navigate = useNavigate();
  const [productos, setProductos] = useState<ProductoResponse[]>([]);
  const [categorias, setCategorias] = useState<CategoriaResponse[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [agregando, setAgregando] = useState<number | null>(null);
  const [cantidades, setCantidades] = useState<Record<number, number>>({});
  const [snackbar, setSnackbar] = useState<{ open: boolean; msg: string }>({
    open: false,
    msg: "",
  });

  // Filtros
  const [busqueda, setBusqueda] = useState("");
  const [categoriasSeleccionadas, setCategoriasSeleccionadas] = useState<string[]>([]);
  const [rangoPrecios, setRangoPrecios] = useState<[number, number]>([0, 0]);
  const [precioMax, setPrecioMax] = useState(0);
  const [ordenar, setOrdenar] = useState<string>("destacados");
const [limite, setLimite] = useState(12);
  useEffect(() => {
    const cargar = async () => {
      try {
        const [prods, cats] = await Promise.all([getProductos(), getCategorias()]);
        setProductos(prods);
        setCategorias(cats);
        if (prods.length > 0) {
          const max = Math.max(...prods.map((p) => p.precio));
          setPrecioMax(max);
          setRangoPrecios([0, max]);
        }
      } catch (e) {
        setError((e as Error).message);
      } finally {
        setCargando(false);
      }
    };
    cargar();
  }, []);

  const [variantesSeleccionadas, setVariantesSeleccionadas] = useState<Record<number, number>>({});

const handleConfirmar = async (product: ProductoResponse, qty: number) => {
    try {
      setAgregando(product.id);
    const varianteId = variantesSeleccionadas[product.id] ?? product.variantes[0]?.id;
if (!varianteId) return;
await agregarAlCarrito({ productoId: product.id, varianteId, cantidad: qty });
      setCantidades((prev) => {
        const next = { ...prev };
        delete next[product.id];
        return next;
      });
      setSnackbar({ open: true, msg: `"${product.nombre}" agregado al carrito.` });
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setAgregando(null);
    }
  };



const handleChangeQty = (productId: number, delta: number, maxStock: number) => {
  setCantidades((prev) => {
    const actual = prev[productId] ?? 0;
    const nueva = actual + delta;
    if (nueva <= 0) {
      const next = { ...prev };
      delete next[productId];
      return next;
    }
    if (nueva > maxStock) return prev;
    return { ...prev, [productId]: nueva };
  });
};

  const toggleCategoria = (nombre: string) => {
  setCategoriasSeleccionadas((prev) =>
    prev.includes(nombre) ? prev.filter((c) => c !== nombre) : [...prev, nombre]
  );
  setLimite(12);
};

  const limpiarFiltros = () => {
    setBusqueda("");
    setCategoriasSeleccionadas([]);
    setRangoPrecios([0, precioMax]);
    setOrdenar("destacados");
    setLimite(12);
  };

  const productosFiltrados = useMemo(() => {
    let resultado = [...productos];

    if (busqueda.trim()) {
      const q = busqueda.toLowerCase();
      resultado = resultado.filter(
        (p) =>
          p.nombre.toLowerCase().includes(q) ||
          p.descripcion.toLowerCase().includes(q) ||
          p.categoriaNombre.toLowerCase().includes(q) ||
          (p.equipo?.toLowerCase().includes(q) ?? false)
      );
    }

    if (categoriasSeleccionadas.length > 0) {
      resultado = resultado.filter((p) =>
        categoriasSeleccionadas.includes(p.categoriaNombre)
      );
    }

    resultado = resultado.filter(
      (p) => p.precio >= rangoPrecios[0] && p.precio <= rangoPrecios[1]
    );

    if (ordenar === "destacados") {
      resultado = resultado.sort((a, b) => (b.destacado ? 1 : 0) - (a.destacado ? 1 : 0));
    } else if (ordenar === "precio_asc") {
      resultado = resultado.sort((a, b) => a.precio - b.precio);
    } else if (ordenar === "precio_desc") {
      resultado = resultado.sort((a, b) => b.precio - a.precio);
    } else if (ordenar === "nombre") {
      resultado = resultado.sort((a, b) => a.nombre.localeCompare(b.nombre));
    }

    return resultado;
  }, [productos, busqueda, categoriasSeleccionadas, rangoPrecios, ordenar]);

  const hayFiltrosActivos =
    busqueda.trim() !== "" ||
    categoriasSeleccionadas.length > 0 ||
    rangoPrecios[0] > 0 ||
    rangoPrecios[1] < precioMax;

  if (cargando) {
    return (
      <Stack alignItems="center" justifyContent="center" sx={{ minHeight: 300 }}>
        <CircularProgress />
      </Stack>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }
const productosPaginados = productosFiltrados.slice(0, limite);
  return (
    <Stack spacing={3}>
      {/* Banner */}
      <Paper
        sx={{
          p: { xs: 2.5, md: 3.5 },
          background: `linear-gradient(135deg, rgba(67,45,4,.92), rgba(142,106,27,.78)), url(${bannerImages.store})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          minHeight: 220,
          display: "flex",
          alignItems: "flex-end",
        }}
      >
        <Stack spacing={1} sx={{ maxWidth: 640 }}>
          <Chip label="Tienda oficial" sx={{ alignSelf: "flex-start" }} />
          <Typography variant="h4" sx={{ fontWeight: 950 }}>
            Tienda de souvenirs
          </Typography>
          <Typography sx={{ color: "rgba(234,242,255,.86)" }}>
            Camisetas, accesorios y piezas de colección del Mundial 2026.
          </Typography>
        </Stack>
      </Paper>

      {/* Layout principal */}
      <Stack direction={{ xs: "column", md: "row" }} spacing={2} alignItems="flex-start">

        {/* Panel de filtros */}
        <Paper
          variant="outlined"
          sx={{
            width: { xs: "100%", md: 260 },
            flexShrink: 0,
            p: 2,
            position: { md: "sticky" },
            top: { md: 16 },
          }}
        >
          <Stack spacing={2}>
            <Stack direction="row" justifyContent="space-between" alignItems="center">
              <Typography variant="subtitle1" sx={{ fontWeight: 900 }}>
                Filtros
              </Typography>
              {hayFiltrosActivos && (
                <Button size="small" onClick={limpiarFiltros}>
                  Limpiar
                </Button>
              )}
            </Stack>

            <Divider />

            {/* Buscador */}
            <TextField
              placeholder="Buscar producto..."
              size="small"
              fullWidth
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" />
                  </InputAdornment>
                ),
              }}
            />

            <Divider />

            {/* Categorías */}
            <Box>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 0.5 }}>
                Categorías
              </Typography>
              <Stack>
                {categorias.map((cat) => (
                  <FormControlLabel
                    key={cat.id}
                    control={
                      <Checkbox
                        size="small"
                        checked={categoriasSeleccionadas.includes(cat.nombre)}
                        onChange={() => toggleCategoria(cat.nombre)}
                      />
                    }
                    label={
                      <Typography variant="body2">
                        {cat.nombre}{" "}
                        <Typography component="span" variant="caption" color="text.secondary">
                          ({productos.filter((p) => p.categoriaNombre === cat.nombre).length})
                        </Typography>
                      </Typography>
                    }
                  />
                ))}
              </Stack>
            </Box>

            <Divider />

            {/* Rango de precio */}
            <Box>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>
                Precio
              </Typography>
              <Slider
                value={rangoPrecios}
                onChange={(_, val) => setRangoPrecios(val as [number, number])}
                min={0}
                max={precioMax}
                step={Math.ceil(precioMax / 100) * 5}
                valueLabelDisplay="auto"
                valueLabelFormat={(v) => `$${v.toLocaleString("es-CO")}`}
                size="small"
              />
              <Stack direction="row" justifyContent="space-between">
                <Typography variant="caption" color="text.secondary">
                  ${rangoPrecios[0].toLocaleString("es-CO")}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  ${rangoPrecios[1].toLocaleString("es-CO")}
                </Typography>
              </Stack>
            </Box>
          </Stack>
        </Paper>

        {/* Contenido derecho */}
        <Stack spacing={2} sx={{ flex: 1, minWidth: 0 }}>

          {/* Barra superior */}
          <Stack
            direction={{ xs: "column", sm: "row" }}
            justifyContent="space-between"
            alignItems={{ xs: "flex-start", sm: "center" }}
            spacing={1}
          >
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
              <Typography color="text.secondary" variant="body2">
                {productosFiltrados.length} de {productos.length} productos
              </Typography>
              {categoriasSeleccionadas.map((cat) => (
                <Chip
                  key={cat}
                  label={cat}
                  size="small"
                  onDelete={() => toggleCategoria(cat)}
                />
              ))}
              {busqueda.trim() && (
                <Chip
                  label={`"${busqueda}"`}
                  size="small"
                  onDelete={() => setBusqueda("")}
                />
              )}
            </Stack>

            <Stack direction="row" spacing={1} alignItems="center">
              <FormControl size="small" sx={{ minWidth: 160 }}>
                <InputLabel>Ordenar por</InputLabel>
                <Select
                  value={ordenar}
                  label="Ordenar por"
                  onChange={(e) => setOrdenar(e.target.value)}
                >
                  <MenuItem value="destacados">Destacados primero</MenuItem>
                  <MenuItem value="precio_asc">Precio: menor a mayor</MenuItem>
                  <MenuItem value="precio_desc">Precio: mayor a menor</MenuItem>
                  <MenuItem value="nombre">Nombre A-Z</MenuItem>
                </Select>
              </FormControl>
              <Button variant="outlined" size="small" onClick={() => navigate("/cart")}>
                Ver carrito
              </Button>
            </Stack>
          </Stack>

          {/* Grid de productos */}
          {productosFiltrados.length === 0 ? (
            <Paper variant="outlined" sx={{ p: 4, textAlign: "center" }}>
              <Typography color="text.secondary">
                No hay productos que coincidan con los filtros.
              </Typography>
              <Button size="small" sx={{ mt: 1 }} onClick={limpiarFiltros}>
                Limpiar filtros
              </Button>
            </Paper>
          ) : (
            <Box
              sx={{
                display: "grid",
                gridTemplateColumns: {
                  xs: "1fr",
                  sm: "repeat(2, 1fr)",
                  lg: "repeat(3, 1fr)",
                },
                gap: 2,
              }}
            >
              {productosPaginados.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onAdd={handleConfirmar}
                  loading={agregando === product.id}
                  quantity={cantidades[product.id] ?? 0}
                  onChangeQty={handleChangeQty}
varianteSeleccionada={variantesSeleccionadas[product.id] ?? product.variantes[0]?.id ?? null}
onChangeVariante={(pid, vid) => setVariantesSeleccionadas((prev) => ({ ...prev, [pid]: vid }))}
/>
              ))}
            </Box>
          )}
          {productosFiltrados.length > limite && (
            <Stack alignItems="center" sx={{ mt: 2 }}>
              <Button variant="outlined" onClick={() => setLimite((prev) => prev + 12)}>
                Ver más ({productosFiltrados.length - limite} restantes)
              </Button>
            </Stack>
          )}
        </Stack>
      </Stack>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ open: false, msg: "" })}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
        message={snackbar.msg}
        action={
          <Button color="inherit" size="small" onClick={() => navigate("/cart")}>
            Ver carrito
          </Button>
        }
      />
    </Stack>
  );
}
