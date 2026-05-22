import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-dahiras',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>🕌 Dahiras</h1>
          <span class="subtitle">{{ dahiras.length }} Dahira(s) actif(s)</span>
        </div>
        <a routerLink="/dahiras/nouveau" class="btn-primary">➕ Nouveau Dahira</a>
      </div>

      <div *ngIf="loading" class="loading">Chargement...</div>

      <div class="dahiras-grid" *ngIf="!loading">
        <div class="dahira-card" *ngFor="let d of dahiras">
          <div class="card-header">
            <span class="card-icon">🕌</span>
            <span class="card-code" *ngIf="d.code">{{ d.code }}</span>
          </div>
          <h3>{{ d.nom }}</h3>
          <div class="card-meta">
            <span>📍 {{ d.ville ? d.ville + ', ' : '' }}{{ d.pays }}</span>
          </div>
          <p class="card-desc" *ngIf="d.description">{{ d.description | slice:0:100 }}</p>
          <div class="card-stats">
            <div class="cs">
              <span class="csn">{{ d.nombreMembres || 0 }}</span>
              <span class="csl">Membres</span>
            </div>
            <div class="cs">
              <span class="csn">{{ (d.totalCotisations || 0) | number:'1.0-0' }}</span>
              <span class="csl">FCFA</span>
            </div>
          </div>
          <div class="card-actions">
            <a [routerLink]="['/membres']" [queryParams]="{dahiraId: d.id}" class="btn-sm btn-view">
              👥 Membres
            </a>
            <a [routerLink]="['/dahiras', d.id, 'edit']" class="btn-sm btn-edit">✏️ Modifier</a>
            <button class="btn-sm btn-danger" (click)="supprimer(d)">🗑</button>
          </div>
        </div>

        <div class="empty-card" *ngIf="dahiras.length === 0">
          <div class="empty-icon">🕌</div>
          <p>Aucun Dahira créé.</p>
          <a routerLink="/dahiras/nouveau" class="btn-primary">Créer le premier Dahira</a>
        </div>
      </div>

      <div class="toast" [class.show]="toastMsg" [class.error]="toastError">
        {{ toastMsg }}
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start;
                   margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .btn-primary { padding: 0.6rem 1.25rem; background: #1A4731; color: #F0C96B;
                   border: none; border-radius: 8px; font-weight: 600; cursor: pointer;
                   text-decoration: none; font-size: 0.88rem; display: inline-block; }
    .dahiras-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.25rem; }
    .dahira-card { background: white; border-radius: 16px; padding: 1.5rem;
                   box-shadow: 0 2px 16px rgba(0,0,0,0.08); border-top: 4px solid #C8952A;
                   transition: transform 0.2s; }
    .dahira-card:hover { transform: translateY(-3px); }
    .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
    .card-icon { font-size: 1.5rem; }
    .card-code { background: #FDF3DC; color: #8B6914; font-size: 0.72rem; font-weight: 700; padding: 0.2rem 0.6rem; border-radius: 4px; }
    h3 { font-size: 1rem; font-weight: 700; color: #1A4731; margin-bottom: 0.4rem; }
    .card-meta { color: #888; font-size: 0.82rem; margin-bottom: 0.5rem; }
    .card-desc { font-size: 0.82rem; color: #666; margin-bottom: 1rem; font-style: italic; }
    .card-stats { display: flex; gap: 1.5rem; margin-bottom: 1rem; padding: 0.75rem; background: #F5F2EC; border-radius: 8px; }
    .cs { text-align: center; flex: 1; }
    .csn { display: block; font-size: 1.2rem; font-weight: 700; color: #1A4731; }
    .csl { display: block; font-size: 0.72rem; color: #888; }
    .card-actions { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .btn-sm { padding: 0.4rem 0.8rem; border-radius: 6px; font-size: 0.82rem; cursor: pointer; border: none; text-decoration: none; }
    .btn-view   { background: #e8f4ea; color: #1A4731; }
    .btn-edit   { background: #FDF3DC; color: #8B6914; }
    .btn-danger { background: #fdecea; color: #c0392b; }
    .btn-danger:hover { background: #c0392b; color: white; }
    .empty-card { background: white; border-radius: 16px; padding: 3rem; text-align: center; color: #aaa; box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    .empty-icon { font-size: 3rem; margin-bottom: 1rem; }
    .empty-card p { margin-bottom: 1rem; }
    .loading { padding: 3rem; text-align: center; color: #888; }
    .toast { position: fixed; bottom: 2rem; right: 2rem; background: #1A4731; color: white;
             padding: 0.85rem 1.5rem; border-radius: 10px; font-size: 0.88rem;
             transform: translateY(120%); transition: transform 0.3s; z-index: 300; }
    .toast.show  { transform: translateY(0); }
    .toast.error { background: #c0392b; }
  `]
})
export class DahirasListComponent implements OnInit {
  private http = inject(HttpClient);
  dahiras: any[] = [];
  loading = true;
  toastMsg = '';
  toastError = false;

  private get headers(): HttpHeaders {
    const token = localStorage.getItem('access_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    });
  }

  ngOnInit(): void { this.charger(); }

  charger(): void {
    this.loading = true;
    this.http.get<any[]>(`${environment.apiUrl}/dahiras`).subscribe({
      next: d => { this.dahiras = d; this.loading = false; },
      error: () => this.loading = false
    });
  }

  supprimer(d: any): void {
    if (!confirm(`Supprimer le Dahira "${d.nom}" ?`)) return;
    this.http.delete(`${environment.apiUrl}/dahiras/${d.id}`, { headers: this.headers }).subscribe({
      next: () => { this.showToast('✅ Dahira supprimé', false); this.charger(); },
      error: err => {
        const msg = err.status === 403 ? 'Non autorisé' : `Erreur ${err.status}`;
        this.showToast(`❌ ${msg}`, true);
      }
    });
  }

  showToast(msg: string, error = false): void {
    this.toastMsg = msg;
    this.toastError = error;
    setTimeout(() => { this.toastMsg = ''; this.toastError = false; }, 3000);
  }
}
