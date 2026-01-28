import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ClientService } from './client.service';
import { environment } from '../../../environments/environment';
import { ClienteRequest } from '../interfaces/cliente-request.interface';
import { Cliente } from '../interfaces/cliente.interface';

describe('ClientService', () => {
  let service: ClientService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/clientes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClientService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ClientService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a client', () => {
    const newClient: ClienteRequest = { 
      instalacao: 12345678, 
      contaContrato: 7000123456, 
      numeroSerie: 987654321, 
      numeroPoste: 'P-12345',
      nomeCliente: 'João da Silva', 
      latitude: -23.550520,
      longitude: -46.633308
    };
    const mockResponse: Cliente = { 
        instalacao: 12345678, 
        nomeCliente: 'João da Silva', 
        latitude: -23.550520, 
        longitude: -46.633308 
    };

    service.createClient(newClient).subscribe(client => {
      expect(client).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newClient);
    req.flush(mockResponse);
  });

  it('should search by instalacao', () => {
    const mockClient: Cliente = { 
        instalacao: 12345678, 
        nomeCliente: 'João da Silva', 
        latitude: -23.550520, 
        longitude: -46.633308 
    };

    service.searchByInstalacao('12345678').subscribe(client => {
      expect(client).toEqual(mockClient);
    });

    const req = httpMock.expectOne(`${apiUrl}/instalacao/12345678`);
    expect(req.request.method).toBe('GET');
    req.flush(mockClient);
  });

  it('should search by conta contrato', () => {
    const mockClient: Cliente = { 
        instalacao: 12345678, 
        nomeCliente: 'João da Silva', 
        latitude: -23.550520, 
        longitude: -46.633308 
    };

    service.searchByContaContrato('7000123456').subscribe(client => {
      expect(client).toEqual(mockClient);
    });

    const req = httpMock.expectOne(`${apiUrl}/conta-contrato/7000123456`);
    expect(req.request.method).toBe('GET');
    req.flush(mockClient);
  });
});
