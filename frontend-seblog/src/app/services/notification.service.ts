import { Injectable, signal } from '@angular/core';
import { Notification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
    notifications = signal<Notification[]>([]);

    show(message: string, type: Notification['type'] = 'info', duration = 4000)  {
        const id = crypto.randomUUID();
        this.notifications.update(list => [...list, { id, message, type }]);
        setTimeout(() => this.remove(id), duration);
    }

    remove (id: string) {
        this.notifications.update(list => list.filter(n => n.id !== id));
    }
}