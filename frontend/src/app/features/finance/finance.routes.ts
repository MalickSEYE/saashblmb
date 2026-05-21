import { Routes } from '@angular/router';
export const FINANCE_ROUTES: Routes = [
  { path: '', loadComponent: () => import('../extra-components').then(m => m.FinanceComponent) }
];
