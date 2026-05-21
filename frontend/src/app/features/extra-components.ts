import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

// ── Finance Component ──────────────────────────────────────
@Component({
  selector: 'app-finance',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>💰 Finance</h1><span class="subtitle">Tableau de bord financier</span></div>
        <div class="header-actions">
          <a [href]="api + '/export/membres/excel'" class="btn-secondary">📥 Export CSV</a>
        </div>
      </div>
      <div class="finance-kpis" *ngIf="stats">
        <div class="fk-card"><div class="fkv">{{ stats.totalValide | number:'1.0-0' }}</div>
          <div class="fkl">FCFA Total validé</div></div>
        <div class="fk-card accent"><div class="fkv">{{ stats.totalCeMois | number:'1.0-0' }}</div>
          <div class="fkl">FCFA ce mois</div></div>
        <div class="fk-card warn"><div class="fkv">{{ stats.nbEnAttente }}</div>
          <div class="fkl">Paiements en attente</div></div>
        <div class="fk-card danger"><div class="fkv">{{ stats.nbRejete }}</div>
          <div class="fkl">Rejetés</div></div>
      </div>
      <div *ngIf="!stats" class="loading">Chargement des données financières...</div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .header-actions { display: flex; gap: 0.75rem; }
    .btn-secondary { padding: 0.6rem 1.25rem; background: #f5f2ec; color: #1A4731; border: 1px solid #ddd;
                     border-radius: 8px; text-decoration: none; font-size: 0.88rem; }
    .finance-kpis { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.25rem; }
    .fk-card { background: white; border-radius: 14px; padding: 1.5rem;
               box-shadow: 0 2px 12px rgba(0,0,0,0.07); border-top: 4px solid #1A4731; }
    .fk-card.accent { border-top-color: #C8952A; }
    .fk-card.warn   { border-top-color: #f39c12; }
    .fk-card.danger { border-top-color: #e74c3c; }
    .fkv { font-size: 1.8rem; font-weight: 700; color: #1C1712; }
    .fkl { font-size: 0.8rem; color: #888; margin-top: 0.3rem; }
    .loading { padding: 3rem; text-align: center; color: #888; }
  `]
})
export class FinanceComponent implements OnInit {
  private http = inject(HttpClient);
  api = environment.apiUrl;
  stats: any = null;
  ngOnInit() {
    this.http.get<any>(`${this.api}/cotisations/stats`).subscribe({ next: s => this.stats = s });
  }
}

// ── Contenus Component ─────────────────────────────────────
@Component({
  selector: 'app-contenus',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>📖 Contenus religieux</h1><span class="subtitle">Khassaïdes, articles, audios, vidéos</span></div>
      </div>
      <div class="content-types">
        <div class="ct-card" *ngFor="let t of types" (click)="filtrer(t.val)" [class.active]="typeFilter === t.val">
          <div class="ct-icon">{{ t.icon }}</div>
          <div class="ct-label">{{ t.label }}</div>
        </div>
      </div>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div class="contenus-grid" *ngIf="!loading">
        <div class="contenu-card" *ngFor="let c of contenus">
          <div class="cc-header">
            <span class="cc-type">{{ c.type }}</span>
            <span class="cc-lang">{{ c.langue || 'fr' }}</span>
          </div>
          <h3>{{ c.titre }}</h3>
          <p *ngIf="c.auteur" class="cc-auteur">Par {{ c.auteur }}</p>
          <div class="cc-footer"><span>👁 {{ c.nbVues || 0 }} vues</span></div>
        </div>
        <div *ngIf="contenus.length === 0" class="empty">📖 Aucun contenu publié.</div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .content-types { display: flex; gap: 0.75rem; flex-wrap: wrap; margin-bottom: 1.5rem; }
    .ct-card { background: white; border-radius: 12px; padding: 0.75rem 1.25rem; cursor: pointer;
               box-shadow: 0 2px 8px rgba(0,0,0,0.07); display: flex; align-items: center; gap: 0.5rem;
               transition: all 0.15s; border: 2px solid transparent; }
    .ct-card.active, .ct-card:hover { border-color: #C8952A; background: #FDF3DC; }
    .ct-icon { font-size: 1.2rem; }
    .ct-label { font-size: 0.85rem; font-weight: 600; color: #333; }
    .contenus-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.25rem; }
    .contenu-card { background: white; border-radius: 14px; padding: 1.25rem; box-shadow: 0 2px 12px rgba(0,0,0,0.07); }
    .cc-header { display: flex; justify-content: space-between; margin-bottom: 0.5rem; }
    .cc-type { background: #FDF3DC; color: #8B6914; font-size: 0.72rem; font-weight: 700; padding: 0.15rem 0.5rem; border-radius: 4px; }
    .cc-lang { color: #888; font-size: 0.75rem; }
    h3 { font-size: 0.95rem; font-weight: 700; color: #1A4731; margin-bottom: 0.4rem; }
    .cc-auteur { font-size: 0.8rem; color: #C8952A; margin-bottom: 0.4rem; }
    .cc-footer { display: flex; gap: 1rem; margin-top: 0.75rem; font-size: 0.78rem; color: #aaa; }
    .loading, .empty { padding: 3rem; text-align: center; color: #888; }
  `]
})
export class ContenusListComponent implements OnInit {
  private http = inject(HttpClient);
  contenus: any[] = [];
  loading = true;
  typeFilter = '';
  types = [
    { val: '', icon: '📚', label: 'Tous' },
    { val: 'KHASSAIDE', icon: '🎵', label: 'Khassaïdes' },
    { val: 'ARTICLE', icon: '📝', label: 'Articles' },
    { val: 'AUDIO', icon: '🎧', label: 'Audios' },
    { val: 'VIDEO', icon: '🎬', label: 'Vidéos' },
    { val: 'PDF', icon: '📄', label: 'PDF' },
    { val: 'CITATION', icon: '💬', label: 'Citations' },
  ];
  ngOnInit() { this.filtrer(''); }
  filtrer(type: string) {
    this.typeFilter = type;
    this.loading = true;
    const url = `${environment.apiUrl}/contenus/public${type ? '?type=' + type : ''}`;
    this.http.get<any>(url).subscribe({
      next: d => { this.contenus = d.content || []; this.loading = false; },
      error: () => this.loading = false
    });
  }
}

// ── Projets Component ──────────────────────────────────────
@Component({
  selector: 'app-projets',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>🤝 Projets sociaux</h1><span class="subtitle">Actions humanitaires et communautaires</span></div>
      </div>
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div class="projets-grid" *ngIf="!loading">
        <div class="projet-card" *ngFor="let p of projets">
          <div class="pp-header"><span class="pp-statut">{{ p.statut }}</span></div>
          <h3>{{ p.titre }}</h3>
          <p class="pp-desc" *ngIf="p.description">{{ p.description }}</p>
          <div class="pp-progress" *ngIf="p.budgetCible > 0">
            <div class="progress-bar">
              <div class="progress-fill" [style.width.%]="(p.montantCollecte / p.budgetCible) * 100"></div>
            </div>
          </div>
        </div>
        <div *ngIf="projets.length === 0" class="empty">🤝 Aucun projet en cours.</div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .projets-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.25rem; }
    .projet-card { background: white; border-radius: 16px; padding: 1.5rem; box-shadow: 0 2px 16px rgba(0,0,0,0.08); border-top: 4px solid #1A4731; }
    .pp-statut { font-size: 0.72rem; font-weight: 700; padding: 0.2rem 0.6rem; border-radius: 4px; background: #e8f4ea; color: #1A4731; }
    h3 { font-size: 1rem; font-weight: 700; color: #1A4731; margin: 0.75rem 0 0.5rem; }
    .pp-desc { font-size: 0.85rem; color: #666; }
    .progress-bar { height: 8px; background: #eee; border-radius: 4px; overflow: hidden; margin-top: 1rem; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #1A4731, #C8952A); border-radius: 4px; }
    .loading, .empty { padding: 3rem; text-align: center; color: #888; }
  `]
})
export class ProjetsListComponent implements OnInit {
  private http = inject(HttpClient);
  projets: any[] = [];
  loading = true;
  ngOnInit() {
    this.http.get<any>(`${environment.apiUrl}/projets`).subscribe({
      next: d => { this.projets = d.content || []; this.loading = false; },
      error: () => this.loading = false
    });
  }
}

// ── Communication Component ────────────────────────────────
@Component({
  selector: 'app-communication',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>📢 Communication</h1><span class="subtitle">SMS, Email, WhatsApp, Annonces</span></div>
      </div>
      <div class="canaux-grid">
        <div class="canal-card" *ngFor="let c of canaux">
          <div class="canal-icon">{{ c.icon }}</div>
          <h3>{{ c.titre }}</h3>
          <p>{{ c.desc }}</p>
          <button class="btn-primary" (click)="ouvrir(c)">Envoyer</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .canaux-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 1.25rem; }
    .canal-card { background: white; border-radius: 16px; padding: 1.5rem; text-align: center; box-shadow: 0 2px 12px rgba(0,0,0,0.07); }
    .canal-icon { font-size: 2.5rem; margin-bottom: 0.75rem; }
    h3 { font-size: 1rem; font-weight: 700; color: #1A4731; margin-bottom: 0.5rem; }
    p { font-size: 0.85rem; color: #666; margin-bottom: 1rem; }
    .btn-primary { padding: 0.55rem 1.25rem; background: #1A4731; color: #F0C96B; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; }
  `]
})
export class CommunicationComponent {
  canaux = [
    { icon: '🔔', titre: 'Notification in-app', desc: 'Notifications en temps réel dans l\'application' },
    { icon: '📣', titre: 'Annonce globale', desc: 'Diffusion à tous les membres connectés' },
    { icon: '📆', titre: 'Rappel événement', desc: 'Rappels automatiques d\'événements' },
  ];
  ouvrir(c: any) { alert('Module ' + c.titre + ' — À configurer'); }
}

// ── Admin Component ────────────────────────────────────────
@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>⚙️ Administration</h1><span class="subtitle">Gestion système</span></div>
      </div>
      <div class="admin-sections">
        <div class="admin-card" *ngFor="let s of sections">
          <div class="admin-icon">{{ s.icon }}</div>
          <div class="admin-body"><h3>{{ s.titre }}</h3><p>{{ s.desc }}</p></div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .admin-sections { display: flex; flex-direction: column; gap: 1rem; }
    .admin-card { background: white; border-radius: 14px; padding: 1.25rem; box-shadow: 0 2px 12px rgba(0,0,0,0.07); display: flex; align-items: center; gap: 1rem; }
    .admin-icon { font-size: 2rem; width: 50px; text-align: center; flex-shrink: 0; }
    .admin-body { flex: 1; }
    h3 { font-size: 0.95rem; font-weight: 700; color: #1A4731; margin: 0 0 0.25rem; }
    p { font-size: 0.85rem; color: #888; margin: 0; }
  `]
})
export class AdminComponent {
  sections = [
    { icon: '👤', titre: 'Utilisateurs', desc: 'Créer et gérer les comptes utilisateurs' },
    { icon: '🔑', titre: 'Rôles', desc: 'Configurer les accès par rôle' },
    { icon: '📋', titre: 'Audit logs', desc: 'Traçabilité des actions' },
    { icon: '⚙️', titre: 'Configuration', desc: 'Paramètres système' },
  ];
}
