export interface Cliente {
  instalacao: number;
  nomeCliente: string;
  latitude: number;
  longitude: number;
  // Campos opcionais se o retorno for o DTO de resposta enxuto
  contaContrato?: number;
  numeroSerie?: number;
  numeroPoste?: string;
}
