import { Cliente } from './cliente.interface';

export interface RouteRequest {
  teamId: number;
  clients: Cliente[];
  currentLat?: number;
  currentLon?: number;
}
