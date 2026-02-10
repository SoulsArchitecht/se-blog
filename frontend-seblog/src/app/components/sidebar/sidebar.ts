import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar {
  authService = inject(AuthService);

  categories = [
    { name: 'Все посты', path: '/' },
    { name: 'Технологии', path: '/category/tech' },
    { name: 'Программирование', path: '/category/programming' },
    { name: 'Дизайн', path: '/category/design' },
    { name: 'Бизнес', path: '/category/business' }
  ];

    userMenu = [
    { name: 'Мои посты', path: '/my-posts', requiresAuth: true },
    { name: 'Избранное', path: '/favorites', requiresAuth: true },
    { name: 'Настройки', path: '/settings', requiresAuth: true }
  ];
}
