import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  
  const token = localStorage.getItem('token');
  
  // Проверяем, что токен существует и выглядит как JWT
  if (token && token.startsWith('eyJ') && !token.includes(' ') && token.length > 50) {
    return true;
  }
  
  router.navigate(['/login']);
  return false;
};