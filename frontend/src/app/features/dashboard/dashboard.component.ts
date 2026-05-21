import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/services';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard">
      <div class="page-header">
        <h1>Tableau de bord</h1>
        <span class="subtitle">Vue d'ensemble de la plateforme</span>
      </div>

      <!-- KPI Cards -->
      <div class="kpi-grid">
        <div class="kpi-card kpi-membres" routerLink="/membres">
          <div class="kpi-icon">👥</div>
          <div class="kpi-body">
            <div class="kpi-value">{{ stats?.totalMembres ?? '—' }}</div>
            <div class="kpi-label">Total membres</div>
            <div class="kpi-sub">{{ stats?.membresActifs ?? 0 }} actifs</div>
          </div>
        </div>
        <div class="kpi-card kpi-dahiras" routerLink="/dahiras">
          <div class="kpi-icon">🕌</div>
          <div class="kpi-body">
            <div class="kpi-value">{{ stats?.totalDahiras ?? '—' }}</div>
            <div class="kpi-label">Dahiras</div>
            <div class="kpi-sub">Organismes actifs</div>
          </div>
        </div>
        <div class="kpi-card kpi-finance" routerLink="/cotisations">
          <div class="kpi-icon">💰</div>
          <div class="kpi-body">
            <div class="kpi-value">{{ (stats?.totalCotisations ?? 0) | number:'1.0-0' }} FCFA</div>
            <div class="kpi-label">Cotisations totales</div>
            <div class="kpi-sub">{{ (stats?.cotisationsCeMois ?? 0) | number:'1.0-0' }} ce mois</div>
          </div>
        </div>
        <div class="kpi-card kpi-events" routerLink="/evenements">
          <div class="kpi-icon">📅</div>
          <div class="kpi-body">
            <div class="kpi-value">{{ stats?.evenementsEnCours ?? '—' }}</div>
            <div class="kpi-label">Événements en cours</div>
            <div class="kpi-sub">Magal, Gamou, Réunions</div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="section-title">Actions rapides</div>
      <div class="quick-actions">
        <a routerLink="/membres" [queryParams]="{action:'new'}" class="action-btn">
          <span class="action-icon">➕</span> Nouveau membre
        </a>
        <a routerLink="/cotisations" [queryParams]="{action:'new'}" class="action-btn">
          <span class="action-icon">💳</span> Enregistrer cotisation
        </a>
        <a routerLink="/evenements" [queryParams]="{action:'new'}" class="action-btn">
          <span class="action-icon">📆</span> Créer événement
        </a>
        <a routerLink="/communication" class="action-btn">
          <span class="action-icon">📢</span> Envoyer notification
        </a>
      </div>

      <!-- Recent Activity -->
      <div class="section-title">Activité récente</div>
      <div class="activity-card">
        <div *ngIf="loading" class="loading">Chargement...</div>
        <div *ngIf="!loading && !stats" class="empty">
          <p>🕌 Bienvenue sur Mouride SaaS Platform</p>
          <p>Commencez par créer votre premier Dahira et ajouter des membres.</p>
          <a routerLink="/dahiras" class="action-btn" style="display:inline-block;margin-top:1rem">
            Créer un Dahira →
          </a>
        </div>
        <div *ngIf="stats" class="stats-row">
          <div class="stat-item">
            <span class="stat-num">{{ stats.membresActifs }}</span>
            <span class="stat-text">Membres actifs</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">{{ (stats.totalCotisations | number:'1.0-0') }}</span>
            <span class="stat-text">FCFA collectés</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">{{ stats.totalDahiras }}</span>
            <span class="stat-text">Dahiras actifs</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { margin-bottom: 1.5rem; }
    .page-header h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 1.25rem; margin-bottom: 2rem; }
    .kpi-card { background: white; border-radius: 16px; padding: 1.5rem;
                box-shadow: 0 2px 16px rgba(0,0,0,0.08); cursor: pointer;
                display: flex; align-items: center; gap: 1rem;
                transition: transform 0.2s, box-shadow 0.2s; border-top: 4px solid transparent; }
    .kpi-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
    .kpi-membres { border-top-color: #1A4731; }
    .kpi-dahiras { border-top-color: #C8952A; }
    .kpi-finance { border-top-color: #2D7A52; }
    .kpi-events  { border-top-color: #8B6914; }
    .kpi-icon { font-size: 2rem; }
    .kpi-value { font-size: 1.8rem; font-weight: 700; color: #1C1712; line-height: 1; }
    .kpi-label { font-size: 0.8rem; font-weight: 600; text-transform: uppercase;
                 letter-spacing: 0.06em; color: #888; margin-top: 0.3rem; }
    .kpi-sub   { font-size: 0.78rem; color: #aaa; margin-top: 0.2rem; }
    .section-title { font-weight: 700; font-size: 1rem; color: #1A4731;
                     margin-bottom: 1rem; padding-left: 0.5rem;
                     border-left: 3px solid #C8952A; }
    .quick-actions { display: flex; flex-wrap: wrap; gap: 0.75rem; margin-bottom: 2rem; }
    .action-btn { display: flex; align-items: center; gap: 0.5rem;
                  padding: 0.65rem 1.25rem; background: #1A4731; color: #F0C96B;
                  border-radius: 10px; text-decoration: none; font-size: 0.88rem;
                  font-weight: 600; transition: background 0.2s; }
    .action-btn:hover { background: #2D7A52; }
    .action-icon { font-size: 1rem; }
    .activity-card { background: white; border-radius: 16px; padding: 2rem;
                     box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    .loading { text-align: center; color: #888; }
    .empty { text-align: center; color: #666; }
    .stats-row { display: flex; gap: 2rem; justify-content: center; }
    .stat-item { text-align: center; }
    .stat-num  { display: block; font-size: 2rem; font-weight: 700; color: #1A4731; }
    .stat-text { display: block; font-size: 0.85rem; color: #888; margin-top: 0.25rem; }
  `]
})
export class DashboardComponent implements OnInit {
  private api = inject(ApiService);
  stats: any = null;
  loading = true;

  ngOnInit(): void {
    this.api.getDashboardStats().subscribe({
      next: s => { this.stats = s; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
