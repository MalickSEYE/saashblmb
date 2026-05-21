import { Routes } from '@angular/router';
export const EVENEMENTS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./evenements-list.component').then(m => m.EvenementsListComponent) }
];
