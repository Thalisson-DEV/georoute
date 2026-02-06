import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { TeamService } from '../../../core/services/team.service';
import { Equipe, EquipeRequest } from '../../../core/interfaces/equipe.interface';
import { SetorEnum } from '../../../core/interfaces/setor.enum';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-manage-teams',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-teams.html'
})
export class ManageTeamsComponent implements OnInit {
  teamService = inject(TeamService);

  teams = signal<Equipe[]>([]);
  isLoading = signal(false);
  errorMessage = signal('');

  // Modal State
  isModalOpen = signal(false);
  isEditing = signal(false);
  currentTeamId: number | null = null;

  // Form Model
  formData: EquipeRequest = {
    nome: '',
    latitudeBase: 0,
    longitudeBase: 0,
    setor: SetorEnum.LEITURA
  };

  setorOptions = Object.values(SetorEnum);

  ngOnInit() {
    this.loadTeams();
  }

  loadTeams() {
    this.isLoading.set(true);
    this.teamService.getTeams().subscribe({
      next: (data) => {
        this.teams.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading teams', err);
        this.errorMessage.set('Erro ao carregar equipes.');
        this.isLoading.set(false);
      }
    });
  }

  openCreateModal() {
    this.isEditing.set(false);
    this.currentTeamId = null;
    this.resetForm();
    this.isModalOpen.set(true);
  }

  openEditModal(team: Equipe) {
    this.isEditing.set(true);
    this.currentTeamId = team.id;
    this.formData = {
      nome: team.nome,
      latitudeBase: team.latitudeBase,
      longitudeBase: team.longitudeBase,
      setor: team.setor
    };
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.errorMessage.set('');
  }

  onSubmit(form: NgForm) {
    if (form.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    const request$ = this.isEditing() && this.currentTeamId
      ? this.teamService.updateTeam(this.currentTeamId, this.formData)
      : this.teamService.createTeam(this.formData);

    request$.subscribe({
      next: () => {
        this.isLoading.set(false);
        this.closeModal();
        this.loadTeams(); // Refresh list
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        if (err.status === 400) {
           this.errorMessage.set('Dados inválidos. Verifique se o nome já existe.');
        } else {
           this.errorMessage.set('Erro ao salvar equipe.');
        }
        console.error(err);
      }
    });
  }

  deleteTeam(team: Equipe) {
    if (!confirm(`Tem certeza que deseja remover a equipe "${team.nome}"?`)) return;

    this.teamService.deleteTeam(team.id).subscribe({
      next: () => {
        this.loadTeams();
      },
      error: (err) => {
        console.error(err);
        alert('Erro ao remover equipe.');
      }
    });
  }

  resetForm() {
    this.formData = {
      nome: '',
      latitudeBase: 0,
      longitudeBase: 0,
      setor: SetorEnum.LEITURA
    };
  }
}
