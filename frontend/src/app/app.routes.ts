import { Routes } from '@angular/router';
import { SearchPageComponent } from './features/search/search-page/search-page';
import { HelpPageComponent } from './features/help/help-page/help-page';
import { LoginPageComponent } from './features/auth/login-page/login-page';
import { SaveClientComponent } from './features/admin/save-client/save-client';
import { ImportClientsComponent } from './features/admin/import-clients/import-clients';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'search', pathMatch: 'full' },
  { path: 'search', component: SearchPageComponent, title: 'GeoRoute - Buscar Clientes' },
  { 
    path: 'register', 
    component: SaveClientComponent, 
    title: 'GeoRoute - Cadastrar Cliente',
    canActivate: [authGuard] 
  },
  { 
    path: 'import', 
    component: ImportClientsComponent, 
    title: 'GeoRoute - Importar Clientes',
    canActivate: [authGuard]
  },
  { path: 'help', component: HelpPageComponent, title: 'GeoRoute - Ajuda' },
  { path: 'login', component: LoginPageComponent, title: 'GeoRoute - Login' },
  // Wildcard route for 404
  { path: '**', redirectTo: 'search' }
];