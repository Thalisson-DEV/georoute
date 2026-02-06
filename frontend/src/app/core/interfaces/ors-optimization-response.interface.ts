export interface OrsOptimizationResponse {
  routes: OrsRoute[];
  summary: OrsSummary;
}

export interface OrsRoute {
  vehicle: number;
  steps: OrsStep[];
}

export interface OrsStep {
  type: string;
  location: number[];
  arrival: number;
  duration: number;
  distance: number;
  service: number;
  waiting_time: number;
  job?: number;
  id?: number; // Maps to Client Installation
}

export interface OrsSummary {
  cost: number;
  duration: number;
  distance: number;
}
