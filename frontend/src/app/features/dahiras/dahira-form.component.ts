import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-dahira-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>{{ isEdit ? '✏️ Modifier le Dahira' : '➕ Nouveau Dahira' }}</h1>
          <span class="subtitle">{{ isEdit ? 'Modifier les informations du Dahira' : 'Créer un nouveau Dahira' }}</span>
        </div>
        <a routerLink="/dahiras" class="btn-ghost">← Retour</a>
      </div>

      <div class="form-card">
        <form [formGroup]="form" (ngSubmit)="sauvegarder()">

          <div class="section-title">Informations générales</div>
          <div class="form-grid">
            <div class="field full">
              <label>Nom du Dahira *</label>
              <input formControlName="nom" placeholder="Ex: Dahira Matlaboul Fawzaini Dakar" />
              <span class="err" *ngIf="hasError('nom')">Nom requis</span>
            </div>
            <div class="field">
              <label>Code (optionnel)</label>
              <input formControlName="code" placeholder="Ex: DK-MF-01" />
            </div>
            <div class="field">
              <label>Téléphone</label>
              <input formControlName="telephone" placeholder="+221 77 000 00 00" />
            </div>
            <div class="field">
              <label>Email</label>
              <input type="email" formControlName="email" placeholder="dahira@exemple.sn" />
            </div>
            <div class="field">
              <label>Date de création</label>
              <input type="date" formControlName="dateCreation" />
            </div>
          </div>

          <div class="section-title">Localisation</div>
          <div class="form-grid">
            <div class="field full">
              <label>Adresse</label>
              <input formControlName="adresse" placeholder="Adresse complète du Dahira" />
            </div>
            <div class="field">
              <label>Ville</label>
              <input formControlName="ville" placeholder="Dakar" />
            </div>
            <div class="field">
              <label>Pays</label>
              <input formControlName="pays" placeholder="Sénégal" />
            </div>
          </div>

          <div class="section-title">Description</div>
          <div class="field full">
            <label>Description</label>
            <textarea formControlName="description" rows="4"
                      placeholder="Décrivez les activités et la mission du Dahira..."></textarea>
          </div>

          <div class="alert-error"   *ngIf="errorMsg">{{ errorMsg }}</div>
          <div class="alert-success" *ngIf="successMsg">{{ successMsg }}</div>

          <div class="form-actions">
            <a routerLink="/dahiras" class="btn-ghost">Annuler</a>
            <button type="submit" class="btn-primary" [disabled]="saving || form.invalid">
              {{ saving ? '⏳...' : (isEdit ? '💾 Modifier' : '💾 Créer le Dahira') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 800px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start;
                   margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .btn-ghost { padding: 0.6rem 1.25rem; background: #f5f2ec; color: #333;
                 border: 1px solid #ddd; border-radius: 8px; text-decoration: none; font-size: 0.88rem; }
    .btn-primary { padding: 0.65rem 1.5rem; background: #1A4731; color: #F0C96B;
                   border: none; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 0.95rem; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .form-card { background: white; border-radius: 16px; padding: 2rem; box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    .section-title { font-size: 0.82rem; font-weight: 700; text-transform: uppercase;
                     letter-spacing: 0.08em; color: #1A4731; margin: 1.5rem 0 1rem;
                     padding-bottom: 0.5rem; border-bottom: 2px solid #FDF3DC; }
    .section-title:first-child { margin-top: 0; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: 0.3rem; }
    .field.full { grid-column: 1 / -1; }
    label { font-size: 0.8rem; font-weight: 600; color: #555; text-transform: uppercase; letter-spacing: 0.05em; }
    input, select, textarea { padding: 0.6rem 0.9rem; border: 1.5px solid #e0e0e0; border-radius: 8px;
                               font-size: 0.9rem; font-family: inherit; outline: none; transition: border-color 0.2s; }
    input:focus, textarea:focus { border-color: #C8952A; }
    textarea { resize: vertical; }
    .err { color: #e74c3c; font-size: 0.78rem; }
    .alert-error   { background: #fdecea; border: 1px solid #f5c6c2; color: #c0392b; border-radius: 8px; padding: 0.75rem 1rem; margin-top: 1rem; }
    .alert-success { background: #d4edda; border: 1px solid #c3e6cb; color: #1A4731; border-radius: 8px; padding: 0.75rem 1rem; margin-top: 1rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 2rem; padding-top: 1.5rem; border-top: 1px solid #f0ebe2; }
    @media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } }
  `]
})
export class DahiraFormComponent implements OnInit {
  private fb     = inject(FormBuilder);
  private http   = inject(HttpClient);
  private router = inject(Router);
  private route  = inject(ActivatedRoute);

  api = environment.apiUrl;
  isEdit = false;
  dahiraId: string | null = null;
  saving = false;
  errorMsg = '';
  successMsg = '';

  form = this.fb.group({
    nom:          ['', Validators.required],
    code:         [''],
    telephone:    [''],
    email:        [''],
    dateCreation: [''],
    adresse:      [''],
    ville:        [''],
    pays:         ['Sénégal'],
    description:  [''],
  });

  ngOnInit(): void {
    this.dahiraId = this.route.snapshot.paramMap.get('id');
    if (this.dahiraId && this.dahiraId !== 'nouveau') {
      this.isEdit = true;
      this.http.get<any>(`${this.api}/dahiras/${this.dahiraId}`).subscribe({
        next: d => this.form.patchValue(d),
        error: () => this.errorMsg = 'Dahira introuvable'
      });
    }
  }

  sauvegarder(): void {
    if (this.form.invalid) return;
    this.saving = true;
    this.errorMsg = '';
    const payload = { ...this.form.value };
    if (!payload.dateCreation) delete (payload as any).dateCreation;

    const req = this.isEdit
      ? this.http.put(`${this.api}/dahiras/${this.dahiraId}`, payload)
      : this.http.post(`${this.api}/dahiras`, payload);

    req.subscribe({
      next: () => {
        this.successMsg = this.isEdit ? '✅ Dahira modifié !' : '✅ Dahira créé avec succès !';
        setTimeout(() => this.router.navigate(['/dahiras']), 1500);
      },
      error: err => {
        this.errorMsg = err.error?.detail || err.error?.message || 'Erreur lors de l\'enregistrement';
        this.saving = false;
      }
    });
  }

  hasError(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }
}
