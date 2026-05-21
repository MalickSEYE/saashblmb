// auth.routes.ts
import { Routes } from '@angular/router';
export const AUTH_ROUTES: Routes = [
  { path: 'login',    loadComponent: () => import('./login.component').then(m => m.LoginComponent) },
  { path: '',         redirectTo: 'login', pathMatch: 'full' }
];

// membres.routes.ts
import { Routes as MembresR } from '@angular/router';
export const MEMBRES_ROUTES: MembresR = [
  { path: '', loadComponent: () => import('./membres-list.component').then(m => m.MembresListComponent) },
];

// dahiras.routes.ts
import { Routes as DahirasR } from '@angular/router';
export const DAHIRAS_ROUTES: DahirasR = [
  { path: '', loadComponent: () => import('./dahiras-list.component').then(m => m.DahirasListComponent) },
];

// cotisations.routes.ts
import { Routes as CotiR } from '@angular/router';
export const COTISATIONS_ROUTES: CotiR = [
  { path: '', loadComponent: () => import('./cotisations-list.component').then(m => m.CotisationsListComponent) },
];

// evenements.routes.ts
import { Routes as EvenR } from '@angular/router';
export const EVENEMENTS_ROUTES: EvenR = [
  { path: '', loadComponent: () => import('./evenements-list.component').then(m => m.EvenementsListComponent) },
];

// finance.routes.ts
import { Routes as FinR } from '@angular/router';
export const FINANCE_ROUTES: FinR = [
  { path: '', loadComponent: () => import('./finance.component').then(m => m.FinanceComponent) },
];

// contenus.routes.ts
import { Routes as ContR } from '@angular/router';
export const CONTENUS_ROUTES: ContR = [
  { path: '', loadComponent: () => import('./contenus-list.component').then(m => m.ContenusListComponent) },
];

// projets.routes.ts
import { Routes as ProjR } from '@angular/router';
export const PROJETS_ROUTES: ProjR = [
  { path: '', loadComponent: () => import('./projets-list.component').then(m => m.ProjetsListComponent) },
];

// communication.routes.ts
import { Routes as CommR } from '@angular/router';
export const COMMUNICATION_ROUTES: CommR = [
  { path: '', loadComponent: () => import('./communication.component').then(m => m.CommunicationComponent) },
];

// admin.routes.ts
import { Routes as AdminR } from '@angular/router';
export const ADMIN_ROUTES: AdminR = [
  { path: '', loadComponent: () => import('./admin.component').then(m => m.AdminComponent) },
];
