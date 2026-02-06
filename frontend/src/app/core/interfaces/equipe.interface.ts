import { SetorEnum } from './setor.enum';

export interface Equipe {
  id: number;
  nome: string;
  latitudeBase: number;
  longitudeBase: number;
  setor: SetorEnum;
}

export interface EquipeRequest {
  nome: string;
  latitudeBase: number;
  longitudeBase: number;
  setor: SetorEnum;
}
