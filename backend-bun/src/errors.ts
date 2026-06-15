// Classes de erro de domínio. Mapeadas para HTTP no error-handler,
// preservando o shape RestExceptionResponse { timestamp, message, status }.

export class AppError extends Error {
  readonly status: number;
  constructor(message: string, status: number) {
    super(message);
    this.name = new.target.name;
    this.status = status;
  }
}

/** 404 — equivalente a jakarta EntityNotFoundException. */
export class EntityNotFoundError extends AppError {
  constructor(message: string) {
    super(message, 404);
  }
}

/** 400 — equivalente a EntityAlreadyExistsException / UserAlreadyExistsException. */
export class EntityAlreadyExistsError extends AppError {
  constructor(message: string) {
    super(message, 400);
  }
}

/** 400 — equivalente a CsvImportException. */
export class CsvImportError extends AppError {
  constructor(message: string) {
    super(message, 400);
  }
}

/** 500 — equivalente a RouteProcessingException / erros de JSON. */
export class RouteProcessingError extends AppError {
  constructor(message: string) {
    super(message, 500);
  }
}

/** Erro repassando o status retornado pelo OpenRouteService (WebClientResponseException). */
export class UpstreamError extends AppError {
  constructor(message: string, status: number) {
    super(message, status);
  }
}
