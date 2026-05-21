import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/services';

@Component({
  selector: 'app-membres',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Membres</h1>
          <span class="subtitle">{{ totalElements }} membres au total</span>
        </div>
        <div class="header-actions">
          <button class="btn-export" (click)="export()">📥 Exporter</button>
          <a routerLink="/membres/nouveau" class="btn-primary">➕ Nouveau membre</a>
        </div>
      </div>

      <!-- Filtres -->
      <div class="filters">
        <input type="search" [(ngModel)]="search" (ngModelChange)="onSearch()"
               placeholder="🔍 Rechercher nom, email, téléphone..." class="search-input" />
        <select [(ngModel)]="statutFilter" (ngModelChange)="charger()">
          <option value="">Tous les statuts</option>
          <option value="ACTIF">Actif</option>
          <option value="INACTIF">Inactif</option>
          <option value="EN_ATTENTE">En attente</option>
          <option value="SUSPENDU">Suspendu</option>
        </select>
      </div>

      <!-- Tableau -->
      <div class="table-card">
        <div *ngIf="loading" class="loading">Chargement des membres...</div>
        <div *ngIf="!loading && membres.length === 0" class="empty">
          <p>🕌 Aucun membre trouvé</p>
        </div>
        <table *ngIf="!loading && membres.length > 0">
          <thead>
            <tr>
              <th>N° Membre</th>
              <th>Nom complet</th>
              <th>Email</th>
              <th>Téléphone</th>
              <th>Dahira</th>
              <th>Statut</th>
              <th>Adhésion</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let m of membres">
              <td class="mono">{{ m.numeroMembre }}</td>
              <td class="bold">{{ m.prenom }} {{ m.nom }}</td>
              <td class="muted">{{ m.email || '—' }}</td>
              <td class="muted">{{ m.telephone || '—' }}</td>
              <td class="muted">{{ m.dahiraNom || '—' }}</td>
              <td><span class="badge" [class]="'badge-' + m.statut">{{ labelStatut(m.statut) }}</span></td>
              <td class="muted">{{ m.dateAdhesion | date:'dd/MM/yyyy' }}</td>
              <td class="actions">
                <a [routerLink]="['/membres', m.id]" class="btn-sm btn-view">👁</a>
                <a [routerLink]="['/membres', m.id, 'edit']" class="btn-sm btn-edit">✏️</a>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination" *ngIf="totalPages > 1">
          <button (click)="goTo(currentPage - 1)" [disabled]="currentPage === 0">‹</button>
          <span>Page {{ currentPage + 1 }} / {{ totalPages }}</span>
          <button (click)="goTo(currentPage + 1)" [disabled]="currentPage >= totalPages - 1">›</button>
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
    .header-actions { display: flex; gap: 0.75rem; }
    .btn-primary { padding: 0.6rem 1.25rem; background: #1A4731; color: #F0C96B;
                   border-radius: 8px; text-decoration: none; font-weight: 600;
                   font-size: 0.88rem; }
    .btn-export  { padding: 0.6rem 1.25rem; background: #f5f2ec; color: #1A4731;
                   border: 1px solid #ddd; border-radius: 8px; cursor: pointer;
                   font-size: 0.88rem; }
    .filters { display: flex; gap: 1rem; margin-bottom: 1.25rem; flex-wrap: wrap; }
    .search-input { flex: 1; min-width: 220px; padding: 0.6rem 1rem;
                    border: 1.5px solid #e0e0e0; border-radius: 8px; font-size: 0.9rem;
                    outline: none; }
    .search-input:focus { border-color: #C8952A; }
    select { padding: 0.6rem 1rem; border: 1.5px solid #e0e0e0; border-radius: 8px;
             font-size: 0.88rem; outline: none; background: white; }
    .table-card { background: white; border-radius: 16px; overflow: hidden;
                  box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    table { width: 100%; border-collapse: collapse; }
    thead { background: #1A4731; }
    thead th { color: #F0C96B; padding: 0.9rem 1rem; text-align: left;
               font-size: 0.78rem; font-weight: 600; text-transform: uppercase;
               letter-spacing: 0.06em; white-space: nowrap; }
    tbody tr { border-bottom: 1px solid #f0ebe2; transition: background 0.15s; }
    tbody tr:hover { background: #fdf8f0; }
    tbody tr:last-child { border-bottom: none; }
    td { padding: 0.8rem 1rem; font-size: 0.88rem; vertical-align: middle; }
    .bold { font-weight: 600; color: #1C1712; }
    .muted { color: #666; }
    .mono  { font-family: monospace; font-size: 0.82rem; color: #888; }
    .badge { display: inline-block; padding: 0.2rem 0.65rem; border-radius: 999px;
             font-size: 0.73rem; font-weight: 600; }
    .badge-ACTIF       { background: #d4edda; color: #1A4731; }
    .badge-INACTIF     { background: #e9e9e9; color: #555; }
    .badge-EN_ATTENTE  { background: #fff3cd; color: #856404; }
    .badge-SUSPENDU    { background: #fdecea; color: #c0392b; }
    .actions { display: flex; gap: 0.4rem; }
    .btn-sm  { padding: 0.3rem 0.6rem; border-radius: 6px; text-decoration: none;
               font-size: 0.8rem; cursor: pointer; border: none; }
    .btn-view{ background: #e8f4ea; }
    .btn-edit{ background: #fdf3dc; }
    .loading { padding: 3rem; text-align: center; color: #888; }
    .empty   { padding: 3rem; text-align: center; color: #aaa; }
    .pagination { display: flex; align-items: center; justify-content: center;
                  gap: 1rem; padding: 1rem; color: #666; font-size: 0.88rem; }
    .pagination button { padding: 0.4rem 0.8rem; border: 1px solid #ddd;
                         border-radius: 6px; cursor: pointer; background: white; }
    .pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
  `]
})
export class MembresListComponent implements OnInit {
  private api = inject(ApiService);

  membres: any[] = [];
  loading = true;
  search = '';
  statutFilter = '';
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  searchTimeout: any;

  ngOnInit(): void { this.charger(); }

  charger(): void {
    this.loading = true;
    const params: any = { page: this.currentPage, size: 20, sort: 'nom' };
    if (this.search) params.search = this.search;
    if (this.statutFilter) params.statut = this.statutFilter;
    this.api.getMembres(params).subscribe({
      next: (data) => {
        this.membres = data.content || [];
        this.totalPages = data.totalPages || 0;
        this.totalElements = data.totalElements || 0;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onSearch(): void {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => { this.currentPage = 0; this.charger(); }, 400);
  }

  goTo(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.charger();
    }
  }

  export(): void {
    alert('Fonctionnalité d\'export — à brancher sur /api/v1/membres/export');
  }

  labelStatut(s: string): string {
    const map: any = { ACTIF: 'Actif', INACTIF: 'Inactif', EN_ATTENTE: 'En attente', SUSPENDU: 'Suspendu' };
    return map[s] || s;
  }
}
