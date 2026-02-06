import { Injectable, computed, inject, signal, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cliente } from '../interfaces/cliente.interface';
import { environment } from '../../../environments/environment';
import { RouteRequest } from '../interfaces/route-request.interface';
import { OrsOptimizationResponse } from '../interfaces/ors-optimization-response.interface';
import { Observable } from 'rxjs';

import { RouteHistory } from '../interfaces/route-history.interface';

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/routes`;
  private storageKey = 'georoute_selected_clients';

  // State
  private selectedClientsSignal = signal<Cliente[]>([]);

  // Computed
  selectedClients = this.selectedClientsSignal.asReadonly();
  clientCount = computed(() => this.selectedClientsSignal().length);
  hasClients = computed(() => this.selectedClientsSignal().length > 0);

  constructor() {
    this.loadFromStorage();

    // Auto-save whenever the list changes
    effect(() => {
      const clients = this.selectedClientsSignal();
      this.saveToStorage(clients);
    });
  }

  private loadFromStorage() {
    try {
      const stored = localStorage.getItem(this.storageKey);
      if (stored) {
        const parsed = JSON.parse(stored);
        if (Array.isArray(parsed)) {
          this.selectedClientsSignal.set(parsed);
        }
      }
    } catch (e) {
      console.warn('Failed to load clients from local storage', e);
    }
  }

  private saveToStorage(clients: Cliente[]) {
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(clients));
    } catch (e) {
      console.warn('Failed to save clients to local storage', e);
    }
  }

  addClient(client: Cliente): boolean {
    const currentList = this.selectedClientsSignal();
    
    // Validation: Max 50
    if (currentList.length >= 50) {
      return false;
    }

    // Validation: Already exists? (Check by instalacao or id if available, assuming instalacao is unique enough for this list)
    if (this.isClientSelected(client)) {
      return false; 
    }

    this.selectedClientsSignal.update(list => [...list, client]);
    return true;
  }

  removeClient(client: Cliente) {
    this.selectedClientsSignal.update(list => 
      list.filter(c => c.instalacao !== client.instalacao)
    );
  }

  toggleClient(client: Cliente) {
    if (this.isClientSelected(client)) {
      this.removeClient(client);
    } else {
      this.addClient(client);
    }
  }

  isClientSelected(client: Cliente): boolean {
    return this.selectedClientsSignal().some(c => c.instalacao === client.instalacao);
  }

  clearRoute() {
    this.selectedClientsSignal.set([]);
  }

  // API Calls
  optimizeRoute(request: RouteRequest): Observable<OrsOptimizationResponse> {
    return this.http.post<OrsOptimizationResponse>(`${this.apiUrl}/optimize`, request);
  }

  getRouteHistory(teamId: number): Observable<RouteHistory[]> {
    return this.http.get<RouteHistory[]>(`${this.apiUrl}/history/${teamId}`);
  }

  getRouteDetails(routeId: string): Observable<OrsOptimizationResponse> {
    return this.http.get<OrsOptimizationResponse>(`${this.apiUrl}/${routeId}`);
  }
}
