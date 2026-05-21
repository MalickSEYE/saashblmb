import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-membre-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>{{ isEdit ? '✏️ Modifier le membre' : '➕ Nouveau membre' }}</h1>
          <span class="subtitle">{{ isEdit ? 'Modifier les informations' : 'Ajouter un nouveau talibé' }}</span>
        </div>
        <a routerLink="/membres" class="btn-ghost">← Retour à la liste</a>
      </div>

      <div class="form-card">
        <form [formGroup]="form" (ngSubmit)="sauvegarder()">

          <!-- Identité -->
          <div class="section-title">Identité</div>
          <div class="form-grid">
            <div class="field">
              <label>Prénom *</label>
              <input formControlName="prenom" placeholder="Amadou" />
              <span class="err" *ngIf="hasError('prenom')">Prénom requis</span>
            </div>
            <div class="field">
              <label>Nom *</label>
              <input formControlName="nom" placeholder="Diallo" />
              <span class="err" *ngIf="hasError('nom')">Nom requis</span>
            </div>
            <div class="field">
              <label>Email</label>
              <input type="email" formControlName="email" placeholder="amadou@exemple.sn" />
            </div>
            <div class="field">
              <label>Téléphone</label>
              <input formControlName="telephone" placeholder="+221 77 000 00 00" />
            </div>
            <div class="field">
              <label>Date de naissance</label>
              <input type="date" formControlName="dateNaissance" />
            </div>
            <div class="field">
              <label>Sexe</label>
              <select formControlName="sexe">
                <option value="">-- Choisir --</option>
                <option value="MASCULIN">Masculin</option>
                <option value="FEMININ">Féminin</option>
              </select>
            </div>
          </div>

          <!-- Localisation -->
          <div class="section-title">Localisation</div>
          <div class="form-grid">
            <div class="field full">
              <label>Adresse</label>
              <input formControlName="adresse" placeholder="Rue 10, Dakar" />
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

          <!-- Informations religieuses -->
          <div class="section-title">Informations religieuses</div>
          <div class="form-grid">
            <div class="field">
              <label>Dahira</label>
              <select formControlName="dahiraId">
                <option value="">-- Sélectionner un Dahira --</option>
                <option *ngFor="let d of dahiras" [value]="d.id">{{ d.nom }}</option>
              </select>
            </div>
            <div class="field">
              <label>Fonction religieuse</label>
              <input formControlName="fonctionReligieuse" placeholder="Ex: Imam, Cheikh..." />
            </div>
            <div class="field">
              <label>Profession</label>
              <input formControlName="profession" placeholder="Enseignant, Commerçant..." />
            </div>
            <div class="field">
              <label>Statut</label>
              <select formControlName="statut">
                <option value="EN_ATTENTE">En attente</option>
                <option value="ACTIF">Actif</option>
                <option value="INACTIF">Inactif</option>
                <option value="SUSPENDU">Suspendu</option>
              </select>
            </div>
          </div>

          <!-- Notes -->
          <div class="section-title">Notes</div>
          <div class="field full">
            <label>Observations</label>
            <textarea formControlName="notes" rows="3" placeholder="Notes complémentaires..."></textarea>
          </div>

          <!-- Alert -->
          <div class="alert-error" *ngIf="errorMsg">{{ errorMsg }}</div>
          <div class="alert-success" *ngIf="successMsg">{{ successMsg }}</div>

          <!-- Actions -->
          <div class="form-actions">
            <a routerLink="/membres" class="btn-ghost">Annuler</a>
            <button type="submit" class="btn-primary" [disabled]="saving || form.invalid">
              {{ saving ? '⏳ Enregistrement...' : (isEdit ? '💾 Modifier' : '💾 Créer le membre') }}
            </button>
          </div>

        </form>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; max-width: 900px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start;
                   margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .subtitle { color: #888; font-size: 0.9rem; }
    .btn-ghost { padding: 0.6rem 1.25rem; background: #f5f2ec; color: #333;
                 border: 1px solid #ddd; border-radius: 8px; text-decoration: none;
                 font-size: 0.88rem; cursor: pointer; }
    .btn-primary { padding: 0.65rem 1.5rem; background: #1A4731; color: #F0C96B;
                   border: none; border-radius: 8px; font-weight: 600; cursor: pointer;
                   font-size: 0.95rem; transition: background 0.2s; }
    .btn-primary:hover:not(:disabled) { background: #2D7A52; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .form-card { background: white; border-radius: 16px; padding: 2rem;
                 box-shadow: 0 2px 16px rgba(0,0,0,0.08); }
    .section-title { font-size: 0.82rem; font-weight: 700; text-transform: uppercase;
                     letter-spacing: 0.08em; color: #1A4731; margin: 1.5rem 0 1rem;
                     padding-bottom: 0.5rem; border-bottom: 2px solid #FDF3DC; }
    .section-title:first-child { margin-top: 0; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: 0.3rem; }
    .field.full { grid-column: 1 / -1; }
    label { font-size: 0.8rem; font-weight: 600; color: #555; text-transform: uppercase; letter-spacing: 0.05em; }
    input, select, textarea {
      padding: 0.6rem 0.9rem; border: 1.5px solid #e0e0e0; border-radius: 8px;
      font-size: 0.9rem; font-family: inherit; outline: none;
      transition: border-color 0.2s; background: white; color: #1C1712;
    }
    input:focus, select:focus, textarea:focus { border-color: #C8952A; }
    textarea { resize: vertical; }
    .err { color: #e74c3c; font-size: 0.78rem; }
    .alert-error   { background: #fdecea; border: 1px solid #f5c6c2; color: #c0392b;
                     border-radius: 8px; padding: 0.75rem 1rem; margin-top: 1rem; font-size: 0.88rem; }
    .alert-success { background: #d4edda; border: 1px solid #c3e6cb; color: #1A4731;
                     border-radius: 8px; padding: 0.75rem 1rem; margin-top: 1rem; font-size: 0.88rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 2rem;
                    padding-top: 1.5rem; border-top: 1px solid #f0ebe2; }
    @media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } }
  `]
})
export class MembreFormComponent implements OnInit {
  private fb     = inject(FormBuilder);
  private http   = inject(HttpClient);
  private router = inject(Router);
  private route  = inject(ActivatedRoute);

  api = environment.apiUrl;
  isEdit = false;
  membreId: string | null = null;
  saving = false;
  errorMsg = '';
  successMsg = '';
  dahiras: any[] = [];

  form = this.fb.group({
    prenom:             ['', Validators.required],
    nom:                ['', Validators.required],
    email:              [''],
    telephone:          [''],
    dateNaissance:      [''],
    sexe:               [''],
    adresse:            [''],
    ville:              [''],
    pays:               ['Sénégal'],
    dahiraId:           [''],
    fonctionReligieuse: [''],
    profession:         [''],
    statut:             ['EN_ATTENTE'],
    notes:              [''],
  });

  ngOnInit(): void {
    // Charger les Dahiras pour le select
    this.http.get<any[]>(`${this.api}/dahiras`).subscribe({
      next: d => this.dahiras = d,
      error: () => {}
    });

    // Mode édition si l'URL contient un ID
    this.membreId = this.route.snapshot.paramMap.get('id');
    if (this.membreId && this.membreId !== 'nouveau') {
      this.isEdit = true;
      this.chargerMembre(this.membreId);
    }
  }

  chargerMembre(id: string): void {
    this.http.get<any>(`${this.api}/membres/${id}`).subscribe({
      next: m => {
        this.form.patchValue({
          prenom: m.prenom, nom: m.nom, email: m.email,
          telephone: m.telephone, dateNaissance: m.dateNaissance,
          sexe: m.sexe, adresse: m.adresse, ville: m.ville,
          pays: m.pays, dahiraId: m.dahiraId,
          fonctionReligieuse: m.fonctionReligieuse,
          profession: m.profession, statut: m.statut, notes: m.notes
        });
      },
      error: () => this.errorMsg = 'Membre introuvable'
    });
  }

  sauvegarder(): void {
    if (this.form.invalid) return;
    this.saving = true;
    this.errorMsg = '';

    const payload = { ...this.form.value };
    // Nettoyer les champs vides
    if (!payload.dahiraId) delete (payload as any).dahiraId;
    if (!payload.dateNaissance) delete (payload as any).dateNaissance;
    if (!payload.sexe) delete (payload as any).sexe;

    const req = this.isEdit
      ? this.http.put(`${this.api}/membres/${this.membreId}`, payload)
      : this.http.post(`${this.api}/membres`, payload);

    req.subscribe({
      next: () => {
        this.successMsg = this.isEdit ? '✅ Membre modifié !' : '✅ Membre créé avec succès !';
        setTimeout(() => this.router.navigate(['/membres']), 1500);
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
