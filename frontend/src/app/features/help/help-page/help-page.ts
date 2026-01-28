import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-help-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-4xl mx-auto space-y-8">
      <div class="text-center">
        <h1 class="text-3xl font-bold text-geoblue-900 dark:text-geoblue-100">Como usar o GeoRoute</h1>
        <p class="mt-2 text-gray-600 dark:text-gray-400">Guia passo-a-passo para localizar clientes.</p>
      </div>

      <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <!-- Step 1 -->
        <div class="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md border border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-center w-12 h-12 rounded-full bg-geoblue-100 dark:bg-geoblue-900 text-geoblue-600 dark:text-geoblue-300 font-bold text-xl mb-4">1</div>
          <h3 class="text-lg font-semibold mb-2 dark:text-gray-100">Escolha o Tipo de Busca</h3>
          <p class="text-gray-600 dark:text-gray-400 text-sm">
            Selecione uma das opções disponíveis: Instalação, Conta Contrato, Número de Série ou Número do Poste.
          </p>
        </div>

        <!-- Step 2 -->
        <div class="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md border border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-center w-12 h-12 rounded-full bg-geoblue-100 dark:bg-geoblue-900 text-geoblue-600 dark:text-geoblue-300 font-bold text-xl mb-4">2</div>
          <h3 class="text-lg font-semibold mb-2 dark:text-gray-100">Digite o Código</h3>
          <p class="text-gray-600 dark:text-gray-400 text-sm">
            Insira o número correspondente à opção escolhida no campo de texto.
          </p>
        </div>

        <!-- Step 3 -->
        <div class="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md border border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-center w-12 h-12 rounded-full bg-geoblue-100 dark:bg-geoblue-900 text-geoblue-600 dark:text-geoblue-300 font-bold text-xl mb-4">3</div>
          <h3 class="text-lg font-semibold mb-2 dark:text-gray-100">Buscar e Visualizar</h3>
          <p class="text-gray-600 dark:text-gray-400 text-sm">
            Clique em "Buscar". Os dados do cliente aparecerão abaixo. Clique em "Ver no Mapa" para abrir a localização.
          </p>
        </div>
      </div>

      <!-- FAQ / Tips -->
      <div class="bg-blue-50 dark:bg-gray-800 p-6 rounded-lg border border-blue-100 dark:border-gray-700 mt-8">
        <h3 class="text-xl font-bold text-geoblue-900 dark:text-geoblue-300 mb-4">Dica Rápida</h3>
        <p class="text-gray-700 dark:text-gray-300">
          Se você quiser ir direto para o mapa sem conferir os dados do cliente, marque a caixa de seleção 
          <strong>"Abrir mapa diretamente"</strong> antes de clicar em buscar.
        </p>
      </div>
    </div>
  `
})
export class HelpPageComponent {}