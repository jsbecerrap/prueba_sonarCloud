import { describe, it, expect } from "vitest";
import { getTeamFlag, formatTeam, getCountryFlag } from "../../utils/countries";
import type { Team } from "../../types/match";

const team = (name: string, code?: string): Team => ({ id: "t1", name, code });

describe("getTeamFlag", () => {
  it("retorna bandera por código ARG", () => expect(getTeamFlag(team("Argentina", "ARG"))).toBe("🇦🇷"));
  it("retorna bandera por código BRA", () => expect(getTeamFlag(team("Brasil", "BRA"))).toBe("🇧🇷"));
  it("retorna bandera por código COL", () => expect(getTeamFlag(team("Colombia", "COL"))).toBe("🇨🇴"));
  it("retorna bandera por código MEX", () => expect(getTeamFlag(team("México", "MEX"))).toBe("🇲🇽"));
  it("retorna bandera por código ESP", () => expect(getTeamFlag(team("España", "ESP"))).toBe("🇪🇸"));
  it("retorna bandera por código FRA", () => expect(getTeamFlag(team("Francia", "FRA"))).toBe("🇫🇷"));
  it("retorna bandera por código GER", () => expect(getTeamFlag(team("Alemania", "GER"))).toBe("🇩🇪"));
  it("retorna bandera por código ALE", () => expect(getTeamFlag(team("Alemania", "ALE"))).toBe("🇩🇪"));
  it("retorna bandera por código ITA", () => expect(getTeamFlag(team("Italia", "ITA"))).toBe("🇮🇹"));
  it("retorna bandera por código USA", () => expect(getTeamFlag(team("Estados Unidos", "USA"))).toBe("🇺🇸"));
  it("retorna bandera por código CAN", () => expect(getTeamFlag(team("Canadá", "CAN"))).toBe("🇨🇦"));
  it("retorna bandera por código ENG", () => expect(getTeamFlag(team("England", "ENG"))).toBe("🏴"));
  it("retorna bandera por código KOR", () => expect(getTeamFlag(team("Korea", "KOR"))).toBe("🇰🇷"));
  it("retorna bandera por código URU", () => expect(getTeamFlag(team("Uruguay", "URU"))).toBe("🇺🇾"));
  it("el código es case-insensitive", () => expect(getTeamFlag(team("Argentina", "arg"))).toBe("🇦🇷"));
  it("busca por nombre si no hay código", () => expect(getTeamFlag(team("argentina"))).toBe("🇦🇷"));
  it("busca por nombre brasil", () => expect(getTeamFlag(team("brasil"))).toBe("🇧🇷"));
  it("busca por nombre brazil en inglés", () => expect(getTeamFlag(team("brazil"))).toBe("🇧🇷"));
  it("busca por nombre colombia", () => expect(getTeamFlag(team("colombia"))).toBe("🇨🇴"));
  it("busca por nombre españa", () => expect(getTeamFlag(team("españa"))).toBe("🇪🇸"));
  it("busca por nombre spain", () => expect(getTeamFlag(team("spain"))).toBe("🇪🇸"));
  it("busca por nombre france", () => expect(getTeamFlag(team("france"))).toBe("🇫🇷"));
  it("busca por nombre alemania", () => expect(getTeamFlag(team("alemania"))).toBe("🇩🇪"));
  it("busca por nombre germany", () => expect(getTeamFlag(team("germany"))).toBe("🇩🇪"));
  it("busca por nombre italia", () => expect(getTeamFlag(team("italia"))).toBe("🇮🇹"));
  it("busca por nombre italy", () => expect(getTeamFlag(team("italy"))).toBe("🇮🇹"));
  it("busca por nombre estados unidos", () => expect(getTeamFlag(team("estados unidos"))).toBe("🇺🇸"));
  it("busca por nombre canada", () => expect(getTeamFlag(team("canada"))).toBe("🇨🇦"));
  it("busca por nombre canadá con tilde", () => expect(getTeamFlag(team("canadá"))).toBe("🇨🇦"));
  it("busca por nombre england", () => expect(getTeamFlag(team("england"))).toBe("🏴"));
  it("busca por nombre inglaterra", () => expect(getTeamFlag(team("inglaterra"))).toBe("🏴"));
  it("busca por nombre uruguay", () => expect(getTeamFlag(team("uruguay"))).toBe("🇺🇾"));
  it("ignora espacios al buscar por nombre", () => expect(getTeamFlag(team("  argentina  "))).toBe("🇦🇷"));
  it("retorna bandera blanca si no reconoce el equipo", () => expect(getTeamFlag(team("Equipo Desconocido"))).toBe("🏳️"));
  it("prioriza código sobre nombre si ambos existen", () => expect(getTeamFlag(team("argentina", "BRA"))).toBe("🇧🇷"));
  it("usa nombre si el código no está en el registro", () => expect(getTeamFlag(team("argentina", "XYZ"))).toBe("🇦🇷"));
});

describe("formatTeam", () => {
  it("incluye bandera, nombre y código", () => {
    const result = formatTeam(team("Argentina", "ARG"));
    expect(result).toContain("🇦🇷");
    expect(result).toContain("Argentina");
    expect(result).toContain("ARG");
  });
  it("no incluye paréntesis si no hay código", () => {
    expect(formatTeam(team("Argentina"))).not.toContain("(");
  });
  it("formato correcto con código", () => { expect(formatTeam(team("Colombia", "COL"))).toBe("🇨🇴 Colombia (COL)"); });
  it("formato correcto sin código", () => { expect(formatTeam(team("colombia"))).toBe("🇨🇴 colombia"); });
});

describe("getCountryFlag", () => {
  it("retorna bandera para argentina", () => expect(getCountryFlag("argentina")).toBe("🇦🇷"));
  it("retorna bandera para colombia", () => expect(getCountryFlag("colombia")).toBe("🇨🇴"));
  it("retorna bandera para brasil", () => expect(getCountryFlag("brasil")).toBe("🇧🇷"));
  it("retorna bandera para mexico", () => expect(getCountryFlag("mexico")).toBe("🇲🇽"));
  it("retorna bandera para españa", () => expect(getCountryFlag("españa")).toBe("🇪🇸"));
  it("es case-insensitive", () => expect(getCountryFlag("ARGENTINA")).toBe("🇦🇷"));
  it("ignora espacios al inicio y final", () => expect(getCountryFlag("  colombia  ")).toBe("🇨🇴"));
  it("retorna bandera blanca para país desconocido", () => expect(getCountryFlag("Pais Desconocido")).toBe("🏳️"));
});