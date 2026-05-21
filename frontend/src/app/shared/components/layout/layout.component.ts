import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/services';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="app-layout" [class.sidebar-collapsed]="collapsed()">
      <!-- SIDEBAR -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <span class="logo-icon">☪</span>
          <span class="logo-text" *ngIf="!collapsed()">Mouride SaaS</span>
        </div>

        <nav class="sidebar-nav">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📊</span>
            <span class="nav-label" *ngIf="!collapsed()">Tableau de bord</span>
          </a>
          <a routerLink="/membres" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">👥</span>
            <span class="nav-label" *ngIf="!collapsed()">Membres</span>
          </a>
          <a routerLink="/dahiras" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">🕌</span>
            <span class="nav-label" *ngIf="!collapsed()">Dahiras</span>
          </a>
          <a routerLink="/cotisations" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">💳</span>
            <span class="nav-label" *ngIf="!collapsed()">Cotisations</span>
          </a>
          <a routerLink="/evenements" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📅</span>
            <span class="nav-label" *ngIf="!collapsed()">Événements</span>
          </a>
          <a routerLink="/finance" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">💰</span>
            <span class="nav-label" *ngIf="!collapsed()">Finance</span>
          </a>
          <a routerLink="/contenus" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📖</span>
            <span class="nav-label" *ngIf="!collapsed()">Contenus religieux</span>
          </a>
          <a routerLink="/projets" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">🤝</span>
            <span class="nav-label" *ngIf="!collapsed()">Projets sociaux</span>
          </a>
          <a routerLink="/communication" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📢</span>
            <span class="nav-label" *ngIf="!collapsed()">Communication</span>
          </a>
          <div class="nav-divider"></div>
          <a routerLink="/admin" routerLinkActive="active" class="nav-item"
             *ngIf="authService.hasRole(['SUPER_ADMIN'])">
            <span class="nav-icon">⚙️</span>
            <span class="nav-label" *ngIf="!collapsed()">Administration</span>
          </a>
        </nav>

        <div class="sidebar-footer">
          <div class="user-info" *ngIf="!collapsed()">
            <div class="user-email">{{ authService.currentUser()?.email }}</div>
            <div class="user-role">{{ authService.currentUser()?.role }}</div>
          </div>
          <button class="btn-logout" (click)="authService.logout()" title="Se déconnecter">
            🚪
          </button>
        </div>
      </aside>

      <!-- MAIN -->
      <div class="main-wrapper">
        <header class="top-bar">
          <button class="btn-toggle" (click)="collapsed.set(!collapsed())" title="Réduire le menu">
            {{ collapsed() ? '☰' : '✕' }}
          </button>
          <div class="top-right">
            <span class="top-user">{{ authService.currentUser()?.email }}</span>
            <span class="top-badge">{{ authService.currentUser()?.role }}</span>
          </div>
        </header>
        <main class="content">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: [`
    .app-layout { display: flex; min-height: 100vh; background: #f5f2ec; }
    /* SIDEBAR */
    .sidebar { width: 240px; background: #1A4731; display: flex; flex-direction: column;
               transition: width 0.25s; flex-shrink: 0; position: sticky; top: 0;
               height: 100vh; overflow: hidden; }
    .sidebar-collapsed .sidebar { width: 64px; }
    .sidebar-header { display: flex; align-items: center; gap: 0.75rem; padding: 1.25rem 1rem;
                      border-bottom: 1px solid rgba(255,255,255,0.1); }
    .logo-icon { font-size: 1.6rem; flex-shrink: 0; }
    .logo-text { font-family: serif; font-size: 1.1rem; font-weight: 700; color: #F0C96B;
                 white-space: nowrap; overflow: hidden; }
    .sidebar-nav { flex: 1; padding: 0.75rem 0; overflow-y: auto; overflow-x: hidden; }
    .nav-item { display: flex; align-items: center; gap: 0.75rem; padding: 0.7rem 1rem;
                color: rgba(255,255,255,0.75); text-decoration: none; font-size: 0.9rem;
                transition: all 0.15s; white-space: nowrap; overflow: hidden; border-radius: 0; }
    .nav-item:hover { background: rgba(255,255,255,0.08); color: white; }
    .nav-item.active { background: rgba(200,149,42,0.25); color: #F0C96B; border-left: 3px solid #C8952A; }
    .nav-icon { font-size: 1.1rem; flex-shrink: 0; width: 24px; text-align: center; }
    .nav-label { white-space: nowrap; }
    .nav-divider { height: 1px; background: rgba(255,255,255,0.1); margin: 0.5rem 1rem; }
    .sidebar-footer { padding: 1rem; border-top: 1px solid rgba(255,255,255,0.1);
                      display: flex; align-items: center; gap: 0.75rem; }
    .user-info { flex: 1; overflow: hidden; }
    .user-email { font-size: 0.78rem; color: rgba(255,255,255,0.8); truncate: ellipsis;
                  white-space: nowrap; overflow: hidden; }
    .user-role  { font-size: 0.7rem; color: #C8952A; font-weight: 600; margin-top: 0.1rem; }
    .btn-logout { background: rgba(255,255,255,0.1); border: none; color: white;
                  cursor: pointer; padding: 0.4rem 0.6rem; border-radius: 6px;
                  font-size: 1rem; flex-shrink: 0; }
    .btn-logout:hover { background: rgba(255,80,80,0.3); }
    /* MAIN */
    .main-wrapper { flex: 1; display: flex; flex-direction: column; min-width: 0; }
    .top-bar { background: white; padding: 0.75rem 1.5rem; display: flex;
               justify-content: space-between; align-items: center;
               box-shadow: 0 1px 4px rgba(0,0,0,0.06); position: sticky; top: 0; z-index: 10; }
    .btn-toggle { background: none; border: none; font-size: 1.2rem; cursor: pointer;
                  color: #555; padding: 0.25rem 0.5rem; }
    .top-right { display: flex; align-items: center; gap: 0.75rem; }
    .top-user { font-size: 0.85rem; color: #555; }
    .top-badge { background: #1A4731; color: #F0C96B; padding: 0.2rem 0.6rem;
                 border-radius: 999px; font-size: 0.72rem; font-weight: 600; }
    .content { flex: 1; }
  `]
})
export class LayoutComponent {
  authService = inject(AuthService);
  collapsed = signal(false);
}
