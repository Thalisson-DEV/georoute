import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer class="bg-gray-100 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800 mt-auto">
      <div class="container mx-auto px-4 py-6 text-center text-sm text-gray-600 dark:text-gray-400">
        <p>&copy; 2026 GeoRoute. Todos os direitos reservados.</p>
        <p class="mt-1">Sipel Construções LTDA</p>
      </div>
    </footer>
  `
})
export class FooterComponent {}
