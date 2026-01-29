import { Cliente } from './cliente.interface';

export interface PaginatedClientesResponse {
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  data: Cliente[];
  content?: Cliente[];
}
