import type { Context } from "hono";
import { ZodError } from "zod";
import { AppError } from "../errors.ts";

interface RestExceptionResponse {
  timestamp: string;
  message: string;
  status: number;
}

function body(message: string, status: number): RestExceptionResponse {
  return { timestamp: new Date().toISOString(), message, status };
}

// Handler global de erros. Mapeia para o shape RestExceptionResponse do back-end Java.
export function errorHandler(err: Error, c: Context): Response {
  // Erros de domínio com status próprio.
  if (err instanceof AppError) {
    return c.json(body(err.message, err.status), err.status as 400);
  }

  // Erros de validação (zod) -> 400, agregando as mensagens (equivalente ao Jakarta Validation).
  if (err instanceof ZodError) {
    const message = err.issues.map((i) => i.message).join("; ");
    return c.json(body(message, 400), 400);
  }

  // Qualquer outro erro -> 500.
  console.error("[error] erro não tratado:", err);
  return c.json(body(err.message || "Erro interno do servidor", 500), 500);
}
