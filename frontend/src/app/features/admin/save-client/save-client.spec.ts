import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SaveClientComponent } from './save-client';
import { ClientService } from '../../../core/services/client.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('SaveClientComponent', () => {
  let component: SaveClientComponent;
  let fixture: ComponentFixture<SaveClientComponent>;
  
  const clientServiceMock = {
    createClient: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    clientServiceMock.createClient.mockReturnValue(of({}));

    await TestBed.configureTestingModule({
      imports: [SaveClientComponent, FormsModule],
      providers: [
        { provide: ClientService, useValue: clientServiceMock },
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaveClientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit form and show success message', () => {
    // Fill form data
    component.client.nomeCliente = 'New Client';
    component.client.instalacao = 12345678;
    
    component.onSubmit();

    expect(clientServiceMock.createClient).toHaveBeenCalledWith(expect.objectContaining({
      nomeCliente: 'New Client',
      instalacao: 12345678
    }));
    expect(component.successMessage()).toBe('Cliente cadastrado com sucesso!');
    expect(component.errorMessage()).toBe('');
    
    // Check form reset
    expect(component.client.nomeCliente).toBe('');
    expect(component.client.instalacao).toBe(0);
  });

  it('should handle error on submit', () => {
    clientServiceMock.createClient.mockReturnValue(throwError(() => new Error('Error')));
    
    component.onSubmit();

    expect(clientServiceMock.createClient).toHaveBeenCalled();
    expect(component.errorMessage()).toContain('Erro ao cadastrar cliente');
    expect(component.successMessage()).toBe('');
  });
});