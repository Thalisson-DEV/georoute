import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClientCardComponent } from './client-card';
import { Cliente } from '../../../core/interfaces/cliente.interface';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ClientCardComponent', () => {
  let component: ClientCardComponent;
  let fixture: ComponentFixture<ClientCardComponent>;

  const mockCliente: Cliente = {
    instalacao: 12345678,
    nomeCliente: 'João da Silva',
    contaContrato: 7000123456,
    numeroSerie: 987654321,
    numeroPoste: 'P-12345',
    latitude: -23.550520,
    longitude: -46.633308
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientCardComponent],
      providers: [
          provideHttpClient(),
          provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientCardComponent);
    component = fixture.componentInstance;
    component.cliente = mockCliente;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display client data', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h3')?.textContent).toContain('João da Silva');
    expect(compiled.textContent).toContain('12345678');
  });
});