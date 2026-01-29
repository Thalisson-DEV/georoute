import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cliente } from '../interfaces/cliente.interface';
import { ClienteRequest } from '../interfaces/cliente-request.interface';
import { PaginatedClientesResponse } from '../interfaces/paginated-clientes-response.interface';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/clientes`;

  constructor() { }

  // Cadastra um novo cliente
  createClient(client: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, client);
  }

  // Busca por número de instalação
  searchByInstalacao(id: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/instalacao/${id}`);
  }

  // Busca por conta contrato
  searchByContaContrato(id: string): Observable<PaginatedClientesResponse> {
    return this.http.get<PaginatedClientesResponse>(`${this.apiUrl}/conta-contrato/${id}`);
  }

  // Busca por número de série
  searchByNumeroSerie(id: string): Observable<PaginatedClientesResponse> {
    return this.http.get<PaginatedClientesResponse>(`${this.apiUrl}/numero-serie/${id}`);
  }

  // Busca por número do poste
  searchByNumeroPoste(id: string): Observable<PaginatedClientesResponse> {
    return this.http.get<PaginatedClientesResponse>(`${this.apiUrl}/numero-poste/${id}`);
  }

  // Importar clientes via CSV
  uploadClients(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/import`, formData, { responseType: 'text' });
  }

  // Abre o redirecionador do backend que encaminha para o Google Maps
  openInMaps(latitude: number, longitude: number): void {
    const mapsUrl = `${environment.apiUrl}/maps/redirect`;
    const url = `${mapsUrl}?latitude=${latitude}&longitude=${longitude}`;
    window.open(url, '_blank');
  }
}
