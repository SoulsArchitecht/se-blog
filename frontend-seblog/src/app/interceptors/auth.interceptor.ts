import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  const token = authService.token();

  let authReq = req;
  if (token) {
    authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        if (!req.url.includes('/auth/login') && !req.url.includes('/auth/refresh')) {
          authService.clearSession();
          notificationService.show('Сессия истекла. Пожалуйста, войдите в систему снова.', 'warning', 500);
          router.navigate(['/login']);
        }
      }

      return throwError(() => error);
    })
  )
  
  // if (token) {
  //   const cloned = req.clone({
  //     headers: req.headers.set('Authorization', `Bearer ${token}`)
  //   });
  //   return next(cloned);
  // }
  
  // return next(req);
};