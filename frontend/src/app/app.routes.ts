import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./shared/components/layout/layout.component').then(m => m.LayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
        title: 'Tableau de bord — Mouride SaaS'
      },
      {
        path: 'membres',
        loadChildren: () => import('./features/membres/membres.routes').then(m => m.MEMBRES_ROUTES),
        title: 'Membres'
      },
      {
        path: 'dahiras',
        loadChildren: () => import('./features/dahiras/dahiras.routes').then(m => m.DAHIRAS_ROUTES),
        title: 'Dahiras'
      },
      {
        path: 'cotisations',
        loadChildren: () => import('./features/cotisations/cotisations.routes').then(m => m.COTISATIONS_ROUTES),
        title: 'Cotisations'
      },
      {
        path: 'evenements',
        loadChildren: () => import('./features/evenements/evenements.routes').then(m => m.EVENEMENTS_ROUTES),
        title: 'Événements'
      },
      {
        path: 'finance',
        loadChildren: () => import('./features/finance/finance.routes').then(m => m.FINANCE_ROUTES),
        canActivate: [() => roleGuard(['SUPER_ADMIN', 'ADMIN'])],
        title: 'Finance'
      },
      {
        path: 'contenus',
        loadChildren: () => import('./features/contenus/contenus.routes').then(m => m.CONTENUS_ROUTES),
        title: 'Contenus religieux'
      },
      {
        path: 'projets',
        loadChildren: () => import('./features/projets/projets.routes').then(m => m.PROJETS_ROUTES),
        title: 'Projets sociaux'
      },
      {
        path: 'communication',
        loadChildren: () => import('./features/communication/communication.routes').then(m => m.COMMUNICATION_ROUTES),
        title: 'Communication'
      },
      {
        path: 'admin',
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES),
        canActivate: [() => roleGuard(['SUPER_ADMIN'])],
        title: 'Administration'
      },
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
