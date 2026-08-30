import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification.model';

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

  removeWithAnimation(notification: Notification): void {
    this.notificationService.remove(notification.id);
  }
}
