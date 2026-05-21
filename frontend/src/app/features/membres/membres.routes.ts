// membres/membres.routes.ts
import { Routes } from '@angular/router';
export const MEMBRES_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./membres-list.component').then(m => m.MembresListComponent) }
];
