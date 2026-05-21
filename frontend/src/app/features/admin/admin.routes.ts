import { Routes } from '@angular/router';
export const ADMIN_ROUTES: Routes = [
  { path: '', loadComponent: () => import('../extra-components').then(m => m.AdminComponent) }
];
