import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ThemeService } from '../../../core/services/theme.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <header class="bg-geoblue-900 text-white shadow-md relative z-50">
      <!-- Main Header Container -->
      <div class="container mx-auto px-4 h-16 w-full flex flex-row items-center justify-between md:grid md:grid-cols-[1fr_auto_1fr]">
        
        <!-- Logo / Title -->
        <div class="flex items-center flex-shrink-0 md:justify-self-start">
          <a routerLink="/" class="flex items-center space-x-2 text-xl font-bold tracking-wide hover:opacity-90 transition-opacity">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8 text-geoblue-300">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z" />
            </svg>
            <span>GeoRoute</span>
          </a>
        </div>

        <!-- Navigation Desktop -->
        <nav class="hidden md:flex items-center space-x-2 justify-self-center">
          <a routerLink="/search" 
             routerLinkActive="bg-geoblue-800 text-geoblue-300 font-semibold shadow-inner" 
             class="px-4 py-2 rounded-md hover:bg-geoblue-800/50 transition-all">Buscar</a>
          
          <a *ngIf="authService.isAuthenticated()" routerLink="/register" 
             routerLinkActive="bg-geoblue-800 text-geoblue-300 font-semibold shadow-inner" 
             class="px-4 py-2 rounded-md hover:bg-geoblue-800/50 transition-all">Cadastrar</a>
          
          <a *ngIf="authService.isAuthenticated()" routerLink="/import" 
             routerLinkActive="bg-geoblue-800 text-geoblue-300 font-semibold shadow-inner" 
             class="px-4 py-2 rounded-md hover:bg-geoblue-800/50 transition-all">Importar</a>
          
          <a routerLink="/help" 
             routerLinkActive="bg-geoblue-800 text-geoblue-300 font-semibold shadow-inner" 
             class="px-4 py-2 rounded-md hover:bg-geoblue-800/50 transition-all">Ajuda</a>
          
          <!-- Login/Logout Logic -->
          <a *ngIf="!authService.isAuthenticated()" routerLink="/login" 
             routerLinkActive="bg-geoblue-800 text-geoblue-300 font-semibold shadow-inner" 
             class="px-4 py-2 rounded-md hover:bg-geoblue-800/50 transition-all">Login</a>
             
          <button *ngIf="authService.isAuthenticated()" (click)="logout()" 
             class="px-4 py-2 rounded-md hover:bg-geoblue-800/50 transition-all text-left">Logout</button>
        </nav>

        <!-- Actions (Desktop & Mobile) -->
        <div class="flex items-center space-x-4 flex-shrink-0 md:justify-self-end">
          <!-- Theme Toggle -->
          <button (click)="toggleTheme()" class="p-2 rounded-full hover:bg-geoblue-800 transition-colors" [title]="themeService.darkMode() ? 'Mudar para tema claro' : 'Mudar para tema escuro'">
            <svg *ngIf="themeService.darkMode()" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v2.25m6.364.386-1.591 1.591M21 12h-2.25m-.386 6.364-1.591-1.591M12 18.75V21m-4.773-4.227-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z" />
            </svg>
            <svg *ngIf="!themeService.darkMode()" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M21.752 15.002A9.75 9.75 0 0 1 18 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.75 9.75 0 0 0 3 11.25C3 16.635 7.365 21 12.75 21a9.75 9.75 0 0 0 9.002-5.998Z" />
            </svg>
          </button>

          <!-- Mobile Menu Button -->
          <button (click)="toggleMenu()" class="md:hidden p-2 rounded-md hover:bg-geoblue-800 transition-colors focus:outline-none" aria-label="Abrir menu">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
              <path *ngIf="!isMenuOpen()" stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
              <path *ngIf="isMenuOpen()" stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Mobile Menu Dropdown -->
      <div *ngIf="isMenuOpen()" class="md:hidden bg-geoblue-800 border-t border-geoblue-700 animate-fade-in-down absolute w-full left-0 shadow-lg">
        <nav class="container mx-auto px-2 py-4 flex flex-col space-y-2">
          <a routerLink="/search" (click)="closeMenu()" 
             routerLinkActive="bg-geoblue-700 border-l-4 border-geoblue-300 text-white font-semibold" 
             class="block px-4 py-3 rounded-r-md hover:bg-geoblue-700 transition-all">Buscar</a>
          
          <a *ngIf="authService.isAuthenticated()" routerLink="/register" (click)="closeMenu()" 
             routerLinkActive="bg-geoblue-700 border-l-4 border-geoblue-300 text-white font-semibold" 
             class="block px-4 py-3 rounded-r-md hover:bg-geoblue-700 transition-all">Cadastrar</a>
          
          <a *ngIf="authService.isAuthenticated()" routerLink="/import" (click)="closeMenu()" 
             routerLinkActive="bg-geoblue-700 border-l-4 border-geoblue-300 text-white font-semibold" 
             class="block px-4 py-3 rounded-r-md hover:bg-geoblue-700 transition-all">Importar</a>
          
          <a routerLink="/help" (click)="closeMenu()" 
             routerLinkActive="bg-geoblue-700 border-l-4 border-geoblue-300 text-white font-semibold" 
             class="block px-4 py-3 rounded-r-md hover:bg-geoblue-700 transition-all">Ajuda</a>
          
          <a *ngIf="!authService.isAuthenticated()" routerLink="/login" (click)="closeMenu()" 
             routerLinkActive="bg-geoblue-700 border-l-4 border-geoblue-300 text-white font-semibold" 
             class="block px-4 py-3 rounded-r-md hover:bg-geoblue-700 transition-all">Login</a>
             
          <button *ngIf="authService.isAuthenticated()" (click)="logout(); closeMenu()" 
             class="block w-full text-left px-4 py-3 rounded-r-md hover:bg-geoblue-700 transition-all">Logout</button>
        </nav>
      </div>
    </header>
  `
})
export class HeaderComponent {
  themeService = inject(ThemeService);
  authService = inject(AuthService);
  isMenuOpen = signal(false);

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  toggleMenu() {
    this.isMenuOpen.update(v => !v);
  }

  closeMenu() {
    this.isMenuOpen.set(false);
  }
  
  logout() {
    this.authService.logout();
  }
}
