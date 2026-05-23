import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: false }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), put: vi.fn() } }));
vi.mock("../../api/mockDb", () => ({ mockDb: { profiles: [] } }));

import { getMyProfile, updateMyProfile } from "../../api/profileApi";
import { http } from "../../api/http";

const mockGet = vi.mocked(http.get);
const mockPut = vi.mocked(http.put);

beforeEach(() => { vi.clearAllMocks(); });

describe("getMyProfile - modo real", () => {
  it("llama a los dos endpoints correctos", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await getMyProfile("u1");
    expect(mockGet).toHaveBeenCalledWith("/api/usuarios/seleccionesFavoritas");
    expect(mockGet).toHaveBeenCalledWith("/api/usuarios/ciudadesFav");
  });
  it("mapea selecciones favoritas correctamente", async () => {
    mockGet.mockResolvedValueOnce([{ id: 1, nombre: "Argentina" }, { id: 2, nombre: "Colombia" }]).mockResolvedValueOnce([]);
    expect((await getMyProfile("u1")).favoriteTeams).toEqual(["Argentina", "Colombia"]);
  });
  it("mapea ciudades favoritas correctamente", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([{ id: 1, nombre: "Bogotá" }, { id: 2, nombre: "Medellín" }]);
    expect((await getMyProfile("u1")).favoriteCities).toEqual(["Bogotá", "Medellín"]);
  });
  it("retorna userId correcto", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    expect((await getMyProfile("u1")).userId).toBe("u1");
  });
  it("usa el fallback name si se proporciona", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    expect((await getMyProfile("u1", { name: "Carlos" })).name).toBe("Carlos");
  });
  it("usa el fallback email si se proporciona", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    expect((await getMyProfile("u1", { email: "carlos@test.com" })).email).toBe("carlos@test.com");
  });
  it("retorna string vacío si no hay fallback name", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    expect((await getMyProfile("u1")).name).toBe("");
  });
  it("notificationsEnabled es true por defecto", async () => {
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    expect((await getMyProfile("u1")).notificationsEnabled).toBe(true);
  });
});

describe("updateMyProfile - modo real", () => {
  it("llama a http.put con nombre", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await updateMyProfile("u1", { name: "Carlos" });
    expect(mockPut).toHaveBeenCalledWith("/api/usuarios/perfil", { nombre: "Carlos" });
  });
  it("llama a http.put con apellido", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await updateMyProfile("u1", { lastName: "García" });
    expect(mockPut).toHaveBeenCalledWith("/api/usuarios/perfil", { apellido: "García" });
  });
  it("llama a http.put con correoNuevo", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await updateMyProfile("u1", { correoNuevo: "nuevo@test.com" });
    expect(mockPut).toHaveBeenCalledWith("/api/usuarios/perfil", { correoNuevo: "nuevo@test.com" });
  });
  it("llama a http.put con contrasenaActual y contrasenaNueva", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await updateMyProfile("u1", { contrasenaActual: "old", contrasenaNueva: "new" });
    expect(mockPut).toHaveBeenCalledWith("/api/usuarios/perfil", { contrasenaActual: "old", contrasenaNueva: "new" });
  });
  it("llama a getMyProfile después de actualizar", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    mockGet.mockResolvedValueOnce([{ id: 1, nombre: "Argentina" }]).mockResolvedValueOnce([]);
    const result = await updateMyProfile("u1", { name: "Carlos" }, { name: "Carlos" });
    expect(result.favoriteTeams).toEqual(["Argentina"]);
  });
  it("no incluye campos vacíos en el payload", async () => {
    mockPut.mockResolvedValueOnce(undefined);
    mockGet.mockResolvedValueOnce([]).mockResolvedValueOnce([]);
    await updateMyProfile("u1", {});
    expect(mockPut).toHaveBeenCalledWith("/api/usuarios/perfil", {});
  });
});