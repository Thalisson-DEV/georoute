import { SetorEnum } from './setor.enum';
import { MunicipioEnum } from './municipio.enum';

export interface Equipe {
  id: number;
  nome: string;
  latitudeBase: number;
  longitudeBase: number;
  setor: SetorEnum;
  municipio: MunicipioEnum;
}

export interface EquipeRequest {
  nome: string;
  latitudeBase: number;
  longitudeBase: number;
  setor: SetorEnum;
  municipio: MunicipioEnum;
}
