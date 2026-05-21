import { Routes } from '@angular/router';
export const PROJETS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('../extra-components').then(m => m.ProjetsListComponent) }
];
