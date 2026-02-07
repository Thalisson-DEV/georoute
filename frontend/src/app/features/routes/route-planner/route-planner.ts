import { Component, inject, signal, OnInit, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouteService } from '../../../core/services/route.service';
import { TeamService } from '../../../core/services/team.service';
import { ClientService } from '../../../core/services/client.service';
import { Cliente } from '../../../core/interfaces/cliente.interface';
import { OrsOptimizationResponse } from '../../../core/interfaces/ors-optimization-response.interface';
import { RouteRequest } from '../../../core/interfaces/route-request.interface';
import { Equipe } from '../../../core/interfaces/equipe.interface';
import { SetorEnum } from '../../../core/interfaces/setor.enum';
import { MunicipioEnum } from '../../../core/interfaces/municipio.enum';
import { RouteHistory } from '../../../core/interfaces/route-history.interface';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-route-planner',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './route-planner.html'
})
export class RoutePlannerComponent implements OnInit {
  routeService = inject(RouteService);
  teamService = inject(TeamService);
  clientService = inject(ClientService);

  // Form inputs
  selectedSetor: SetorEnum | '' = '';
  selectedMunicipio: MunicipioEnum | '' = '';
  selectedTeamId: number | null = null;
  
  // UI State
  activeTab = signal<'new' | 'history'>('new');
  useCurrentLocation = signal(false);
  currentLocation = signal<{lat: number, lon: number} | null>(null);
  locationStatus = signal<'idle' | 'loading' | 'success' | 'error'>('idle');

  // Data
  teams = signal<Equipe[]>([]);
  routeHistory = signal<RouteHistory[]>([]);
  setorOptions = Object.values(SetorEnum);
  municipioOptions = Object.values(MunicipioEnum);
  
  // Cache for client details fetched by ID
  clientCache = signal(new Map<number, Cliente>());

  // State
  isLoading = signal(false);
  isTeamsLoading = signal(false);
  isHistoryLoading = signal(false);
  errorMessage = signal('');
  optimizationResult = signal<OrsOptimizationResponse | null>(null);
  selectedHistoryRouteId = signal<string | null>(null);

  selectedClients = this.routeService.selectedClients;

  // Computed view model for the template to avoid function calls in loops
  stepsView = computed(() => {
    const result = this.optimizationResult();
    if (!result || !result.routes || result.routes.length === 0) return [];

    const steps = result.routes[0].steps;
    const cache = this.clientCache();
    const selected = this.selectedClients();

    return steps.map(step => {
      let client: Cliente | undefined;
      if (step.type === 'job' && step.id) {
        client = selected.find(c => c.instalacao === step.id) || cache.get(step.id);
      }
      return { step, client };
    });
  });

  ngOnInit() {
    this.loadTeams();
  }

  setTab(tab: 'new' | 'history') {
    this.activeTab.set(tab);
    this.errorMessage.set('');
    
    // Reset views slightly when switching contexts if needed
    if (tab === 'new') {
        this.selectedHistoryRouteId.set(null);
        // We keep the optimizationResult if it was just calculated
    }
  }

  toggleLocationSource(useCurrent: boolean) {
      this.useCurrentLocation.set(useCurrent);
      if (useCurrent) {
          this.fetchCurrentLocation();
      } else {
          this.currentLocation.set(null);
          this.locationStatus.set('idle');
      }
  }

  fetchCurrentLocation() {
      if (!navigator.geolocation) {
          this.locationStatus.set('error');
          this.errorMessage.set('Geolocalização não é suportada pelo seu navegador.');
          return;
      }

      this.locationStatus.set('loading');
      navigator.geolocation.getCurrentPosition(
          (position) => {
              this.currentLocation.set({
                  lat: position.coords.latitude,
                  lon: position.coords.longitude
              });
              this.locationStatus.set('success');
          },
          (err) => {
              console.error('Error getting location', err);
              this.locationStatus.set('error');
              this.errorMessage.set('Não foi possível obter sua localização. Verifique as permissões.');
              // Revert toggle if failed
              this.useCurrentLocation.set(false);
          }
      );
  }

  loadTeams() {
    this.isTeamsLoading.set(true);
    const setor = this.selectedSetor === '' ? undefined : this.selectedSetor;
    const municipio = this.selectedMunicipio === '' ? undefined : this.selectedMunicipio;
    
    this.teamService.getTeams(setor, municipio).subscribe({
      next: (data) => {
        this.teams.set(data);
        this.isTeamsLoading.set(false);
        if (this.selectedTeamId && !data.find(t => t.id === this.selectedTeamId)) {
          this.selectedTeamId = null;
          this.routeHistory.set([]);
        }
      },
      error: (err) => {
        console.error('Error loading teams', err);
        this.isTeamsLoading.set(false);
      }
    });
  }

  onSetorChange() {
    this.loadTeams();
  }

  onMunicipioChange() {
    this.loadTeams();
  }

