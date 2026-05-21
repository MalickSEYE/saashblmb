import { Routes } from '@angular/router';

export const MEMBRES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./membres-list.component').then(m => m.MembresListComponent)
  },
  {
    path: 'nouveau',
    loadComponent: () => import('./membre-form.component').then(m => m.MembreFormComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./membre-form.component').then(m => m.MembreFormComponent)
  }
];
