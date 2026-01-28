import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ImportClientsComponent } from './import-clients';
import { ClientService } from '../../../core/services/client.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

describe('ImportClientsComponent', () => {
  let component: ImportClientsComponent;
  let fixture: ComponentFixture<ImportClientsComponent>;
  
  const clientServiceMock = {
    uploadClients: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    clientServiceMock.uploadClients.mockReturnValue(of('Success'));

    await TestBed.configureTestingModule({
      imports: [ImportClientsComponent],
      providers: [
        { provide: ClientService, useValue: clientServiceMock },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportClientsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should upload file when selected', () => {
    const file = new File([''], 'test.csv', { type: 'text/csv' });
    const event = { target: { files: [file] } };

    component.onFileSelected(event);

    expect(clientServiceMock.uploadClients).toHaveBeenCalledWith(file);
    expect(component.message()).toBe('Success');
    expect(component.isError()).toBe(false);
  });

  it('should handle error during upload', () => {
    const file = new File([''], 'test.csv', { type: 'text/csv' });
    clientServiceMock.uploadClients.mockReturnValue(throwError(() => new Error('Error')));

    component.uploadFile(file);

    expect(component.message()).toContain('Erro ao enviar arquivo');
    expect(component.isError()).toBe(true);
  });
});
