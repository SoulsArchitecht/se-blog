import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-toast-containter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-containter.html',
  styleUrl: './toast-containter.scss'
})
export class ToastContainter {
  notificationService = inject(NotificationService);

  getIcon(type: string): string {
    switch(type) {
      case 'success': return '✅';
      case 'error': return '❌';
      case 'warning': return '⚠️';
      default: return 'ℹ️';
    }
  }
}
