import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock("../../api/config", () => ({ USE_MOCK: true }));
vi.mock("../../api/http", () => ({ http: { get: vi.fn(), post: vi.fn() } }));

import { getPools, obtenerApuesta, obtenerRanking, createPool, joinPool, recalcPoolPoints } from "../../api/poolsApi";
import { mockDb, matchesMock } from "../../api/mockDb";
import { poolsMock } from "../../api/poolsMockDb";

beforeEach(() => {
  mockDb.matches = [...matchesMock];
  poolsMock.length = 1;
  poolsMock[0] = {
    id: "p1", name: "Polla Amigos", code: "AMIGOS2026",
    matchIds: mockDb.matches.map((m) => m.id),
    members: [
      { user: { id: "u1", name: "Sara", stickers: [], repeated: [] }, points: 0 },
      { user: { id: "u2", name: "Juan", stickers: [], repeated: [] }, points: 0 },
    ],
  } as any;
});

describe("recalcPoolPoints", () => {
  it("no lanza error", async () => { await expect(recalcPoolPoints("p1")).resolves.not.toThrow(); });
});

describe("getPools", () => {
  it("retorna las pools del mock", async () => {
    const result = await getPools(1);
    expect(Array.isArray(result)).toBe(true);
    expect(result.length).toBeGreaterThan(0);
  });
});

describe("obtenerApuesta", () => {
  it("retorna la pool con id p1", async () => { expect((await obtenerApuesta("p1" as any) as any).id).toBe("p1"); });
  it("retorna la primera pool si el id no existe", async () => { expect(await obtenerApuesta(999)).toBeDefined(); });
});

describe("obtenerRanking", () => {
  it("retorna array vacío en mock", async () => { expect(await obtenerRanking(1)).toEqual([]); });
});

describe("createPool", () => {
  it("crea una pool y la agrega al mock", async () => {
    const initial = poolsMock.length;
    const result = await createPool("Mi Polla", 1, "2026-07-01");
    expect(poolsMock.length).toBe(initial + 1);
    expect(result.name).toBe("Mi Polla");
  });
  it("asigna estado ABIERTA a la nueva pool", async () => {
    expect((await createPool("Test Pool", 1, "2026-07-01") as any).estado).toBe("ABIERTA");
  });
  it("genera un código de invitación", async () => {
    const result = await createPool("Test Pool", 1, "2026-07-01");
    expect(result.code).toBeTruthy();
    expect(typeof result.code).toBe("string");
  });
  it("lanza error si el nombre está vacío", async () => {
    await expect(createPool("", 1, "2026-07-01")).rejects.toThrow("El nombre de la polla es obligatorio");
  });
  it("lanza error si el nombre es solo espacios", async () => {
    await expect(createPool("   ", 1, "2026-07-01")).rejects.toThrow("El nombre de la polla es obligatorio");
  });
  it("trimea el nombre antes de guardar", async () => {
    expect((await createPool("  Mi Pool  ", 1, "2026-07-01")).name).toBe("Mi Pool");
  });
  it("asigna todos los partidos del mockDb a la nueva pool", async () => {
    expect((await createPool("Test", 1, "2026-07-01") as any).matchIds).toHaveLength(mockDb.matches.length);
  });
});

describe("joinPool", () => {
  it("retorna la pool si el código es correcto", async () => {
    const result = await joinPool("AMIGOS2026", 1);
    expect(result).not.toBeNull();
    expect((result as any).code).toBe("AMIGOS2026");
  });
  it("retorna null si el código no existe", async () => { expect(await joinPool("CODINEXISTENTE", 1)).toBeNull(); });
  it("el código es case-insensitive", async () => { expect(await joinPool("amigos2026", 1)).not.toBeNull(); });
  it("retorna null para código vacío", async () => { expect(await joinPool("", 1)).toBeNull(); });
});