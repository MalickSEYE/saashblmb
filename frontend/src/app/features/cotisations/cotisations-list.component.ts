import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/services';

@Component({
  selector: 'app-cotisations',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div><h1>💳 Cotisations</h1><span class="subtitle">Suivi des paiements</span></div>
        <button class="btn-primary" (click)="showForm = !showForm">➕ Enregistrer</button>
      </div>

      <!-- Stats financières -->
      <div class="stats-row" *ngIf="stats">
        <div class="stat-card green">
          <div class="sv">{{ (stats.totalValide | number:'1.0-0') }} FCFA</div>
          <div class="sl">Total validé</div>
        </div>
        <div class="stat-card blue">
          <div class="sv">{{ (stats.totalCeMois | number:'1.0-0') }} FCFA</div>
          <div class="sl">Ce mois</div>
        </div>
        <div class="stat-card orange">
          <div class="sv">{{ stats.nbEnAttente }}</div>
          <div class="sl">En attente</div>
        </div>
        <div class="stat-card gray">
          <div class="sv">{{ stats.nbRejete }}</div>
          <div class="sl">Rejetées</div>
        </div>
      </div>

      <!-- Formulaire enregistrement -->
      <div class="form-card" *ngIf="showForm">
        <h3>Enregistrer une cotisation</h3>
        <form [formGroup]="form" (ngSubmit)="enregistrer()">
          <div class="form-grid">
            <div class="field"><label>Membre *</label>
              <input formControlName="membreId" placeholder="UUID du membre" /></div>
            <div class="field"><label>Montant (FCFA) *</label>
              <input type="number" formControlName="montant" placeholder="5000" /></div>
            <div class="field"><label>Type</label>
              <select formControlName="type">
                <option value="MENSUELLE">Mensuelle</option>
                <option value="ANNUELLE">Annuelle</option>
                <option value="SPECIALE">Spéciale</option>
                <option value="DON">Don</option>
              </select></div>
            <div class="field"><label>Période (YYYY-MM)</label>
              <input formControlName="periode" placeholder="2024-05" /></div>
            <div class="field"><label>Moyen de paiement</label>
              <select formControlName="moyenPaiement">
                <option value="WAVE">Wave</option>
                <option value="ORANGE_MONEY">Orange Money</option>
                <option value="FREE_MONEY">Free Money</option>
                <option value="CARTE">Carte bancaire</option>
                <option value="MANUEL">Manuel / Espèces</option>
              </select></div>
            <div class="field"><label>Référence paiement</label>
              <input formControlName="referencePaiement" placeholder="WV-XXXX..." /></div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-ghost" (click)="showForm = false">Annuler</button>
            <button type="submit" class="btn-primary" [disabled]="form.invalid || saving">
              {{ saving ? '⏳ Enregistrement...' : '💾 Enregistrer' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Tableau -->
      <div class="table-card">
        <div class="filters">
          <select [(ngModel)]="statutFilter" (ngModelChange)="charger()">
            <option value="">Tous les statuts</option>
            <option value="EN_ATTENTE">En attente</option>
            <option value="VALIDEE">Validées</option>
            <option value="REJETEE">Rejetées</option>
          </select>
        </div>

        <div *ngIf="loading" class="loading">Chargement...</div>
        <table *ngIf="!loading && cotisations.length > 0">
          <thead>
            <tr>
              <th>Membre</th>
              <th>Montant</th>
              <th>Type</th>
              <th>Période</th>
              <th>Moyen</th>
              <th>Statut</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let c of cotisations">
              <td class="bold">{{ c.membreNomComplet || c.membreId }}</td>
              <td class="bold green">{{ c.montant | number:'1.0-0' }} FCFA</td>
              <td class="muted">{{ c.type }}</td>
              <td class="muted mono">{{ c.periode || '—' }}</td>
              <td><span class="badge-pay" [class]="'pay-' + c.moyenPaiement">{{ c.moyenPaiement }}</span></td>
              <td><span class="badge" [class]="'badge-' + c.statut">{{ labelStatut(c.statut) }}</span></td>
              <td class="muted">{{ c.datePaiement | date:'dd/MM/yyyy HH:mm' }}</td>
              <td class="actions">
                <button *ngIf="c.statut === 'EN_ATTENTE'" class="btn-sm btn-ok" (click)="valider(c.id)">✅</button>
                <button *ngIf="c.statut === 'EN_ATTENTE'" class="btn-sm btn-ko" (click)="rejeter(c.id)">❌</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div *ngIf="!loading && cotisations.length === 0" class="empty">Aucune cotisation trouvée.</div>
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
    .btn-ghost { padding: 0.6rem 1.25rem; background: #eee; color: #333;
                 border: none; border-radius: 8px; cursor: pointer; }
    .stats-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
                 gap: 1rem; margin-bottom: 1.5rem; }
    .stat-card { background: white; border-radius: 12px; padding: 1.25rem;
                 box-shadow: 0 2px 12px rgba(0,0,0,0.07); border-top: 4px solid #ddd; }
    .stat-card.green  { border-top-color: #1A4731; }
    .stat-card.blue   { border-top-color: #2196F3; }
    .stat-card.orange { border-top-color: #C8952A; }
    .stat-card.gray   { border-top-color: #999; }
    .sv { font-size: 1.4rem; font-weight: 700; color: #1C1712; }
    .sl { font-size: 0.78rem; color: #888; margin-top: 0.25rem; }
    .form-card { background: white; border-radius: 16px; padding: 1.5rem;
                 box-shadow: 0 2px 16px rgba(0,0,0,0.08); margin-bottom: 1.5rem; }
    .form-card h3 { font-size: 1rem; font-weight: 700; color: #1A4731;
                    margin-bottom: 1rem; border-bottom: 2px solid #FDF3DC; padding-bottom: 0.5rem; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: 0.3rem; }
    label { font-size: 0.78rem; font-weight: 600; color: #666; text-transform: uppercase; }
    input, select { padding: 0.55rem 0.85rem; border: 1.5px solid #e0e0e0; border-radius: 8px;
                    font-size: 0.9rem; outline: none; background: white; }
    input:focus, select:focus { border-color: #C8952A; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1rem; }
    .filters { padding: 1rem; border-bottom: 1px solid #f0ebe2; }
    .table-card { background: white; border-radius: 16px; overflow: hidden;
                  box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    table { width: 100%; border-collapse: collapse; }
    thead { background: #1A4731; }
    thead th { color: #F0C96B; padding: 0.9rem 1rem; text-align: left;
               font-size: 0.78rem; font-weight: 600; text-transform: uppercase; white-space: nowrap; }
    tbody tr { border-bottom: 1px solid #f0ebe2; }
    tbody tr:hover { background: #fdf8f0; }
    td { padding: 0.75rem 1rem; font-size: 0.88rem; vertical-align: middle; }
    .bold { font-weight: 600; }
    .green { color: #1A4731; }
    .muted { color: #666; }
    .mono  { font-family: monospace; }
    .badge { display: inline-block; padding: 0.2rem 0.6rem; border-radius: 999px;
             font-size: 0.73rem; font-weight: 600; }
    .badge-EN_ATTENTE { background: #fff3cd; color: #856404; }
    .badge-VALIDEE    { background: #d4edda; color: #1A4731; }
    .badge-REJETEE    { background: #fdecea; color: #c0392b; }
    .badge-pay { font-size: 0.72rem; padding: 0.15rem 0.5rem; border-radius: 4px;
                 background: #e8f4ea; color: #1A4731; font-weight: 600; }
    .actions { display: flex; gap: 0.4rem; }
    .btn-sm { padding: 0.3rem 0.6rem; border: none; border-radius: 6px; cursor: pointer; }
    .btn-ok { background: #d4edda; }
    .btn-ko { background: #fdecea; }
    .loading, .empty { padding: 3rem; text-align: center; color: #888; }
  `]
})
export class CotisationsListComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);

  cotisations: any[] = [];
  stats: any = null;
  loading = true;
  saving = false;
  showForm = false;
  statutFilter = '';

  form = this.fb.group({
    membreId:          ['', Validators.required],
    montant:           [null, [Validators.required, Validators.min(1)]],
    type:              ['MENSUELLE'],
    periode:           [''],
    moyenPaiement:     ['MANUEL'],
    referencePaiement: ['']
  });

  ngOnInit(): void {
    this.charger();
    this.api.getCotisationsStats().subscribe({ next: s => this.stats = s });
  }

  charger(): void {
    this.loading = true;
    const params: any = { page: 0, size: 50, sort: 'datePaiement,desc' };
    if (this.statutFilter) params.statut = this.statutFilter;
    this.api.getCotisations(params).subscribe({
      next: d => { this.cotisations = d.content || []; this.loading = false; },
      error: () => this.loading = false
    });
  }

  enregistrer(): void {
    if (this.form.invalid) return;
    this.saving = true;
    this.api.createCotisation(this.form.value).subscribe({
      next: () => {
        this.showForm = false; this.form.reset({ type: 'MENSUELLE', moyenPaiement: 'MANUEL' });
        this.saving = false; this.charger();
        this.api.getCotisationsStats().subscribe({ next: s => this.stats = s });
      },
      error: () => this.saving = false
    });
  }

  valider(id: string): void {
    if (confirm('Valider ce paiement ?')) {
      this.api.validerCotisation(id).subscribe({ next: () => this.charger() });
    }
  }

  rejeter(id: string): void {
    const raison = prompt('Raison du rejet :');
    this.api.rejeterCotisation(id, raison || '').subscribe({ next: () => this.charger() });
  }

  labelStatut(s: string): string {
    return { EN_ATTENTE: 'En attente', VALIDEE: 'Validée', REJETEE: 'Rejetée' }[s] || s;
  }
}
