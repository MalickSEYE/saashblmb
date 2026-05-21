import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/services';
import { CanActivateFn, Router } from '@angular/router';

// ── Auth Interceptor ───────────────────────────────────────
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        authService.logout();
      }
      return throwError(() => err);
    })
  );
};

// ── Auth Guard ─────────────────────────────────────────────
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  return router.createUrlTree(['/auth/login']);
};

// ── Role Guard ─────────────────────────────────────────────
export const roleGuard = (roles: string[]): boolean => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.hasRole(roles)) return true;
  router.navigate(['/dashboard']);
  return false;
};
