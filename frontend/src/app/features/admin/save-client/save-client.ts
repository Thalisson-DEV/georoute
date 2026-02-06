import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientService } from '../../../core/services/client.service';
import { ClienteRequest } from '../../../core/interfaces/cliente-request.interface';
import { ManageTeamsComponent } from '../manage-teams/manage-teams';

type TabType = 'clientes' | 'equipes';

@Component({
  selector: 'app-save-client',
  standalone: true,
  imports: [CommonModule, FormsModule, ManageTeamsComponent],
  templateUrl: './save-client.html',
  styleUrl: './save-client.css',
})
export class SaveClientComponent {
  private clientService = inject(ClientService);
  private router = inject(Router);

  activeTab = signal<TabType>('clientes');

  // Client Form State
  client: ClienteRequest = {
    instalacao: 0,
    contaContrato: 0,
    numeroSerie: 0,
    numeroPoste: '',
    nomeCliente: '',
    latitude: 0,
    longitude: 0
  };

  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  setActiveTab(tab: TabType) {
    this.activeTab.set(tab);
    // Clear messages when switching
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  onSubmitClient() {
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.clientService.createClient(this.client).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMessage.set('Cliente cadastrado com sucesso!');
        // Reset form
        this.client = {
          instalacao: 0,
          contaContrato: 0,
          numeroSerie: 0,
          numeroPoste: '',
          nomeCliente: '',
          latitude: 0,
          longitude: 0
        };
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set('Erro ao cadastrar cliente. Verifique os dados e tente novamente.');
        console.error('Save error', err);
      }
    });
  }
}