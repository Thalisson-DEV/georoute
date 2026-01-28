import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store token on successful login', () => {
    const mockResponse = { token: 'fake-jwt-token' };
    const credentials = { email: 'user@example.com', password: 'password' };

    service.login(credentials).subscribe(response => {
      expect(response.token).toBe('fake-jwt-token');
      expect(service.isAuthenticated()).toBe(true);
      expect(localStorage.getItem('auth_token')).toBe('fake-jwt-token');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should clear token on logout', () => {
    localStorage.setItem('auth_token', 'fake-token');
    service.isAuthenticated.set(true);

    service.logout();

    expect(localStorage.getItem('auth_token')).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
  });
});
