import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/services';

@Component({
  selector: 'app-evenements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>📅 Événements</h1><span class="subtitle">Magal, Gamou, Ziar, Réunions</span></div>
        <button class="btn-primary" (click)="showForm = !showForm">➕ Créer</button>
      </div>

      <!-- Formulaire -->
      <div class="form-card" *ngIf="showForm">
        <h3>Créer un événement</h3>
        <div class="form-grid">
          <div class="field full"><label>Titre *</label>
            <input [(ngModel)]="newEvent.titre" placeholder="Grand Magal de Touba 2024" /></div>
          <div class="field"><label>Type</label>
            <select [(ngModel)]="newEvent.type">
              <option value="MAGAL">Magal</option>
              <option value="GAMOU">Gamou</option>
              <option value="ZIAR">Ziar</option>
              <option value="CONFERENCE">Conférence religieuse</option>
              <option value="REUNION">Réunion Dahira</option>
              <option value="AUTRE">Autre</option>
            </select></div>
          <div class="field"><label>Lieu</label>
            <input [(ngModel)]="newEvent.lieu" placeholder="Touba, Sénégal" /></div>
          <div class="field"><label>Date début *</label>
            <input type="datetime-local" [(ngModel)]="newEvent.dateDebut" /></div>
          <div class="field"><label>Date fin</label>
            <input type="datetime-local" [(ngModel)]="newEvent.dateFin" /></div>
          <div class="field"><label>Capacité max</label>
            <input type="number" [(ngModel)]="newEvent.capaciteMax" placeholder="500" /></div>
          <div class="field full"><label>Description</label>
            <input [(ngModel)]="newEvent.description" placeholder="Description de l'événement..." /></div>
        </div>
        <div class="form-actions">
          <button class="btn-ghost" (click)="showForm = false">Annuler</button>
          <button class="btn-primary" (click)="creer()" [disabled]="!newEvent.titre || !newEvent.dateDebut">
            💾 Créer
          </button>
        </div>
      </div>

      <!-- Filtres -->
      <div class="filters-bar">
        <button *ngFor="let s of statuts" class="filter-btn"
                [class.active]="statutFilter === s.val"
                (click)="statutFilter = s.val; charger()">
          {{ s.label }}
        </button>
      </div>

      <!-- Grille -->
      <div *ngIf="loading" class="loading">Chargement...</div>
      <div class="events-grid" *ngIf="!loading">
        <div class="event-card" *ngFor="let e of evenements">
          <div class="event-header">
            <span class="event-type" [class]="'type-' + e.type">{{ labelType(e.type) }}</span>
            <span class="event-statut" [class]="'statut-' + e.statut">{{ labelStatut(e.statut) }}</span>
          </div>
          <h3>{{ e.titre }}</h3>
          <div class="event-meta">
            <span>📅 {{ e.dateDebut | date:'dd/MM/yyyy HH:mm' }}</span>
            <span *ngIf="e.lieu">📍 {{ e.lieu }}</span>
          </div>
          <div class="event-stats" *ngIf="e.nbInscrits !== undefined">
            <span>👥 {{ e.nbInscrits }} inscrits</span>
            <span *ngIf="e.capaciteMax">/ {{ e.capaciteMax }} places</span>
          </div>
          <div class="event-desc" *ngIf="e.description">{{ e.description | slice:0:100 }}...</div>
        </div>

        <div class="empty-state" *ngIf="evenements.length === 0">
          <p>📅 Aucun événement planifié.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .btn-primary { padding: 0.6rem 1.25rem; background: #1A4731; color: #F0C96B;
                   border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
    .btn-ghost { padding: 0.6rem 1.25rem; background: #eee; color: #333; border: none; border-radius: 8px; cursor: pointer; }
    .form-card { background: white; border-radius: 16px; padding: 1.5rem;
                 box-shadow: 0 2px 16px rgba(0,0,0,0.08); margin-bottom: 1.5rem; }
    .form-card h3 { font-size: 1rem; font-weight: 700; color: #1A4731;
                    margin-bottom: 1rem; border-bottom: 2px solid #FDF3DC; padding-bottom: 0.5rem; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: 0.3rem; }
    .field.full { grid-column: 1 / -1; }
    label { font-size: 0.78rem; font-weight: 600; color: #666; text-transform: uppercase; }
    input, select { padding: 0.55rem 0.85rem; border: 1.5px solid #e0e0e0; border-radius: 8px; font-size: 0.9rem; outline: none; }
    input:focus, select:focus { border-color: #C8952A; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1rem; }
    .filters-bar { display: flex; gap: 0.5rem; margin-bottom: 1.25rem; flex-wrap: wrap; }
    .filter-btn { padding: 0.45rem 1rem; border: 1.5px solid #ddd; border-radius: 999px;
                  background: white; cursor: pointer; font-size: 0.85rem; transition: all 0.15s; }
    .filter-btn.active { background: #1A4731; color: #F0C96B; border-color: #1A4731; }
    .events-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.25rem; }
    .event-card { background: white; border-radius: 16px; padding: 1.5rem;
                  box-shadow: 0 2px 16px rgba(0,0,0,0.08); border-left: 4px solid #C8952A; }
    .event-header { display: flex; justify-content: space-between; margin-bottom: 0.75rem; }
    .event-type { font-size: 0.72rem; font-weight: 700; padding: 0.2rem 0.6rem;
                  border-radius: 4px; background: #FDF3DC; color: #8B6914; }
    .event-statut { font-size: 0.72rem; font-weight: 700; padding: 0.2rem 0.6rem; border-radius: 4px; }
    .statut-PLANIFIE  { background: #e8f4ea; color: #1A4731; }
    .statut-EN_COURS  { background: #fff3cd; color: #856404; }
    .statut-TERMINE   { background: #e9e9e9; color: #555; }
    .statut-ANNULE    { background: #fdecea; color: #c0392b; }
    h3 { font-size: 1rem; font-weight: 700; color: #1A4731; margin-bottom: 0.5rem; }
    .event-meta { color: #888; font-size: 0.82rem; display: flex; flex-direction: column; gap: 0.2rem; margin-bottom: 0.5rem; }
    .event-stats { font-size: 0.82rem; color: #666; margin-bottom: 0.5rem; }
    .event-desc { font-size: 0.82rem; color: #999; font-style: italic; }
    .loading, .empty-state { padding: 3rem; text-align: center; color: #888; }
  `]
})
export class EvenementsListComponent implements OnInit {
  private api = inject(ApiService);
  evenements: any[] = [];
  loading = true;
  showForm = false;
  statutFilter = '';
  newEvent: any = { titre: '', type: 'REUNION', lieu: '', dateDebut: '', dateFin: '', capaciteMax: null, description: '' };
  statuts = [
    { val: '', label: 'Tous' },
    { val: 'PLANIFIE', label: '📋 Planifiés' },
    { val: 'EN_COURS', label: '🟢 En cours' },
    { val: 'TERMINE', label: '✅ Terminés' },
    { val: 'ANNULE', label: '❌ Annulés' },
  ];

  ngOnInit(): void { this.charger(); }

  charger(): void {
    this.loading = true;
    const params: any = { page: 0, size: 50 };
    if (this.statutFilter) params.statut = this.statutFilter;
    this.api.getEvenements(params).subscribe({
      next: d => { this.evenements = d.content || []; this.loading = false; },
      error: () => this.loading = false
    });
  }

  creer(): void {
    this.api.createEvenement(this.newEvent).subscribe({
      next: () => { this.showForm = false; this.newEvent = { titre: '', type: 'REUNION' }; this.charger(); },
      error: err => alert('Erreur: ' + (err.error?.detail || 'Création impossible'))
    });
  }

  labelType(t: string)   { return { MAGAL:'Magal', GAMOU:'Gamou', ZIAR:'Ziar', CONFERENCE:'Conférence', REUNION:'Réunion', AUTRE:'Autre' }[t] || t; }
  labelStatut(s: string) { return { PLANIFIE:'Planifié', EN_COURS:'En cours', TERMINE:'Terminé', ANNULE:'Annulé' }[s] || s; }
}
