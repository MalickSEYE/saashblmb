import { Routes } from '@angular/router';
export const DAHIRAS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./dahiras-list.component').then(m => m.DahirasListComponent) }
];
