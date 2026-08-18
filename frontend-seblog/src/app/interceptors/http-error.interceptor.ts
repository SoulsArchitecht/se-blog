import { Injectable, inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
    const notificationService = inject(NotificationService);
    
    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.error && error.error.userMessage) {
                notificationService.show(error.error.userMessage, 'error');
            }

            else {
                notificationService.show('Произошла непредвиденная ошибка сети', 'error');
            }

            return throwError(() => error);
        })
    );
};