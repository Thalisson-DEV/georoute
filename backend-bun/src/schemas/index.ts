import { z } from "zod";

// Enums equivalentes a SetorEnum / MunicipioEnum.
export const setorEnum = z.enum(["COMERCIAL", "LEITURA"]);
export const municipioEnum = z.enum(["JUAZEIRO", "REMANSO", "BONFIM", "JACOBINA"]);

export type Setor = z.infer<typeof setorEnum>;
export type Municipio = z.infer<typeof municipioEnum>;

// ClientesRequestDTO — apenas `instalacao` é obrigatório.
export const clientesRequestSchema = z.object({
  instalacao: z.number({ required_error: "O numero da instalação não pode estar vazio." }),
  contaContrato: z.number().nullish(),
  numeroSerie: z.number().nullish(),
  numeroPoste: z.string().nullish(),
  nomeCliente: z.string().nullish(),
  latitude: z.number().nullish(),
  longitude: z.number().nullish(),
});
export type ClientesRequest = z.infer<typeof clientesRequestSchema>;

// EquipesRequestDTO.
export const equipesRequestSchema = z.object({
  nome: z.string({ required_error: "O nome da equipe é obrigatório" }).trim().min(1, "O nome da equipe é obrigatório"),
  latitudeBase: z.number({ required_error: "A latitude base é obrigatória" }),
  longitudeBase: z.number({ required_error: "A longitude base é obrigatória" }),
  setor: setorEnum.refine((v) => v !== undefined, { message: "O setor é obrigatório" }),
  municipio: municipioEnum.refine((v) => v !== undefined, { message: "O município é obrigatório" }),
});
export type EquipesRequest = z.infer<typeof equipesRequestSchema>;

// ClientDTO usado na otimização de rota (campos extras do front-end são ignorados).
export const clientDtoSchema = z.object({
  instalacao: z.number({ required_error: "O número da instalação é obrigatório" }),
  latitude: z.number({ required_error: "A latitude do cliente é obrigatória" }),
  longitude: z.number({ required_error: "A longitude do cliente é obrigatória" }),
});

// RouteRequestDTO.
export const routeRequestSchema = z.object({
  teamId: z.number({ required_error: "O ID da equipe é obrigatório" }),
  clients: z.array(clientDtoSchema).min(1, "A lista de clientes não pode estar vazia"),
  currentLat: z.number().nullish(),
  currentLon: z.number().nullish(),
});
export type RouteRequest = z.infer<typeof routeRequestSchema>;

/** Valida com zod e lança ZodError (capturado pelo error-handler como 400). */
export function validate<T>(schema: z.ZodType<T>, data: unknown): T {
  return schema.parse(data);
}
