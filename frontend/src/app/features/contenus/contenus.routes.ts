import { Routes } from '@angular/router';
export const CONTENUS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('../extra-components').then(m => m.ContenusListComponent) }
];
