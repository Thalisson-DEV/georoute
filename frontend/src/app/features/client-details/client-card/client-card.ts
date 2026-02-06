import { Component, Input, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Cliente } from '../../../core/interfaces/cliente.interface';
import { ClientService } from '../../../core/services/client.service';
import { RouteService } from '../../../core/services/route.service';

@Component({
  selector: 'app-client-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden border border-gray-200 dark:border-gray-700 transition-colors duration-300"
         [class.ring-2]="isSelected()"
         [class.ring-geoblue-500]="isSelected()">
      <div class="p-6">
        <div class="flex justify-between items-start">
            <h3 class="text-xl font-bold text-geoblue-900 dark:text-geoblue-300 mb-4">{{ cliente.nomeCliente }}</h3>
            <span *ngIf="isSelected()" class="bg-geoblue-100 text-geoblue-800 text-xs font-medium px-2.5 py-0.5 rounded dark:bg-geoblue-900 dark:text-geoblue-300">
              No Roteiro
            </span>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-700 dark:text-gray-300">
          <div>
            <p class="font-semibold text-gray-500 dark:text-gray-400">Instalação</p>
            <p>{{ cliente.instalacao }}</p>
          </div>
          <div *ngIf="cliente.contaContrato">
            <p class="font-semibold text-gray-500 dark:text-gray-400">Conta Contrato</p>
            <p>{{ cliente.contaContrato }}</p>
          </div>
          <div *ngIf="cliente.numeroSerie">
            <p class="font-semibold text-gray-500 dark:text-gray-400">Número de Série</p>
            <p>{{ cliente.numeroSerie }}</p>
          </div>
          <div *ngIf="cliente.numeroPoste">
            <p class="font-semibold text-gray-500 dark:text-gray-400">Poste</p>
            <p>{{ cliente.numeroPoste }}</p>
          </div>
          <div class="md:col-span-2">
            <p class="font-semibold text-gray-500 dark:text-gray-400">Localização</p>
            <p>{{ cliente.latitude }}, {{ cliente.longitude }}</p>
          </div>
        </div>

        <div class="mt-6 flex justify-end gap-3">
          <button (click)="toggleRoute()" 
                  [class.bg-green-600]="!isSelected()"
                  [class.hover:bg-green-700]="!isSelected()"
                  [class.bg-red-600]="isSelected()"
                  [class.hover:bg-red-700]="isSelected()"
                  class="text-white font-semibold py-2 px-4 rounded-md shadow flex items-center gap-2 transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 dark:focus:ring-offset-gray-800">
            <svg *ngIf="!isSelected()" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            <svg *ngIf="isSelected()" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
            </svg>
            {{ isSelected() ? 'Remover' : 'Roteiro' }}
          </button>

          <button (click)="openMap()" class="bg-geoblue-600 hover:bg-geoblue-700 text-white font-semibold py-2 px-4 rounded-md shadow flex items-center gap-2 transition-colors focus:outline-none focus:ring-2 focus:ring-geoblue-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z" />
            </svg>
            Ver no Mapa
          </button>
        </div>
      </div>
    </div>
  `
})
export class ClientCardComponent {
  @Input({ required: true }) cliente!: Cliente;
  private clientService = inject(ClientService);
  private routeService = inject(RouteService);

  isSelected = computed(() => this.routeService.isClientSelected(this.cliente));

  openMap() {
    this.clientService.openInMaps(this.cliente.latitude, this.cliente.longitude);
  }

  toggleRoute() {
    this.routeService.toggleClient(this.cliente);
  }
}