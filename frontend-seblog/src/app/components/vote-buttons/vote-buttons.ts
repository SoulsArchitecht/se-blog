import { Component, inject, input, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NotificationService } from '../../services/notification.service';
import { PostVoteService } from '../../services/post-vote.service';
import { CommentVoteService } from '../../services/comment-vote.service';
import { AuthService } from '../../services/auth.service';
import { VoteStats } from '../../models/vote.model';
import { ApiResponse } from '../../models/api-response.model';

export type VoteEntityType = 'post' | 'comment';

@Component({
  selector: 'app-vote-buttons',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './vote-buttons.html',
  styleUrl: './vote-buttons.scss'
})
export class VoteButtons {
  authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  private postVoteService = inject(PostVoteService);
  private commentVoteService = inject(CommentVoteService);

  entityType = input.required<VoteEntityType>();
  entityId = input.required<string>();
  stats = input<VoteStats | null>(null);
  isVoting = input<boolean>(false);
  orientation = input<'horizontal' | 'vertical'>('horizontal');
  showCounts = input<boolean>(true);
  showLoginHint = input<boolean>(false);

  private localStats = signal<VoteStats | null>(null);
  private localIsVoting = signal(false);

  currentStats = computed(() => this.localStats() ?? this.stats());

  isLiked(): boolean {
    return this.currentStats()?.userVote === 'LIKE';
  }

  isDisliked(): boolean {
    return this.currentStats()?.userVote === 'DISLIKE';
  }

  vote(voteType: 'LIKE' | 'DISLIKE'): void {
    console.log(' [VoteButtons] vote clicked:', voteType);
    console.log('🔐 [VoteButtons] isAuthenticated:', this.authService.isAuthenticated());
    console.log('⏳ [VoteButtons] isVoting:', this.localIsVoting());

    if (this.localIsVoting() || this.isVoting()) {
      console.log(' [VoteButtons] blocked: already voting');
      return;
    }

    if (!this.authService.isAuthenticated()) {
      console.log('⛔ [VoteButtons] blocked: not authenticated');
      this.notificationService.show('Требуется авторизация', 'warning', 3000);
      return;
    }

    const currentVote = this.currentStats()?.userVote || null;
    const newVoteType = (currentVote === voteType) ? 'REMOVE' : voteType;

    console.log(' [VoteButtons] sending vote:', newVoteType);
    this.localIsVoting.set(true);

    const request$ = this.entityType() === 'post'
      ? this.getPostRequest(newVoteType)
      : this.getCommentRequest(newVoteType);

    request$.subscribe({
      next: (response: ApiResponse<VoteStats>) => {
        console.log('✅ [VoteButtons] success:', response);
        console.log('📊 [VoteButtons] new stats:', response.data);
        console.log(' [VoteButtons] message:', response.message);

        this.notificationService.show(response.message || 'Голос учтен', 'success', 2000);
        this.localStats.set(response.data);
        this.localIsVoting.set(false);
      },
      error: (error) => {
        console.error('❌ [VoteButtons] error:', error);
        this.localIsVoting.set(false);
      }
    });
  }

  private getPostRequest(voteType: 'LIKE' | 'DISLIKE' | 'REMOVE') {
    const id = String(this.entityId());
    if (voteType === 'REMOVE') {
      return this.postVoteService.removePostVote(id);
    }
    return this.postVoteService.votePost(id, { type: voteType });
  }

  private getCommentRequest(voteType: 'LIKE' | 'DISLIKE' | 'REMOVE') {
    const id = String(this.entityId());
    if (voteType === 'REMOVE') {
      return this.commentVoteService.removeCommentVote(id);
    }
    return this.commentVoteService.voteComment(id, { type: voteType });
  }
}