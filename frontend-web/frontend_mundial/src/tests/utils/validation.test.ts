import { describe, it, expect } from "vitest";
import {
  validatePersonName, validateEmail, getPasswordRules, validatePassword,
  validateAvatar, splitCommaValues, validateRequired, validateTextLength,
  validateCode, validatePositiveNumber, validateIntegerRange, validatePaymentReference,
} from "../../utils/validation";

describe("validatePersonName", () => {
  it("retorna error si está vacío", () => expect(validatePersonName("", "Nombre")).toBe("Nombre es obligatorio."));
  it("retorna error si es solo espacios", () => expect(validatePersonName("   ", "Nombre")).toBe("Nombre es obligatorio."));
  it("retorna error si tiene menos de 2 caracteres", () => expect(validatePersonName("A", "Nombre")).toBe("Nombre debe tener al menos 2 caracteres."));
  it("retorna error si tiene números", () => expect(validatePersonName("Ana123", "Nombre")).toBe("Nombre solo puede tener letras y espacios."));
  it("retorna error si tiene caracteres especiales", () => expect(validatePersonName("Ana@", "Nombre")).toBe("Nombre solo puede tener letras y espacios."));
  it("retorna vacío para nombre válido", () => expect(validatePersonName("Ana", "Nombre")).toBe(""));
  it("acepta nombres con tildes", () => expect(validatePersonName("Sofía", "Nombre")).toBe(""));
  it("acepta nombres con espacios", () => expect(validatePersonName("Ana María", "Nombre")).toBe(""));
  it("acepta nombres con apóstrofo", () => expect(validatePersonName("O'Brien", "Nombre")).toBe(""));
  it("acepta nombres con guión", () => expect(validatePersonName("Jean-Pierre", "Nombre")).toBe(""));
  it("acepta ñ", () => expect(validatePersonName("Muñoz", "Nombre")).toBe(""));
});

describe("validateEmail", () => {
  it("retorna error si está vacío", () => expect(validateEmail("")).toBe("El correo es obligatorio."));
  it("retorna error si es solo espacios", () => expect(validateEmail("   ")).toBe("El correo es obligatorio."));
  it("retorna error si no tiene @", () => expect(validateEmail("emailsinpunto")).toBe("Escribe un correo válido."));
  it("retorna error si no tiene dominio", () => expect(validateEmail("email@")).toBe("Escribe un correo válido."));
  it("retorna error si la extensión es menor a 2 chars", () => expect(validateEmail("email@dom.c")).toBe("Escribe un correo válido."));
  it("retorna vacío para email válido", () => expect(validateEmail("user@example.com")).toBe(""));
  it("acepta email con subdominio", () => expect(validateEmail("user@mail.example.com")).toBe(""));
  it("ignora espacios al inicio y final", () => expect(validateEmail("  user@test.com  ")).toBe(""));
});

describe("getPasswordRules", () => {
  it("retorna 5 reglas", () => expect(getPasswordRules("Test1!")).toHaveLength(5));
  it("marca longitud mínima como válida si >= 8", () => expect(getPasswordRules("Abcdef1!")[0].valid).toBe(true));
  it("marca longitud mínima como inválida si < 8", () => expect(getPasswordRules("Ab1!")[0].valid).toBe(false));
  it("marca mayúscula como válida si la tiene", () => expect(getPasswordRules("Abcdef1!")[1].valid).toBe(true));
  it("marca mayúscula como inválida si no la tiene", () => expect(getPasswordRules("abcdef1!")[1].valid).toBe(false));
  it("marca minúscula como válida si la tiene", () => expect(getPasswordRules("Abcdef1!")[2].valid).toBe(true));
  it("marca número como válido si lo tiene", () => expect(getPasswordRules("Abcdef1!")[3].valid).toBe(true));
  it("marca número como inválido si no lo tiene", () => expect(getPasswordRules("Abcdefgh!")[3].valid).toBe(false));
  it("marca símbolo como válido si lo tiene", () => expect(getPasswordRules("Abcdef1!")[4].valid).toBe(true));
  it("marca símbolo como inválido si no lo tiene", () => expect(getPasswordRules("Abcdef12")[4].valid).toBe(false));
});

