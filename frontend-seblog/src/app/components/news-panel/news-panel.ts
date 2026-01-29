import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-news-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './news-panel.html',
  styleUrl: './news-panel.scss'
})
export class NewsPanel {
  trendingPosts = [
    { title: 'Angular 20: Что нового', views: 1250 },
    { title: 'TypeScript 5.4 особенности', views: 980 },
    { title: 'Лучшие практики React 18', views: 750 },
    { title: 'Node.js обновления', views: 620 },
    { title: 'Веб-безопасность в 2024', views: 540 }
  ];
  
  recentComments = [
    { user: 'Алексей', post: 'Про оптимизацию', time: '2 часа назад' },
    { user: 'Мария', post: 'Новый фреймворк', time: '5 часов назад' },
    { user: 'Иван', post: 'Базы данных', time: '1 день назад' }
  ];
}
