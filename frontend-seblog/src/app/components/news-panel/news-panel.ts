import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NewsPanelService } from '../../services/news-panel.service';
import { Post } from '../../models/post.model';
import { Comment } from '../../models/comment.model';

@Component({
  selector: 'app-news-panel',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './news-panel.html',
  styleUrl: './news-panel.scss'
})
export class NewsPanel implements OnInit {
  private newsPanelService = inject(NewsPanelService);

  popularPosts = signal<Post[]>([]);
  recentComments = signal<Comment[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.loadNewsPanelData();
  }

  loadNewsPanelData(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.newsPanelService.getNewsPanelData(5, 5).subscribe({
      next: (data) => {
        this.popularPosts.set(data.popularPosts);
        this.recentComments.set(data.recentComments);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading news panel data:', error);
        this.error.set('Не удалось загрузить данные');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Обрезка текста до нужной длины
   */
  truncateText(text: string, maxLength: number = 50): string {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }

  /**
   * Форматирование даты
   */
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffSeconds = Math.floor(diffMs / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffDays > 0) {
      return `${diffDays} ${this.declension(diffDays, 'день', 'дня', 'дней')} назад`;
    }
    if (diffHours > 0) {
      return `${diffHours} ${this.declension(diffHours, 'час', 'часа', 'часов')} назад`;
    }
    if (diffMinutes > 0) {
      return `${diffMinutes} ${this.declension(diffMinutes, 'минуту', 'минуты', 'минут')} назад`;
    }
    return 'Только что';
  }

  /**
   * Склонение слов по падежам
   */
  private declension(n: number, one: string, two: string, five: string): string {
    n = Math.abs(n) % 100;
    const n1 = n % 10;
    if (n > 10 && n < 20) return five;
    if (n1 > 1 && n1 < 5) return two;
    if (n1 === 1) return one;
    return five;
  }

  /**
 * Склонение слова "просмотр"
 */
  getViewText(count: number): string {
    const cases = [
      [1, 'просмотр'],
      [2, 'просмотра'],
      [4, 'просмотра'],
      [0, 'просмотров']
    ];
    
    const n = Math.abs(count) % 100;
    const n1 = n % 10;
    
    if (n > 10 && n < 20) return 'просмотров';
    if (n1 > 1 && n1 < 5) return 'просмотра';
    if (n1 === 1) return 'просмотр';
    return 'просмотров';
  }
}