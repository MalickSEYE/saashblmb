import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  role: string;
  email: string;
  userId: string;
  expiresIn: number;
}

export interface User {
  email: string;
  role: string;
  userId: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private baseUrl = `${environment.apiUrl}/auth`;

  currentUser = signal<User | null>(this.getUserFromStorage());

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, { email, password })
      .pipe(tap(res => {
        localStorage.setItem('access_token', res.accessToken);
        localStorage.setItem('refresh_token', res.refreshToken);
        const user: User = { email: res.email, role: res.role, userId: res.userId };
        localStorage.setItem('current_user', JSON.stringify(user));
        this.currentUser.set(user);
      }));
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('current_user');
    this.currentUser.set(null);
    this.router.navigate(['/auth/login']);
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  hasRole(roles: string[]): boolean {
    const user = this.currentUser();
    return user ? roles.includes(user.role) : false;
  }

  private getUserFromStorage(): User | null {
    try {
      const stored = localStorage.getItem('current_user');
      return stored ? JSON.parse(stored) : null;
    } catch { return null; }
  }
}

// ── API Service ────────────────────────────────────────────
@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  // Dashboard
  getDashboardStats() { return this.http.get<any>(`${this.base}/dashboard/stats`); }

  // Membres
  getMembres(params?: any) { return this.http.get<any>(`${this.base}/membres`, { params }); }
  getMembre(id: string)    { return this.http.get<any>(`${this.base}/membres/${id}`); }
  createMembre(data: any)  { return this.http.post<any>(`${this.base}/membres`, data); }
  updateMembre(id: string, data: any) { return this.http.put<any>(`${this.base}/membres/${id}`, data); }
  deleteMembre(id: string) { return this.http.delete(`${this.base}/membres/${id}`); }
  getMembresStats()        { return this.http.get<any>(`${this.base}/membres/stats`); }

  // Dahiras
  getDahiras()             { return this.http.get<any[]>(`${this.base}/dahiras`); }
  getDahira(id: string)    { return this.http.get<any>(`${this.base}/dahiras/${id}`); }
  createDahira(data: any)  { return this.http.post<any>(`${this.base}/dahiras`, data); }
  updateDahira(id: string, data: any) { return this.http.put<any>(`${this.base}/dahiras/${id}`, data); }

  // Cotisations
  getCotisations(params?: any) { return this.http.get<any>(`${this.base}/cotisations`, { params }); }
  createCotisation(data: any)  { return this.http.post<any>(`${this.base}/cotisations`, data); }
  validerCotisation(id: string){ return this.http.put(`${this.base}/cotisations/${id}/valider`, {}); }
  rejeterCotisation(id: string, raison?: string) {
    return this.http.put(`${this.base}/cotisations/${id}/rejeter`, { raison });
  }
  getCotisationsStats() { return this.http.get<any>(`${this.base}/cotisations/stats`); }

  // Événements
  getEvenements(params?: any) { return this.http.get<any>(`${this.base}/evenements`, { params }); }
  getEvenement(id: string)    { return this.http.get<any>(`${this.base}/evenements/${id}`); }
  createEvenement(data: any)  { return this.http.post<any>(`${this.base}/evenements`, data); }
  updateEvenement(id: string, data: any) { return this.http.put<any>(`${this.base}/evenements/${id}`, data); }
}
