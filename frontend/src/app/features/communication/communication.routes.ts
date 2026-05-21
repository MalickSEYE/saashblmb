import { Routes } from '@angular/router';
export const COMMUNICATION_ROUTES: Routes = [
  { path: '', loadComponent: () => import('../extra-components').then(m => m.CommunicationComponent) }
];
