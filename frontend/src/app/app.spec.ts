import { describe, it, expect, beforeEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { ThemeService } from './core/services/theme.service';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';

class MockThemeService {
  darkMode = signal(false);
  toggleTheme() {}
}

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: ThemeService, useClass: MockThemeService },
        provideRouter([])
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});