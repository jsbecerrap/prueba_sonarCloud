import { useCallback, useEffect, useState } from "react";
import {
  Alert,
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
import {
  addPaymentMethod,
  deletePaymentMethod,
  getMyPaymentMethods,
  
  
  setDefaultPaymentMethod,
  updatePaymentMethod,
} from "../api/paymentsApi";
import { useApp } from "../context/AppContext";
import type { PaymentMethod, PaymentMethodType } from "../types/payment";
import {
  validatePaymentReference,
  validateTextLength,
  type FieldErrors,
} from "../utils/validation";
import { useNavigate } from "react-router-dom";
type PaymentField = "label" | "details";
type Msg = { text: string; severity: "success" | "error" | "info" } | null;


export default function Payments() {
  const { user } = useApp();
  const navigate = useNavigate();
  const [methods, setMethods] = useState<PaymentMethod[]>([]);
 
  const [type, setType] = useState<PaymentMethodType>("CARD");
  const [label, setLabel] = useState("");
  const [details, setDetails] = useState("");
  const [errors, setErrors] = useState<FieldErrors<PaymentField>>({});
  const [msg, setMsg] = useState<Msg>(null);
  const [loading, setLoading] = useState(false);
const [editDialogOpen, setEditDialogOpen] = useState(false);
const [editMethodId, setEditMethodId] = useState("");
const [editLabel, setEditLabel] = useState("");
const [editType, setEditType] = useState<PaymentMethodType>("CARD");
const [editDetails, setEditDetails] = useState("");
  const refresh = useCallback(async () => {
    if (!user) return;
    const paymentMethods = await getMyPaymentMethods(user.id);
setMethods(paymentMethods ?? []);
  }, [user]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  if (!user) return <Alert severity="warning">Debes iniciar sesión.</Alert>;

  const validateForm = () => {
    const nextErrors: FieldErrors<PaymentField> = {
      label: validateTextLength(label, "El nombre del método", 4, 40),
      details: validatePaymentReference(details, "La referencia"),
    };

   if (type === "CARD" && !/\d{4}/.test(details) && !details.startsWith("pm_")) {
  nextErrors.details = "La referencia de tarjeta debe incluir al menos 4 números.";
}

    Object.keys(nextErrors).forEach((key) => {
      const typedKey = key as PaymentField;
      if (!nextErrors[typedKey]) delete nextErrors[typedKey];
    });

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const onAddMethod = async () => {
    if (!validateForm()) return;

    try {
      setLoading(true);
      setMsg(null);

      await addPaymentMethod(user.id, type, label, details);
      setLabel("");
      setDetails("");
      setType("CARD");
      setMsg({ text: "Método de pago agregado.", severity: "success" });
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "No se pudo agregar el método de pago.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };

  const onSetDefault = async (paymentId: string) => {
    try {
      setLoading(true);
      await setDefaultPaymentMethod(user.id, paymentId);
      setMsg({ text: "Método predeterminado actualizado.", severity: "success" });
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "No se pudo actualizar el método.";
      setMsg({ text: message, severity: "error" });
    } finally {
      setLoading(false);
    }
  };
const onDelete = async (paymentId: string) => {
  if (!confirm("¿Seguro que quieres eliminar este método de pago?")) return;
  try {
    setLoading(true);
    await deletePaymentMethod(user.id, paymentId);
    setMsg({ text: "Método de pago eliminado.", severity: "success" });
    await refresh();
  } catch (e) {
    const message = e instanceof Error ? e.message : "No se pudo eliminar el método.";
    setMsg({ text: message, severity: "error" });
  } finally {
    setLoading(false);
  }
};

const onOpenEdit = (method: PaymentMethod) => {
  setEditMethodId(method.id);
  setEditLabel(method.label);
  setEditType(method.type);
  setEditDetails(method.details ?? "");
  setEditDialogOpen(true);
};

const onUpdate = async () => {
  try {
    setLoading(true);
    setEditDialogOpen(false);
    await updatePaymentMethod(user.id, editMethodId, editType, editLabel, editDetails);
    setMsg({ text: "Método actualizado correctamente.", severity: "success" });
    await refresh();
  } catch (e) {
    const message = e instanceof Error ? e.message : "No se pudo actualizar el método.";
    setMsg({ text: message, severity: "error" });
  } finally {
    setLoading(false);
  }
};

  return (
    <Stack spacing={2}>
      <Typography variant="h5">Pagos</Typography>

      <Alert severity="info">
        Administra métodos de pago sandbox, consulta transacciones y solicita reembolsos con
        trazabilidad.
      </Alert>

      {msg && <Alert severity={msg.severity}>{msg.text}</Alert>}

      <Paper sx={{ p: 2.5 }}>
        <Typography variant="h6">Agregar método</Typography>
        <Stack direction={{ xs: "column", md: "row" }} spacing={1.5} sx={{ mt: 2 }}>
          <TextField
            select
            label="Tipo"
            value={type}
            onChange={(event) => setType(event.target.value as PaymentMethodType)}
            disabled={loading}
            sx={{ minWidth: { md: 160 } }}
          >
            <MenuItem value="CARD">Tarjeta</MenuItem>
            <MenuItem value="PSE">PSE</MenuItem>
            <MenuItem value="TRANSFER">Transferencia</MenuItem>
            <MenuItem value="CASH">Efectivo</MenuItem>
          </TextField>
          <TextField
            label="Nombre"
            value={label}
            onChange={(event) => setLabel(event.target.value)}
            error={Boolean(errors.label)}
            helperText={errors.label || "Ejemplo: Visa personal"}
            disabled={loading}
            fullWidth
          />
          <TextField
            label="Referencia"
            value={details}
            onChange={(event) => setDetails(event.target.value)}
            error={Boolean(errors.details)}
            helperText={errors.details || "Ejemplo: 4111 **** 1111"}
            disabled={loading}
            fullWidth
          />
          <Button variant="contained" onClick={onAddMethod} disabled={loading}>
            Agregar
          </Button>
        </Stack>
      </Paper>

      <Paper sx={{ p: 2.5 }}>
        <Typography variant="h6">Métodos guardados</Typography>
        {methods.length === 0 ? (
          <Typography color="text.secondary" sx={{ mt: 1 }}>
            No tienes métodos guardados.
          </Typography>
        ) : (
          <Stack spacing={1.5} sx={{ mt: 2 }}>
            {methods.map((method) => (
              <Paper key={method.id} variant="outlined" sx={{ p: 2 }}>
                <Stack direction={{ xs: "column", sm: "row" }} spacing={1} justifyContent="space-between">
                  <Stack spacing={0.5}>
                    <Typography sx={{ fontWeight: 800 }}>{method.label}</Typography>
                    <Typography color="text.secondary">
                      {method.type} {method.details ? `· ${method.details}` : ""}
                    </Typography>
                    {method.isDefault && <Chip label="Predeterminado" size="small" color="success" />}
                  </Stack>
                  <Stack direction="row" spacing={1}>
  {!method.isDefault && (
    <Button variant="outlined" onClick={() => onSetDefault(method.id)} disabled={loading}>
      Usar por defecto
    </Button>
  )}
  <Button variant="outlined" onClick={() => onOpenEdit(method)} disabled={loading}>
    Editar
  </Button>
  <Button variant="outlined" color="error" onClick={() => onDelete(method.id)} disabled={loading}>
    Eliminar
  </Button>
</Stack>
                </Stack>
              </Paper>
            ))}
          </Stack>
        )}
      </Paper>

   <Paper sx={{ p: 2.5 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Stack spacing={0.25}>
            <Typography variant="h6">Transacciones</Typography>
            <Typography color="text.secondary" variant="body2">
              Consulta el historial completo de tus pagos.
            </Typography>
          </Stack>
          <Button variant="outlined" onClick={() => navigate("/transactions")}>
            Ver historial
          </Button>
        </Stack>
      </Paper>
          
      
  <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)}>
        <DialogTitle>Editar método de pago</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1, minWidth: 320 }}>
            <TextField
              select
              label="Tipo"
              value={editType}
              onChange={(e) => setEditType(e.target.value as PaymentMethodType)}
            >
              <MenuItem value="CARD">Tarjeta</MenuItem>
              <MenuItem value="PSE">PSE</MenuItem>
              <MenuItem value="TRANSFER">Transferencia</MenuItem>
              <MenuItem value="CASH">Efectivo</MenuItem>
            </TextField>
            <TextField
              label="Nombre"
              value={editLabel}
              onChange={(e) => setEditLabel(e.target.value)}
            />
            <TextField
              label="Referencia"
              value={editDetails}
              onChange={(e) => setEditDetails(e.target.value)}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancelar</Button>
          <Button variant="contained" onClick={onUpdate} disabled={loading}>
            Guardar
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}