  onTeamChange() {
    // Clear previous results when team changes
    if (this.activeTab() === 'new') {
        this.optimizationResult.set(null);
    }
    this.routeHistory.set([]);
    this.selectedHistoryRouteId.set(null);
    
    if (this.selectedTeamId) {
        this.loadHistory(this.selectedTeamId);
    }
  }

  loadHistory(teamId: number) {
      this.isHistoryLoading.set(true);
      this.routeService.getRouteHistory(teamId).subscribe({
          next: (history) => {
              this.routeHistory.set(history);
              this.isHistoryLoading.set(false);
          },
          error: (err) => {
              console.error('Error loading history', err);
              this.isHistoryLoading.set(false);
          }
      });
  }

  viewHistoryDetail(routeId: string) {
      this.selectedHistoryRouteId.set(routeId);
      this.loadRouteDetail(routeId);
  }

  loadRouteDetail(routeId: string) {
      this.isLoading.set(true);
      this.optimizationResult.set(null);
      
      this.routeService.getRouteDetails(routeId).subscribe({
          next: (response) => {
              this.optimizationResult.set(response);
              this.isLoading.set(false);
              // Fetch details for clients in the route that we don't know
              this.enrichClientDetails(response);
          },
          error: (err) => {
              this.isLoading.set(false);
              this.errorMessage.set('Erro ao carregar detalhes da rota.');
              console.error(err);
          }
      });
  }

  enrichClientDetails(response: OrsOptimizationResponse) {
      if (!response.routes || response.routes.length === 0) return;
      
      const steps = response.routes[0].steps;
      steps.forEach(step => {
          if (step.type === 'job' && step.id) {
              const id = step.id;
              // Check if we already have it in selectedClients or cache
              const cache = this.clientCache();
              const selected = this.selectedClients();

              if (!selected.find(c => c.instalacao === id) && !cache.has(id)) {
                  // Fetch from backend
                  this.clientService.searchByInstalacao(id.toString()).subscribe({
                      next: (client) => {
                          // Check if response is indeed a client object (search can return different types)
                          // Assuming searchByInstalacao returns single object or throws
                          if (client && 'instalacao' in (client as any)) {
                             this.clientCache.update(prev => {
                               const newMap = new Map(prev);
                               newMap.set(id, client as Cliente);
                               return newMap;
                             });
                          }
                      },
                      error: (e) => console.warn(`Could not fetch details for client ${id}`, e)
                  });
              }
          }
      });
  }

  // Helper retained for compatibility if needed, but template should use stepsView
  getClientByInstalacao(id: number | undefined): Cliente | undefined {
    if (!id) return undefined;
    // Check selected list first
    const fromSelected = this.selectedClients().find(c => c.instalacao === id);
    if (fromSelected) return fromSelected;
    
    // Check cache
    return this.clientCache().get(id);
  }

  removeClient(cliente: Cliente) {
    this.routeService.removeClient(cliente);
  }

  clearRoute() {
    this.routeService.clearRoute();
    this.optimizationResult.set(null);
  }

  optimize() {
    if (!this.selectedTeamId) {
      this.errorMessage.set('Por favor, selecione uma equipe.');
      return;
    }

    if (this.selectedClients().length === 0) {
      this.errorMessage.set('Adicione pelo menos um cliente ao roteiro.');
      return;
    }

    // Check location if required
    let lat: number | undefined;
    let lon: number | undefined;

    if (this.useCurrentLocation()) {
        const loc = this.currentLocation();
        if (!loc) {
            this.errorMessage.set('Aguardando localização atual...');
            this.fetchCurrentLocation(); // Try again
            return;
        }
        lat = loc.lat;
        lon = loc.lon;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.optimizationResult.set(null);

    const request: RouteRequest = {
      teamId: this.selectedTeamId,
      clients: this.selectedClients(),
      currentLat: lat,
      currentLon: lon
    };

    this.routeService.optimizeRoute(request).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.optimizationResult.set(response);
        // Refresh history to show the new one
        this.loadHistory(this.selectedTeamId!);
        this.enrichClientDetails(response);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.errorMessage.set('Erro ao otimizar rota. Verifique os dados e tente novamente.');
        console.error(err);
      }
    });
  }

  getGoogleMapsUrl(): string {
    const result = this.optimizationResult();
    if (!result || !result.routes || result.routes.length === 0) return '';

    const steps = result.routes[0].steps;
    const origin = steps[0].location; // [lon, lat]
    const destination = steps[steps.length - 1].location;
    
    // Waypoints (exclude start and end)
    const waypoints = steps.slice(1, -1)
      .filter(step => step.type === 'job')
      .map(step => `${step.location[1]},${step.location[0]}`)
      .join('|');

    const baseUrl = 'https://www.google.com/maps/dir/?api=1';
    const originParam = `&origin=${origin[1]},${origin[0]}`;
    const destParam = `&destination=${destination[1]},${destination[0]}`;
    const waypointsParam = waypoints ? `&waypoints=${waypoints}` : '';

    return `${baseUrl}${originParam}${destParam}${waypointsParam}`;
  }

  openGoogleMaps() {
    const url = this.getGoogleMapsUrl();
    if (url) {
      window.open(url, '_blank');
    }
  }
}