describe("validatePassword", () => {
  it("retorna error si está vacía", () => expect(validatePassword("")).toBe("La contraseña es obligatoria."));
  it("retorna error si no cumple reglas", () => expect(validatePassword("abc")).toBe("La contraseña debe ser más segura."));
  it("retorna vacío si cumple todas las reglas", () => expect(validatePassword("Abcdef1!")).toBe(""));
  it("retorna error si falta mayúscula", () => expect(validatePassword("abcdef1!")).toBe("La contraseña debe ser más segura."));
  it("retorna error si falta número", () => expect(validatePassword("Abcdefgh!")).toBe("La contraseña debe ser más segura."));
  it("retorna error si falta símbolo", () => expect(validatePassword("Abcdefg1")).toBe("La contraseña debe ser más segura."));
  it("retorna error si es muy corta", () => expect(validatePassword("Ab1!")).toBe("La contraseña debe ser más segura."));
});

describe("validateAvatar", () => {
  const makeFile = (type: string, size: number) => new File(["x".repeat(size)], "avatar.jpg", { type });
  it("retorna error si el tipo no está permitido", () => expect(validateAvatar(makeFile("image/gif", 100))).toBe("El avatar debe ser una imagen JPG, PNG o WEBP."));
  it("retorna vacío para JPG", () => expect(validateAvatar(makeFile("image/jpeg", 100))).toBe(""));
  it("retorna vacío para PNG", () => expect(validateAvatar(makeFile("image/png", 100))).toBe(""));
  it("retorna vacío para WEBP", () => expect(validateAvatar(makeFile("image/webp", 100))).toBe(""));
  it("retorna error si supera 1MB", () => expect(validateAvatar(makeFile("image/jpeg", 1024 * 1024 + 1))).toBe("El avatar no puede pesar más de 1 MB."));
  it("retorna vacío si es exactamente 1MB", () => expect(validateAvatar(makeFile("image/jpeg", 1024 * 1024))).toBe(""));
});

describe("splitCommaValues", () => {
  it("separa valores por coma", () => expect(splitCommaValues("a, b, c")).toEqual(["a", "b", "c"]));
  it("elimina espacios extra", () => expect(splitCommaValues("  a  ,  b  ")).toEqual(["a", "b"]));
  it("filtra valores vacíos", () => expect(splitCommaValues("a,,b")).toEqual(["a", "b"]));
  it("retorna array vacío para string vacío", () => expect(splitCommaValues("")).toEqual([]));
  it("retorna un elemento si no hay comas", () => expect(splitCommaValues("solo")).toEqual(["solo"]));
});

describe("validateRequired", () => {
  it("retorna error si está vacío", () => expect(validateRequired("", "Campo")).toBe("Campo es obligatorio."));
  it("retorna error si es solo espacios", () => expect(validateRequired("   ", "Campo")).toBe("Campo es obligatorio."));
  it("retorna vacío si tiene contenido", () => expect(validateRequired("valor", "Campo")).toBe(""));
  it("retorna error si no alcanza minLength", () => expect(validateRequired("ab", "Campo", 5)).toBe("Campo debe tener al menos 5 caracteres."));
  it("retorna vacío si alcanza exactamente minLength", () => expect(validateRequired("abcde", "Campo", 5)).toBe(""));
});

describe("validateTextLength", () => {
  it("retorna error si está vacío", () => expect(validateTextLength("", "Campo", 2, 10)).toBe("Campo es obligatorio."));
  it("retorna error si es demasiado corto", () => expect(validateTextLength("a", "Campo", 2, 10)).toBe("Campo debe tener al menos 2 caracteres."));
  it("retorna error si es demasiado largo", () => expect(validateTextLength("abcdefghijk", "Campo", 2, 10)).toBe("Campo debe tener máximo 10 caracteres."));
  it("retorna vacío si está dentro del rango", () => expect(validateTextLength("hola", "Campo", 2, 10)).toBe(""));
  it("retorna vacío si tiene exactamente maxLength", () => expect(validateTextLength("abcdefghij", "Campo", 2, 10)).toBe(""));
});

