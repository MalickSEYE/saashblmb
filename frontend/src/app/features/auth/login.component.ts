import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/services';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-page">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-symbol">☪</div>
          <h1>Mouride SaaS</h1>
          <p>Plateforme de gestion religieuse</p>
        </div>

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="login-form">
          <div class="field">
            <label>Adresse email</label>
            <input type="email" formControlName="email" placeholder="admin@mouride.sn"
                   [class.error]="form.get('email')?.invalid && form.get('email')?.touched" />
            <span class="err" *ngIf="form.get('email')?.errors?.['required'] && form.get('email')?.touched">
              Email requis
            </span>
          </div>

          <div class="field">
            <label>Mot de passe</label>
            <input [type]="showPwd ? 'text' : 'password'" formControlName="password"
                   placeholder="••••••••"
                   [class.error]="form.get('password')?.invalid && form.get('password')?.touched" />
            <button type="button" class="toggle-pwd" (click)="showPwd = !showPwd">
              {{ showPwd ? '🙈' : '👁' }}
            </button>
          </div>

          <div class="login-options">
            <label class="remember">
              <input type="checkbox" /> Se souvenir de moi
            </label>
            <a routerLink="/auth/forgot-password">Mot de passe oublié ?</a>
          </div>

          <div class="alert-error" *ngIf="errorMessage">
            {{ errorMessage }}
          </div>

          <button type="submit" class="btn-login" [disabled]="loading || form.invalid">
            <span *ngIf="!loading">Se connecter</span>
            <span *ngIf="loading" class="spinner">⏳ Connexion...</span>
          </button>

          <p class="register-link">
            Pas encore de compte ? <a routerLink="/auth/register">S'inscrire</a>
          </p>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-page {
      min-height: 100vh;
      background: linear-gradient(135deg, #1A4731 0%, #2D7A52 50%, #1A4731 100%);
      display: flex; align-items: center; justify-content: center;
      padding: 1rem;
    }
    .login-card {
      background: #fff; border-radius: 20px; padding: 2.5rem;
      width: 100%; max-width: 420px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
    }
    .login-header { text-align: center; margin-bottom: 2rem; }
    .logo-symbol { font-size: 3rem; margin-bottom: 0.5rem; }
    .login-header h1 { font-size: 1.6rem; font-weight: 700; color: #1A4731; margin: 0; }
    .login-header p  { color: #888; font-size: 0.9rem; margin-top: 0.25rem; }
    .field { margin-bottom: 1.25rem; position: relative; }
    .field label { display: block; font-size: 0.8rem; font-weight: 600; color: #555;
                   text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 0.4rem; }
    .field input { width: 100%; padding: 0.65rem 1rem; border: 1.5px solid #e0e0e0;
                   border-radius: 10px; font-size: 0.95rem; outline: none;
                   transition: border-color 0.2s; box-sizing: border-box; }
    .field input:focus { border-color: #C8952A; }
    .field input.error { border-color: #e74c3c; }
    .toggle-pwd { position: absolute; right: 0.75rem; top: 2.1rem; background: none;
                  border: none; cursor: pointer; font-size: 1rem; }
    .err { color: #e74c3c; font-size: 0.78rem; margin-top: 0.25rem; display: block; }
    .login-options { display: flex; justify-content: space-between; align-items: center;
                     font-size: 0.85rem; margin-bottom: 1rem; }
    .remember { display: flex; align-items: center; gap: 0.4rem; color: #666; }
    .login-options a { color: #C8952A; text-decoration: none; }
    .login-options a:hover { text-decoration: underline; }
    .alert-error { background: #fdecea; border: 1px solid #f5c6c2; color: #c0392b;
                   border-radius: 8px; padding: 0.75rem 1rem; font-size: 0.88rem;
                   margin-bottom: 1rem; }
    .btn-login { width: 100%; padding: 0.85rem; background: #1A4731; color: #F0C96B;
                 font-size: 1rem; font-weight: 600; border: none; border-radius: 10px;
                 cursor: pointer; transition: background 0.2s; }
    .btn-login:hover:not(:disabled) { background: #2D7A52; }
    .btn-login:disabled { opacity: 0.6; cursor: not-allowed; }
    .register-link { text-align: center; font-size: 0.88rem; color: #888; margin-top: 1rem; }
    .register-link a { color: #C8952A; text-decoration: none; font-weight: 600; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  loading = false;
  showPwd = false;
  errorMessage = '';

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';
    const { email, password } = this.form.value;
    this.authService.login(email!, password!).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur de connexion. Vérifiez vos identifiants.';
        this.loading = false;
      }
    });
  }
}
