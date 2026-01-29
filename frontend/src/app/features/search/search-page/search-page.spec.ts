import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SearchPageComponent } from './search-page';
import { ClientService } from '../../../core/services/client.service';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { Cliente } from '../../../core/interfaces/cliente.interface';
import { PaginatedClientesResponse } from '../../../core/interfaces/paginated-clientes-response.interface';

describe('SearchPageComponent', () => {
  let component: SearchPageComponent;
  let fixture: ComponentFixture<SearchPageComponent>;
  
  // Mock data based on ClientesResponseDTO
  const mockCliente: Cliente = {
    instalacao: 12345678,
    nomeCliente: 'João da Silva',
    latitude: -23.550520,
    longitude: -46.633308
  };

  const mockPaginatedResponse: PaginatedClientesResponse = {
    pageNumber: 0,
    pageSize: 10,
    totalElements: 1,
    totalPages: 1,
    data: [mockCliente]
  };

  // Create a mock service object
  const clientServiceMock = {
    searchByInstalacao: vi.fn(),
    searchByContaContrato: vi.fn(),
    searchByNumeroSerie: vi.fn(),
    searchByNumeroPoste: vi.fn(),
    openInMaps: vi.fn()
  };

  beforeEach(async () => {
    // Reset mocks
    vi.clearAllMocks();
    clientServiceMock.searchByInstalacao.mockReturnValue(of(mockCliente));
    clientServiceMock.searchByContaContrato.mockReturnValue(of(mockPaginatedResponse));
    clientServiceMock.searchByNumeroSerie.mockReturnValue(of(mockPaginatedResponse));
    clientServiceMock.searchByNumeroPoste.mockReturnValue(of(mockPaginatedResponse));

    await TestBed.configureTestingModule({
      imports: [SearchPageComponent],
      providers: [
        { provide: ClientService, useValue: clientServiceMock },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call searchByInstalacao when search type is instalacao', () => {
    component.searchType = 'instalacao';
    component.searchQuery = '12345678';
    
    component.onSearch();

    expect(clientServiceMock.searchByInstalacao).toHaveBeenCalledWith('12345678');
    expect(component.clienteList()).toEqual([mockCliente]);
    expect(component.isLoading()).toBe(false);
  });

  it('should call searchByContaContrato when search type is contaContrato', () => {
    component.searchType = 'contaContrato';
    component.searchQuery = '7000123456';
    
    component.onSearch();

    expect(clientServiceMock.searchByContaContrato).toHaveBeenCalledWith('7000123456');
    expect(component.clienteList()).toEqual([mockCliente]);
  });

  it('should handle error when client not found', () => {
    component.searchType = 'instalacao';
    component.searchQuery = '999';
    
    const errorResponse = { status: 404, statusText: 'Not Found' };
    clientServiceMock.searchByInstalacao.mockReturnValue(throwError(() => errorResponse));

    component.onSearch();

    expect(clientServiceMock.searchByInstalacao).toHaveBeenCalledWith('999');
    expect(component.clienteList()).toEqual([]);
    expect(component.errorMessage()).toBe('Cliente não encontrado.');
  });

  it('should open in maps directly if directToMap is true', () => {
    component.searchType = 'instalacao';
    component.searchQuery = '12345678';
    component.directToMap = true;
    
    component.onSearch();

    expect(clientServiceMock.searchByInstalacao).toHaveBeenCalledWith('12345678');
    expect(clientServiceMock.openInMaps).toHaveBeenCalledWith(mockCliente.latitude, mockCliente.longitude);
    expect(component.clienteList()).toEqual([]); // Should not populate list if direct map is used
  });
});
