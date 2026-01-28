import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientService } from '../../../core/services/client.service';

@Component({
  selector: 'app-import-clients',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-clients.html',
  styleUrl: './import-clients.css',
})
export class ImportClientsComponent {
  private clientService = inject(ClientService);

  isLoading = signal(false);
  message = signal('');
  isError = signal(false);

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.uploadFile(file);
    }
  }

  uploadFile(file: File) {
    this.isLoading.set(true);
    this.message.set('Enviando arquivo...');
    this.isError.set(false);

    this.clientService.uploadClients(file).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.message.set(response || 'Importação iniciada com sucesso! O processamento ocorrerá em segundo plano.');
        this.isError.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.message.set('Erro ao enviar arquivo. Verifique se o formato está correto e tente novamente.');
        this.isError.set(true);
        console.error('Upload error', err);
      }
    });
  }
}