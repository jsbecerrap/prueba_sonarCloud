import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("firebase/messaging", () => ({ getToken: vi.fn() }));
vi.mock("../../firebaseConfig", () => ({ messaging: {}, VAPID_KEY: "test-key" }));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/notificationApi", () => ({ registrarFcmToken: vi.fn() }));
vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({
  http: { get: vi.fn(), post: vi.fn() },
  setAuthToken: vi.fn(),
  getAuthToken: vi.fn(),
}));

import { loginApi, registerApi, logoutApi, getMeApi } from "../../api/authApi";
import { setAuthToken, getAuthToken } from "../../api/http";

const mockSetAuthToken = vi.mocked(setAuthToken);
const mockGetAuthToken = vi.mocked(getAuthToken);

beforeEach(() => {
  vi.clearAllMocks();
  mockGetAuthToken.mockReturnValue(null);
});

describe("loginApi", () => {
  it("lanza error si username está vacío", async () => { await expect(loginApi("", "password123")).rejects.toThrow("Usuario o correo vacío"); });
  it("lanza error si password está vacío", async () => { await expect(loginApi("usuario", "")).rejects.toThrow("Contraseña vacía"); });
  it("lanza error si username es solo espacios", async () => { await expect(loginApi("   ", "password123")).rejects.toThrow("Usuario o correo vacío"); });
  it("retorna role admin si username contiene admin", async () => { const res = await loginApi("adminuser", "cualquier"); expect(res.user.role).toBe("admin"); });
  it("retorna role admin si password es Admin2026*", async () => { const res = await loginApi("cualquier", "Admin2026*"); expect(res.user.role).toBe("admin"); });
  it("retorna role support si username contiene soporte", async () => { const res = await loginApi("soporte1", "cualquier"); expect(res.user.role).toBe("support"); });
  it("retorna role support si username contiene support", async () => { const res = await loginApi("support_user", "cualquier"); expect(res.user.role).toBe("support"); });
  it("retorna role support si password es Soporte2026*", async () => { const res = await loginApi("cualquier", "Soporte2026*"); expect(res.user.role).toBe("support"); });
  it("retorna role user por defecto", async () => { const res = await loginApi("usuario_normal", "pass"); expect(res.user.role).toBe("user"); });
  it("asigna id u1 a sara", async () => { const res = await loginApi("sara", "pass"); expect(res.user.id).toBe("u1"); });
  it("asigna id u2 a juan", async () => { const res = await loginApi("juan", "pass"); expect(res.user.id).toBe("u2"); });
  it("genera id dinámico para usuario desconocido", async () => { const res = await loginApi("pedro", "pass"); expect(res.user.id).toBe("u_pedro"); });
  it("asigna email si el username contiene @", async () => { const res = await loginApi("user@test.com", "pass"); expect(res.user.email).toBe("user@test.com"); });
  it("retorna token válido", async () => { const res = await loginApi("usuario", "pass"); expect(res.token).toBeTruthy(); });
  it("llama setAuthToken con el token generado", async () => { const res = await loginApi("usuario", "pass"); expect(mockSetAuthToken).toHaveBeenCalledWith(res.token); });
});

describe("registerApi", () => {
  it("lanza error si nombre está vacío", async () => { await expect(registerApi({ name: "", lastName: "Doe", email: "a@b.com", password: "pass" })).rejects.toThrow("Nombre vacío"); });
  it("lanza error si apellido está vacío", async () => { await expect(registerApi({ name: "John", lastName: "", email: "a@b.com", password: "pass" })).rejects.toThrow("Apellido vacío"); });
  it("lanza error si email está vacío", async () => { await expect(registerApi({ name: "John", lastName: "Doe", email: "", password: "pass" })).rejects.toThrow("Correo vacío"); });
  it("lanza error si password está vacío", async () => { await expect(registerApi({ name: "John", lastName: "Doe", email: "a@b.com", password: "" })).rejects.toThrow("Contraseña vacía"); });
  it("retorna user con role user por defecto", async () => { const res = await registerApi({ name: "John", lastName: "Doe", email: "john@test.com", password: "pass" }); expect(res.user.role).toBe("user"); });
  it("respeta el role enviado en payload", async () => { const res = await registerApi({ name: "John", lastName: "Doe", email: "john@test.com", password: "pass", role: "admin" }); expect(res.user.role).toBe("admin"); });
  it("normaliza email a minúsculas", async () => { const res = await registerApi({ name: "John", lastName: "Doe", email: "JOHN@TEST.COM", password: "pass" }); expect(res.user.email).toBe("john@test.com"); });
  it("llama setAuthToken", async () => { await registerApi({ name: "John", lastName: "Doe", email: "john@test.com", password: "pass" }); expect(mockSetAuthToken).toHaveBeenCalled(); });
});

describe("logoutApi", () => {
  it("llama setAuthToken con null", async () => { await logoutApi(); expect(mockSetAuthToken).toHaveBeenCalledWith(null); });
  it("funciona sin pasar user", async () => { await expect(logoutApi()).resolves.not.toThrow(); });
  it("funciona pasando null como user", async () => { await expect(logoutApi(null)).resolves.not.toThrow(); });
  it("funciona pasando un user válido", async () => { await expect(logoutApi({ id: "u1", name: "Sara", role: "user" })).resolves.not.toThrow(); expect(mockSetAuthToken).toHaveBeenCalledWith(null); });
});

describe("getMeApi", () => {
  it("retorna null si no hay token", async () => { mockGetAuthToken.mockReturnValue(null); expect(await getMeApi()).toBeNull(); });
  it("retorna null si el token es inválido", async () => { mockGetAuthToken.mockReturnValue("token_invalido"); expect(await getMeApi()).toBeNull(); });
  it("retorna el user si el token es válido", async () => { const token = btoa(JSON.stringify({ id: "u1", name: "Sara", role: "user" })); mockGetAuthToken.mockReturnValue(token); const res = await getMeApi(); expect(res?.id).toBe("u1"); });
  it("normaliza role ROLE_ADMIN a admin", async () => { const token = btoa(JSON.stringify({ id: "u_admin", name: "Admin", role: "ROLE_ADMIN" })); mockGetAuthToken.mockReturnValue(token); const res = await getMeApi(); expect(res?.role).toBe("admin"); });
  it("retorna null si el token decodificado no tiene id", async () => { const token = btoa(JSON.stringify({ name: "Sin ID", role: "user" })); mockGetAuthToken.mockReturnValue(token); expect(await getMeApi()).toBeNull(); });
});