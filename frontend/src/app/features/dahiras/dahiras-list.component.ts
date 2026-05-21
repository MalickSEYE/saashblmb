import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/services';

@Component({
  selector: 'app-dahiras',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>🕌 Dahiras</h1><span class="subtitle">{{ dahiras.length }} dahira(s) actif(s)</span></div>
        <button class="btn-primary" (click)="showForm = !showForm">➕ Nouveau Dahira</button>
      </div>

      <!-- Formulaire création rapide -->
      <div class="form-card" *ngIf="showForm">
        <h3>Créer un Dahira</h3>
        <div class="form-grid">
          <div class="field"><label>Nom *</label>
            <input [(ngModel)]="newDahira.nom" placeholder="Ex: Dahira Matlaboul Fawzaini" /></div>
          <div class="field"><label>Ville</label>
            <input [(ngModel)]="newDahira.ville" placeholder="Dakar" /></div>
          <div class="field"><label>Pays</label>
            <input [(ngModel)]="newDahira.pays" placeholder="Sénégal" /></div>
          <div class="field"><label>Téléphone</label>
            <input [(ngModel)]="newDahira.telephone" placeholder="+221 77..." /></div>
          <div class="field full"><label>Description</label>
            <input [(ngModel)]="newDahira.description" placeholder="Description du Dahira" /></div>
        </div>
        <div class="form-actions">
          <button class="btn-ghost" (click)="showForm = false">Annuler</button>
          <button class="btn-primary" (click)="creer()" [disabled]="!newDahira.nom">💾 Créer</button>
        </div>
      </div>

      <!-- Grille des Dahiras -->
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div class="dahiras-grid" *ngIf="!loading">
        <div class="dahira-card" *ngFor="let d of dahiras">
          <div class="card-header">
            <span class="card-icon">🕌</span>
            <span class="card-code">{{ d.code }}</span>
          </div>
          <h3>{{ d.nom }}</h3>
          <div class="card-meta">
            <span>📍 {{ d.ville || '—' }}, {{ d.pays }}</span>
          </div>
          <div class="card-stats">
            <div class="cs"><span class="csn">{{ d.nombreMembres }}</span><span class="csl">Membres</span></div>
            <div class="cs"><span class="csn">{{ (d.totalCotisations | number:'1.0-0') || '0' }}</span><span class="csl">FCFA</span></div>
          </div>
          <div class="card-actions">
            <a [routerLink]="['/membres']" [queryParams]="{dahiraId: d.id}" class="btn-sm btn-view">
              👥 Membres
            </a>
            <button class="btn-sm btn-edit" (click)="selectionner(d)">✏️ Modifier</button>
          </div>
        </div>

        <!-- Carte vide si aucun dahira -->
        <div class="empty-card" *ngIf="dahiras.length === 0">
          <div class="empty-icon">🕌</div>
          <p>Aucun Dahira créé.</p>
          <button class="btn-primary" (click)="showForm = true">Créer le premier Dahira</button>
        </div>
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
                   border: none; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 0.88rem; }
    .btn-ghost { padding: 0.6rem 1.25rem; background: #eee; color: #333;
                 border: none; border-radius: 8px; cursor: pointer; }
    .form-card { background: white; border-radius: 16px; padding: 1.5rem;
                 box-shadow: 0 2px 16px rgba(0,0,0,0.08); margin-bottom: 1.5rem; }
    .form-card h3 { font-size: 1rem; font-weight: 700; color: #1A4731;
                    margin-bottom: 1rem; border-bottom: 2px solid #FDF3DC; padding-bottom: 0.5rem; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: 0.3rem; }
    .field.full { grid-column: 1 / -1; }
    label { font-size: 0.78rem; font-weight: 600; color: #666; text-transform: uppercase; }
    input { padding: 0.55rem 0.85rem; border: 1.5px solid #e0e0e0; border-radius: 8px;
            font-size: 0.9rem; outline: none; }
    input:focus { border-color: #C8952A; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1rem; }
    .dahiras-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1.25rem; }
    .dahira-card { background: white; border-radius: 16px; padding: 1.5rem;
                   box-shadow: 0 2px 16px rgba(0,0,0,0.08); border-top: 4px solid #C8952A;
                   transition: transform 0.2s, box-shadow 0.2s; }
    .dahira-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
    .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
    .card-icon { font-size: 1.5rem; }
    .card-code { background: #FDF3DC; color: #8B6914; font-size: 0.72rem;
                 font-weight: 700; padding: 0.2rem 0.6rem; border-radius: 4px; }
    h3 { font-size: 1rem; font-weight: 700; color: #1A4731; margin-bottom: 0.5rem; }
    .card-meta { color: #888; font-size: 0.82rem; margin-bottom: 1rem; }
    .card-stats { display: flex; gap: 1.5rem; margin-bottom: 1rem; padding: 0.75rem;
                  background: #F5F2EC; border-radius: 8px; }
    .cs { text-align: center; flex: 1; }
    .csn { display: block; font-size: 1.2rem; font-weight: 700; color: #1A4731; }
    .csl { display: block; font-size: 0.72rem; color: #888; }
    .card-actions { display: flex; gap: 0.5rem; }
    .btn-sm { padding: 0.4rem 0.8rem; border-radius: 6px; font-size: 0.82rem;
              cursor: pointer; border: none; text-decoration: none; }
    .btn-view { background: #e8f4ea; color: #1A4731; }
    .btn-edit { background: #FDF3DC; color: #8B6914; }
    .loading { padding: 3rem; text-align: center; color: #888; }
    .empty-card { background: white; border-radius: 16px; padding: 3rem;
                  text-align: center; color: #aaa; box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    .empty-icon { font-size: 3rem; margin-bottom: 1rem; }
  `]
})
export class DahirasListComponent implements OnInit {
  private api = inject(ApiService);
  dahiras: any[] = [];
  loading = true;
  showForm = false;
  newDahira: any = { nom: '', ville: '', pays: 'Sénégal', telephone: '', description: '' };

  ngOnInit(): void { this.charger(); }

  charger(): void {
    this.loading = true;
    this.api.getDahiras().subscribe({
      next: d => { this.dahiras = d; this.loading = false; },
      error: () => this.loading = false
    });
  }

  creer(): void {
    this.api.createDahira(this.newDahira).subscribe({
      next: () => { this.showForm = false; this.newDahira = { nom:'',ville:'',pays:'Sénégal' }; this.charger(); },
      error: err => alert('Erreur: ' + (err.error?.detail || 'Création impossible'))
    });
  }

  selectionner(d: any): void { alert('Modifier le Dahira "' + d.nom + '" — à implémenter'); }
}
