import { Routes } from '@angular/router';
export const COTISATIONS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./cotisations-list.component').then(m => m.CotisationsListComponent) }
];
