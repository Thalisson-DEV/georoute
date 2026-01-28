import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cliente } from '../interfaces/cliente.interface';
import { ClienteRequest } from '../interfaces/cliente-request.interface';

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
  searchByContaContrato(id: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/conta-contrato/${id}`);
  }

  // Busca por número de série
  searchByNumeroSerie(id: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/numero-serie/${id}`);
  }

  // Busca por número do poste
  searchByNumeroPoste(id: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/numero-poste/${id}`);
  }

  // Importar clientes via CSV
  uploadClients(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/import`, formData, { responseType: 'text' });
  }

  /**
   * Abre o redirecionador do backend que encaminha para o Google Maps.
   */
  openInMaps(latitude: number, longitude: number): void {
    const url = `${this.apiUrl}/maps/redirect?latitude=${latitude}&longitude=${longitude}`;
    window.open(url, '_blank');
  }
}