describe("validateCode", () => {
  it("retorna error si está vacío", () => expect(validateCode("", "Código")).toBe("Código es obligatorio."));
  it("retorna error si es solo espacios", () => expect(validateCode("   ", "Código")).toBe("Código es obligatorio."));
  it("acepta UUID válido", () => expect(validateCode("550e8400-e29b-41d4-a716-446655440000", "Código")).toBe(""));
  it("acepta shortcode alfanumérico de 4 chars", () => expect(validateCode("ABCD", "Código")).toBe(""));
  it("acepta shortcode alfanumérico de 40 chars", () => expect(validateCode("A".repeat(40), "Código")).toBe(""));
  it("retorna error si shortcode tiene menos de 4 chars", () => expect(validateCode("ABC", "Código")).toBe("Código no tiene un formato válido."));
  it("retorna error si shortcode tiene más de 40 chars", () => expect(validateCode("A".repeat(41), "Código")).toBe("Código no tiene un formato válido."));
  it("retorna error si tiene caracteres especiales no UUID", () => expect(validateCode("ABC@123", "Código")).toBe("Código no tiene un formato válido."));
  it("es case-insensitive para UUID", () => expect(validateCode("550E8400-E29B-41D4-A716-446655440000", "Código")).toBe(""));
});

describe("validatePositiveNumber", () => {
  it("retorna error si no es finito", () => expect(validatePositiveNumber(NaN, "Cantidad")).toBe("Cantidad debe ser un número válido."));
  it("retorna error si es Infinity", () => expect(validatePositiveNumber(Infinity, "Cantidad")).toBe("Cantidad debe ser un número válido."));
  it("retorna error si es menor al mínimo", () => expect(validatePositiveNumber(0, "Cantidad", 1)).toBe("Cantidad debe ser mínimo 1."));
  it("retorna error si es mayor al máximo", () => expect(validatePositiveNumber(101, "Cantidad", 1, 100)).toBe("Cantidad debe ser máximo 100."));
  it("retorna vacío si está en el rango", () => expect(validatePositiveNumber(50, "Cantidad", 1, 100)).toBe(""));
  it("retorna vacío si es exactamente el mínimo", () => expect(validatePositiveNumber(1, "Cantidad", 1)).toBe(""));
  it("retorna vacío si es exactamente el máximo", () => expect(validatePositiveNumber(100, "Cantidad", 1, 100)).toBe(""));
  it("acepta decimales válidos", () => expect(validatePositiveNumber(1.5, "Cantidad", 1, 10)).toBe(""));
});

describe("validateIntegerRange", () => {
  it("retorna error si no es finito", () => expect(validateIntegerRange(NaN, "Número", 1, 10)).toBe("Número debe ser un número válido."));
  it("retorna error si no es entero", () => expect(validateIntegerRange(1.5, "Número", 1, 10)).toBe("Número debe ser un número entero."));
  it("retorna error si es menor al mínimo", () => expect(validateIntegerRange(0, "Número", 1, 10)).toBe("Número debe ser mínimo 1."));
  it("retorna error si es mayor al máximo", () => expect(validateIntegerRange(11, "Número", 1, 10)).toBe("Número debe ser máximo 10."));
  it("retorna vacío si está en el rango", () => expect(validateIntegerRange(5, "Número", 1, 10)).toBe(""));
  it("retorna vacío en el límite inferior", () => expect(validateIntegerRange(1, "Número", 1, 10)).toBe(""));
  it("retorna vacío en el límite superior", () => expect(validateIntegerRange(10, "Número", 1, 10)).toBe(""));
});

describe("validatePaymentReference", () => {
  it("retorna error si está vacío", () => expect(validatePaymentReference("", "Ref")).toBe("Ref es obligatorio."));
  it("retorna error si tiene menos de 4 chars", () => expect(validatePaymentReference("AB", "Ref")).toBe("Ref debe tener al menos 4 caracteres."));
  it("retorna error si tiene más de 40 chars", () => expect(validatePaymentReference("A".repeat(41), "Ref")).toBe("Ref debe tener máximo 40 caracteres."));
  it("retorna error si tiene caracteres inválidos", () => expect(validatePaymentReference("REF@#$", "Ref")).toBe("Ref solo puede tener letras, números, espacios, puntos, guiones o asteriscos."));
  it("retorna vacío para referencia válida", () => expect(validatePaymentReference("REF-2026", "Ref")).toBe(""));
  it("acepta asteriscos", () => expect(validatePaymentReference("4111****1234", "Ref")).toBe(""));
  it("acepta puntos", () => expect(validatePaymentReference("ref.pago.01", "Ref")).toBe(""));
  it("acepta espacios", () => expect(validatePaymentReference("REF 2026", "Ref")).toBe(""));
  it("acepta exactamente 4 chars", () => expect(validatePaymentReference("ABCD", "Ref")).toBe(""));
  it("acepta exactamente 40 chars", () => expect(validatePaymentReference("A".repeat(40), "Ref")).toBe(""));
});