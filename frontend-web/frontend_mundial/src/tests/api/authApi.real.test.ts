import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("firebase/messaging", () => ({ getToken: vi.fn() }));
vi.mock("../../firebaseConfig", () => ({ messaging: {}, VAPID_KEY: "test-key" }));
vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({
  http: { get: vi.fn(), post: vi.fn() },
  setAuthToken: vi.fn(),
  getAuthToken: vi.fn(),
}));
vi.mock("../../api/eventsApi", () => ({ createSystemEvent: vi.fn() }));
vi.mock("../../api/notificationApi", () => ({ registrarFcmToken: vi.fn() }));
vi.mock("../../context/AppContext", () => ({}));

import { loginApi, registerApi, logoutApi, getMeApi } from "../../api/authApi";
import { http, setAuthToken, getAuthToken } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPost = vi.mocked(http.post);
const mockSetAuthToken = vi.mocked(setAuthToken);
const mockGetAuthToken = vi.mocked(getAuthToken);

const backendPerfil = (overrides: Record<string, any> = {}) => ({
  id: 1, correoUsuario: "user@test.com", nombre: "Juan", apellido: "García", rol: "ROLE_USER", ...overrides,
});

beforeEach(() => { vi.clearAllMocks(); mockGetAuthToken.mockReturnValue(null); });

describe("loginApi - modo real", () => {
  it("llama al endpoint de login con credenciales correctas", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt_token_123" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    await loginApi("user@test.com", "password123");
    expect(mockPost).toHaveBeenCalledWith("/login", { correoUsuario: "user@test.com", contrasena: "password123" });
  });
  it("llama a setAuthToken con el token recibido", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt_token_123" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    await loginApi("user@test.com", "pass");
    expect(mockSetAuthToken).toHaveBeenCalledWith("jwt_token_123");
  });
  it("llama al endpoint de perfil después del login", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    await loginApi("user@test.com", "pass");
    expect(mockGet).toHaveBeenCalledWith("/api/usuarios/perfil");
  });
  it("normaliza ROLE_USER a user", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil({ rol: "ROLE_USER" }));
    expect((await loginApi("user@test.com", "pass")).user.role).toBe("user");
  });
  it("normaliza ROLE_ADMIN a admin", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil({ rol: "ROLE_ADMIN" }));
    expect((await loginApi("admin@test.com", "pass")).user.role).toBe("admin");
  });
  it("normaliza ROLE_SUPPORT a support", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil({ rol: "ROLE_SUPPORT" }));
    expect((await loginApi("soporte@test.com", "pass")).user.role).toBe("support");
  });
  it("retorna token y user correctamente", async () => {
    mockPost.mockResolvedValueOnce({ token: "mi_token" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    const result = await loginApi("user@test.com", "pass");
    expect(result.token).toBe("mi_token");
    expect(result.user.id).toBe("1");
  });
  it("trimea el username antes de enviar", async () => {
    mockPost.mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    await loginApi("  user@test.com  ", "pass");
    expect(mockPost).toHaveBeenCalledWith("/login", { correoUsuario: "user@test.com", contrasena: "pass" });
  });
  it("lanza error si username está vacío", async () => {
    await expect(loginApi("", "pass")).rejects.toThrow("Usuario o correo vacío");
  });
  it("lanza error si password está vacío", async () => {
    await expect(loginApi("user@test.com", "")).rejects.toThrow("Contraseña vacía");
  });
});

describe("registerApi - modo real", () => {
  it("llama al endpoint de registro con los datos correctos", async () => {
    mockPost.mockResolvedValueOnce(undefined).mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    await registerApi({ name: "Juan", lastName: "García", email: "juan@test.com", password: "pass" });
    expect(mockPost).toHaveBeenCalledWith("/api/usuarios/registrar", { correoUsuario: "juan@test.com", contrasena: "pass", nombre: "Juan", apellido: "García", rol: "ROLE_USUARIO" });
  });
  it("normaliza el email a minúsculas", async () => {
    mockPost.mockResolvedValueOnce(undefined).mockResolvedValueOnce({ token: "jwt" });
    mockGet.mockResolvedValueOnce(backendPerfil());
    await registerApi({ name: "Juan", lastName: "García", email: "JUAN@TEST.COM", password: "pass" });
    expect(mockPost).toHaveBeenCalledWith("/api/usuarios/registrar", expect.objectContaining({ correoUsuario: "juan@test.com" }));
  });
  it("lanza error si nombre está vacío", async () => { await expect(registerApi({ name: "", lastName: "G", email: "j@t.com", password: "p" })).rejects.toThrow("Nombre vacío"); });
  it("lanza error si apellido está vacío", async () => { await expect(registerApi({ name: "J", lastName: "", email: "j@t.com", password: "p" })).rejects.toThrow("Apellido vacío"); });
  it("lanza error si email está vacío", async () => { await expect(registerApi({ name: "J", lastName: "G", email: "", password: "p" })).rejects.toThrow("Correo vacío"); });
  it("lanza error si password está vacío", async () => { await expect(registerApi({ name: "J", lastName: "G", email: "j@t.com", password: "" })).rejects.toThrow("Contraseña vacía"); });
});

describe("logoutApi - modo real", () => {
  it("llama al endpoint de logout", async () => {
    mockPost.mockResolvedValueOnce(undefined);
    await logoutApi();
    expect(mockPost).toHaveBeenCalledWith("/api/auth/logout");
  });
  it("llama a setAuthToken con null", async () => {
    mockPost.mockResolvedValueOnce(undefined);
    await logoutApi();
    expect(mockSetAuthToken).toHaveBeenCalledWith(null);
  });
  it("no lanza error si el endpoint falla", async () => {
    mockPost.mockRejectedValueOnce(new Error("Network error"));
    await expect(logoutApi()).resolves.not.toThrow();
    expect(mockSetAuthToken).toHaveBeenCalledWith(null);
  });
});

describe("getMeApi - modo real", () => {
  it("retorna null si no hay token", async () => { expect(await getMeApi()).toBeNull(); });
  it("llama al endpoint de perfil si hay token", async () => {
    mockGetAuthToken.mockReturnValue("jwt_token");
    mockGet.mockResolvedValueOnce(backendPerfil());
    await getMeApi();
    expect(mockGet).toHaveBeenCalledWith("/api/usuarios/perfil");
  });
  it("mapea el perfil correctamente", async () => {
    mockGetAuthToken.mockReturnValue("jwt_token");
    mockGet.mockResolvedValueOnce(backendPerfil({ nombre: "Sara", apellido: "Díaz", rol: "ROLE_USER" }));
    const result = await getMeApi();
    expect(result?.name).toBe("Sara");
    expect(result?.role).toBe("user");
  });
  it("retorna null y limpia token si el endpoint falla", async () => {
    mockGetAuthToken.mockReturnValue("token_invalido");
    mockGet.mockRejectedValueOnce(new Error("Unauthorized"));
    expect(await getMeApi()).toBeNull();
    expect(mockSetAuthToken).toHaveBeenCalledWith(null);
  });
  it("normaliza ROLE_ADMIN a admin", async () => {
    mockGetAuthToken.mockReturnValue("jwt_token");
    mockGet.mockResolvedValueOnce(backendPerfil({ rol: "ROLE_ADMIN" }));
    expect((await getMeApi())?.role).toBe("admin");
  });
});