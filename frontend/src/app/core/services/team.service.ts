import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Equipe, EquipeRequest } from '../interfaces/equipe.interface';
import { SetorEnum } from '../interfaces/setor.enum';
import { MunicipioEnum } from '../interfaces/municipio.enum';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/equipes`;

  getTeams(setor?: SetorEnum, municipio?: MunicipioEnum): Observable<Equipe[]> {
    let params = new HttpParams();
    if (setor) {
      params = params.set('setor', setor);
    }
    if (municipio) {
      params = params.set('municipio', municipio);
    }
    return this.http.get<Equipe[]>(this.apiUrl, { params });
  }

  createTeam(team: EquipeRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, team);
  }

  updateTeam(id: number, team: EquipeRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, team);
  }

  deleteTeam(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
