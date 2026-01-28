import { Injectable, signal, effect, inject, PLATFORM_ID } from '@angular/core';
import { DOCUMENT, isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private document = inject(DOCUMENT);
  private platformId = inject(PLATFORM_ID);
  
  // Signal to track theme state
  darkMode = signal<boolean>(false);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      const savedTheme = localStorage.getItem('theme');
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      
      if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
        this.darkMode.set(true);
      }
    }

    // Effect to apply class when signal changes
    effect(() => {
      if (this.darkMode()) {
        this.document.documentElement.classList.add('dark');
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('theme', 'dark');
        }
      } else {
        this.document.documentElement.classList.remove('dark');
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('theme', 'light');
        }
      }
    });
  }

  toggleTheme() {
    this.darkMode.update(d => !d);
  }
}
