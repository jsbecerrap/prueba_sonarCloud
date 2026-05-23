import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), put: vi.fn() } }));

import { getMyProfile, updateMyProfile } from "../../api/profileApi";
import { mockDb } from "../../api/mockDb";

beforeEach(() => {
  mockDb.profiles = [];
});

describe("getMyProfile", () => {
  it("crea un perfil nuevo si no existe", async () => {
    const result = await getMyProfile("u_nuevo");
    expect(result.userId).toBe("u_nuevo");
    expect(mockDb.profiles).toHaveLength(1);
  });
  it("retorna el perfil existente sin crear uno nuevo", async () => {
    await getMyProfile("u1");
    await getMyProfile("u1");
    expect(mockDb.profiles).toHaveLength(1);
  });
  it("usa el fallback name si se proporciona", async () => {
    expect((await getMyProfile("u_nuevo", { name: "Carlos" })).name).toBe("Carlos");
  });
  it("usa el fallback lastName si se proporciona", async () => {
    expect((await getMyProfile("u_nuevo", { lastName: "Pérez" })).lastName).toBe("Pérez");
  });
  it("usa el fallback email si se proporciona", async () => {
    expect((await getMyProfile("u_nuevo", { email: "carlos@test.com" })).email).toBe("carlos@test.com");
  });
  it("usa Usuario como nombre por defecto si no hay fallback", async () => {
    expect((await getMyProfile("u_sin_fallback")).name).toBe("Usuario");
  });
  it("inicializa favoriteTeams como array vacío", async () => {
    expect((await getMyProfile("u_nuevo")).favoriteTeams).toEqual([]);
  });
  it("inicializa favoriteCities como array vacío", async () => {
    expect((await getMyProfile("u_nuevo")).favoriteCities).toEqual([]);
  });
  it("inicializa notificationsEnabled como true", async () => {
    expect((await getMyProfile("u_nuevo")).notificationsEnabled).toBe(true);
  });
  it("no sobreescribe el perfil si ya existe", async () => {
    await getMyProfile("u1", { name: "Carlos" });
    expect((await getMyProfile("u1", { name: "Otro nombre" })).name).toBe("Carlos");
  });
});

describe("updateMyProfile", () => {
  it("actualiza el nombre del perfil", async () => {
    await getMyProfile("u1", { name: "Carlos" });
    expect((await updateMyProfile("u1", { name: "Nuevo Nombre" })).name).toBe("Nuevo Nombre");
  });
  it("actualiza el apellido del perfil", async () => {
    await getMyProfile("u1");
    expect((await updateMyProfile("u1", { lastName: "García" })).lastName).toBe("García");
  });
  it("actualiza favoriteTeams", async () => {
    await getMyProfile("u1");
    expect((await updateMyProfile("u1", { favoriteTeams: ["Argentina", "Colombia"] })).favoriteTeams).toEqual(["Argentina", "Colombia"]);
  });
  it("actualiza favoriteCities", async () => {
    await getMyProfile("u1");
    expect((await updateMyProfile("u1", { favoriteCities: ["Bogotá", "Medellín"] })).favoriteCities).toEqual(["Bogotá", "Medellín"]);
  });
  it("actualiza notificationsEnabled", async () => {
    await getMyProfile("u1");
    expect((await updateMyProfile("u1", { notificationsEnabled: false })).notificationsEnabled).toBe(false);
  });
  it("actualiza el updatedAt al modificar", async () => {
    await getMyProfile("u1");
    const before = new Date().toISOString();
    expect((await updateMyProfile("u1", { name: "Test" })).updatedAt >= before).toBe(true);
  });
  it("crea el perfil si no existe y luego lo actualiza", async () => {
    const result = await updateMyProfile("u_inexistente", { name: "Nuevo" }, { name: "Fallback" });
    expect(result.userId).toBe("u_inexistente");
    expect(result.name).toBe("Nuevo");
  });
  it("actualiza varios campos a la vez", async () => {
    await getMyProfile("u1");
    const result = await updateMyProfile("u1", { name: "Ana", lastName: "López", notificationsEnabled: false });
    expect(result.name).toBe("Ana");
    expect(result.lastName).toBe("López");
    expect(result.notificationsEnabled).toBe(false);
  });
  it("persiste los cambios en mockDb", async () => {
    await getMyProfile("u1");
    await updateMyProfile("u1", { name: "Persistido" });
    expect(mockDb.profiles.find((p) => p.userId === "u1")?.name).toBe("Persistido");
  });
});