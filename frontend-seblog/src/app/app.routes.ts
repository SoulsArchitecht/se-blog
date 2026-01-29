import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home')
      .then(m => m.HomeComponent),
    title: 'Главная'
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login')
      .then(m => m.LoginComponent),
    title: 'Вход'
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register')
      .then(m => m.RegisterComponent),
    title: 'Регистрация'
  },
  {
    path: 'create-post',
    loadComponent: () => import('./pages/post-create/post-create')
      .then(m => m.PostCreateComponent),
    canActivate: [authGuard],
    title: 'Создать пост'
  },
  {
    path: 'post/:id',
    loadComponent: () => import('./pages/post-detail/post-detail')
      .then(m => m.PostDetailComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/user-profile/user-profile')
      .then(m => m.ProfileComponent),
    canActivate: [authGuard],
    title: 'Профиль'
  }
];