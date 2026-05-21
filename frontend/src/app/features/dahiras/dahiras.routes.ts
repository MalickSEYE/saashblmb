import { Routes } from '@angular/router';

export const DAHIRAS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./dahiras-list.component').then(m => m.DahirasListComponent)
  },
  {
    path: 'nouveau',
    loadComponent: () => import('./dahira-form.component').then(m => m.DahiraFormComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./dahira-form.component').then(m => m.DahiraFormComponent)
  }
];
