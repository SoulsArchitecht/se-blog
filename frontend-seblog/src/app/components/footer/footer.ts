import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './footer.html',
  styleUrl: './footer.scss'
})
export class Footer {
  currentYear = new Date().getFullYear();

  quickLinks = [
    { label: 'Главная', path: '/' },
    { label: 'Категории', path: '/categories' },
    { label: 'Популярное', path: '/trending' },
    { label: 'О блоге', path: '/about' }
  ];

  legalLinks = [
    { label: 'Политика конфиденциальности', path: '/privacy' },
    { label: 'Пользовательское соглашение', path: '/terms' },
    { label: 'Контакты', path: '/contact' }
  ];

  socialLinks = [
    { name: 'GitHub', url: 'https://github.com/yourname', icon: 'GH' },
    { name: 'Telegram', url: 'https://t.me/yourchannel', icon: 'TG' },
    { name: 'Email', url: 'mailto:sshibkodev@gmail.com', icon: '✉️' }
  ];
}