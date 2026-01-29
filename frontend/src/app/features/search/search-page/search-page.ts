import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClientService } from '../../../core/services/client.service';
import { Cliente } from '../../../core/interfaces/cliente.interface';
import { ClientCardComponent } from '../../../features/client-details/client-card/client-card';
import { HttpErrorResponse } from '@angular/common/http';
import { PaginatedClientesResponse } from '../../../core/interfaces/paginated-clientes-response.interface';
import { Observable } from 'rxjs';

type SearchType = 'instalacao' | 'contaContrato' | 'numeroSerie' | 'numeroPoste';

@Component({
  selector: 'app-search-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ClientCardComponent],
  template: `
    <div class="max-w-3xl mx-auto space-y-8">
      
      <!-- Header Section -->
      <div class="text-center space-y-2">
        <h1 class="text-3xl font-bold text-geoblue-900 dark:text-geoblue-100">Localizar Cliente</h1>
        <p class="text-gray-600 dark:text-gray-400">Selecione o tipo de busca e insira o código para encontrar a localização.</p>
      </div>

      <!-- Search Form -->
      <div class="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700">
        <form (ngSubmit)="onSearch()" class="space-y-6">
          
          <!-- Search Type Selection -->
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <label class="cursor-pointer">
              <input type="radio" name="searchType" [(ngModel)]="searchType" value="instalacao" class="peer sr-only">
              <div class="p-3 text-center rounded-md border border-gray-300 dark:border-gray-600 peer-checked:bg-geoblue-100 peer-checked:border-geoblue-500 peer-checked:text-geoblue-900 dark:peer-checked:bg-geoblue-900 dark:peer-checked:text-geoblue-100 hover:bg-gray-50 dark:hover:bg-gray-700 transition-all">
                <span class="text-sm font-medium">Instalação</span>
              </div>
            </label>
            <label class="cursor-pointer">
              <input type="radio" name="searchType" [(ngModel)]="searchType" value="contaContrato" class="peer sr-only">
              <div class="p-3 text-center rounded-md border border-gray-300 dark:border-gray-600 peer-checked:bg-geoblue-100 peer-checked:border-geoblue-500 peer-checked:text-geoblue-900 dark:peer-checked:bg-geoblue-900 dark:peer-checked:text-geoblue-100 hover:bg-gray-50 dark:hover:bg-gray-700 transition-all">
                <span class="text-sm font-medium">Conta Contrato</span>
              </div>
            </label>
            <label class="cursor-pointer">
              <input type="radio" name="searchType" [(ngModel)]="searchType" value="numeroSerie" class="peer sr-only">
              <div class="p-3 text-center rounded-md border border-gray-300 dark:border-gray-600 peer-checked:bg-geoblue-100 peer-checked:border-geoblue-500 peer-checked:text-geoblue-900 dark:peer-checked:bg-geoblue-900 dark:peer-checked:text-geoblue-100 hover:bg-gray-50 dark:hover:bg-gray-700 transition-all">
                <span class="text-sm font-medium">Nº Série</span>
              </div>
            </label>
            <label class="cursor-pointer">
              <input type="radio" name="searchType" [(ngModel)]="searchType" value="numeroPoste" class="peer sr-only">
              <div class="p-3 text-center rounded-md border border-gray-300 dark:border-gray-600 peer-checked:bg-geoblue-100 peer-checked:border-geoblue-500 peer-checked:text-geoblue-900 dark:peer-checked:bg-geoblue-900 dark:peer-checked:text-geoblue-100 hover:bg-gray-50 dark:hover:bg-gray-700 transition-all">
                <span class="text-sm font-medium">Nº Poste</span>
              </div>
            </label>
          </div>

          <!-- Input Field -->
          <div class="relative">
            <input type="text" 
                   [(ngModel)]="searchQuery" 
                   name="query" 
                   placeholder="Digite o número..." 
                   class="w-full px-4 py-3 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-geoblue-500 focus:border-transparent outline-none transition-shadow"
                   [disabled]="isLoading()">
          </div>

          <!-- Direct to Map Toggle -->
          <div class="flex items-center space-x-2">
             <input type="checkbox" id="directMap" [(ngModel)]="directToMap" name="directMap" class="w-4 h-4 text-geoblue-600 border-gray-300 rounded focus:ring-geoblue-500 dark:bg-gray-700 dark:border-gray-600">
             <label for="directMap" class="text-sm text-gray-700 dark:text-gray-300 select-none">
               Abrir mapa diretamente (não mostrar detalhes)
             </label>
          </div>

          <!-- Submit Button -->
          <button type="submit" 
                  [disabled]="isLoading() || !searchQuery.trim()"
                  class="w-full bg-geoblue-600 hover:bg-geoblue-700 disabled:bg-geoblue-400 text-white font-bold py-3 px-4 rounded-md shadow transition-colors flex justify-center items-center">
            <span *ngIf="isLoading()" class="mr-2">
              <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </span>
            {{ isLoading() ? 'Buscando...' : 'Buscar' }}
          </button>
        </form>
      </div>

      <!-- Messages -->
      <div *ngIf="errorMessage()" class="p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
        {{ errorMessage() }}
      </div>

      <!-- Result Cards -->
      <div *ngIf="clienteList().length > 0 && !directToMap" class="space-y-4">
        <p class="text-sm text-gray-500 dark:text-gray-400 text-right">
          Encontrado(s): {{ clienteList().length }} cliente(s)
        </p>
        <div *ngFor="let cliente of clienteList()" class="animate-fade-in-up">
          <app-client-card [cliente]="cliente"></app-client-card>
        </div>
      </div>
    </div>
  `
})
export class SearchPageComponent {
  private clientService = inject(ClientService);

  searchType: SearchType = 'instalacao';
  searchQuery = '';
  directToMap = false;

  isLoading = signal(false);
  errorMessage = signal('');
  clienteList = signal<Cliente[]>([]);

  onSearch() {
    if (!this.searchQuery.trim()) return;

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.clienteList.set([]);

    let searchObs: Observable<Cliente | PaginatedClientesResponse>;

    switch (this.searchType) {
      case 'instalacao':
        searchObs = this.clientService.searchByInstalacao(this.searchQuery);
        break;
      case 'contaContrato':
        searchObs = this.clientService.searchByContaContrato(this.searchQuery);
        break;
      case 'numeroSerie':
        searchObs = this.clientService.searchByNumeroSerie(this.searchQuery);
        break;
      case 'numeroPoste':
        searchObs = this.clientService.searchByNumeroPoste(this.searchQuery);
        break;
    }

    searchObs.subscribe({
      next: (response) => {
        this.isLoading.set(false);
        
        let clientes: Cliente[] = [];

        if (this.searchType === 'instalacao') {
           // Single object
           clientes = [response as Cliente];
        } else {
           // Paginated object
           const paginated = response as PaginatedClientesResponse;
           clientes = paginated.data || [];
        }

        if (this.directToMap && clientes.length > 0) {
          // If direct to map is checked, we take the first one (or maybe we should warn if multiple?)
          // For now, taking the first one seems logical as a fallback.
          this.clientService.openInMaps(clientes[0].latitude, clientes[0].longitude);
        } else {
          this.clienteList.set(clientes);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        if (err.status === 404) {
          this.errorMessage.set('Cliente não encontrado.');
        } else {
          this.errorMessage.set('Ocorreu um erro na busca. Tente novamente.');
        }
        console.error('Search error', err);
      }
    });
  }
}