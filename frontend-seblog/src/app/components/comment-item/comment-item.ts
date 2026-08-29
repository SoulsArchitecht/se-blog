import { Component, inject, input, output, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Comment, CommentVoteStats } from '../../models/comment.model';
import { AuthService } from '../../services/auth.service';
import { CommentForm } from '../comment-form/comment-form';
import { VoteButtons } from '../vote-buttons/vote-buttons';
import { CommentVoteService } from '../../services/comment-vote.service';
import { VoteStats } from '../../models/vote.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-comment-item',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    CommentForm,
    VoteButtons,
  ],
  templateUrl: './comment-item.html',
  styleUrl: './comment-item.scss',
  host: {
    '[attr.depth]': 'depth()'
  }
})
export class CommentItem {
  private authService = inject(AuthService);
  private commentVoteService = inject(CommentVoteService);
  notificationService = inject(NotificationService);

  comment = input.required<Comment>();
  postId = input.required<string>();
  depth = input<number>(0);
  isReply = input<boolean>(false);

  edit = output<{ id: string, content: string }>();
  delete = output<string>();
  voteChanged = output<{ commentId: string; stats: CommentVoteStats} >();

  isEditing = signal(false);
  showReplyForm = signal(false);
  isVoting = signal(false);
  voteStats = signal<VoteStats | null>(null);

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

  ngOnInit(): void {
    this.loadVoteStats();
  }

  loadVoteStats(): void {
    this.commentVoteService.getCommentVoteStats(this.comment().id).subscribe({
      next: (response) => {
        this.voteStats.set(response.data);
      },
      error: (error) => {
        console.error('Error loading comment vote stats', error);
      }
    });
  }

  handleVote(voteType: 'LIKE' | 'DISLIKE' | 'REMOVE'): void {
    this.isVoting.set(true);

    const request$ = voteType === 'REMOVE'
      ? this.commentVoteService.removeCommentVote(this.comment().id)
      : this.commentVoteService.voteComment(this.comment().id, { type: voteType });

    request$.subscribe({
      next: (response) => {
        this.voteStats.set(response.data);
        this.notificationService.show(response.message || 'Голос учтен', 'success', 200)
        // this.voteChanged.emit({ 
        //   commentId: this.comment().id, 
        //   stats: stats
        // });
        this.isVoting.set(false);
      },
      error: (error) => {
        console.error('Error voting comment:', error);
        this.isVoting.set(false);
      }
    });
  }

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
