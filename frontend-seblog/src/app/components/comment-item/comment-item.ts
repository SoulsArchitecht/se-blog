import { Component, inject, input, output, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Comment } from '../../models/comment.model';
import { AuthService } from '../../services/auth.service';
import { CommentForm } from '../comment-form/comment-form';

@Component({
  selector: 'app-comment-item',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    CommentForm
  ],
  templateUrl: './comment-item.html',
  styleUrl: './comment-item.scss',
  host: {
    '[attr.depth]': 'depth()'
  }
})
export class CommentItem {
  private authService = inject(AuthService);

  comment = input.required<Comment>();
  postId = input.required<string>();
  depth = input<number>(0);
  isReply = input<boolean>(false);

  edit = output<{ id: string, content: string }>();
  delete = output<string>();

  isEditing = signal(false);
  showReplyForm = signal(false);

  avatarUrl = computed(() => {
    const avatar = this.comment().author.avatar;
    return avatar ? `/api/v1/uploads/${avatar}` : '/assets/default-avatar.png';
  });

  canEdit = computed(() => {
    const user = this.authService.user();
    if (!user) return false;
    if (['ROLE_ADMIN', 'ROLE_MODERATOR'].includes(user.role)) return true;
    return this.comment().author.id === user.id;
  });

  canDelete = computed(() => {
    const user = this.authService.user();
    if (!user) return false;
    if (user.role === 'ROLE_ADMIN') return true;
    if (user.role === 'ROLE_MODERATOR') return true;
    return this.comment().author.id === user.id;
  });

  toggleEdit(): void {
    if (!this.canEdit()) return;
    this.isEditing.update(v => !v);
    if (this.isEditing()) this.showReplyForm.set(false);
  }

  toggleReply(): void {
    if (!this.authService.isAuthenticated()) return;
    this.showReplyForm.update(v => !v);
    if (this.showReplyForm()) this.isEditing.set(false);
  }

  onEditSubmit(event: {content: string}): void {
    this.edit.emit({ id: this.comment().id, content: event.content });
    this.isEditing.set(false);
  }

  onReplySubmit(event: { content: string }): void {
    this.edit.emit({ id: this.comment().id, content: event.content });
    this.showReplyForm.set(false);
  }
}
