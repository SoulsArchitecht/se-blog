import { Component, inject, input, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Post } from '../../models/post.model';
import { AuthService } from '../../services/auth.service';
import { PostVoteService } from '../../services/post-vote.service';
import { VoteStats } from '../../models/vote.model';
import { VoteButtons } from '../vote-buttons/vote-buttons';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [
    CommonModule, DatePipe, RouterLink, VoteButtons
  ],
  templateUrl: './post-card.html',
  styleUrl: './post-card.scss'
})
export class PostCard implements OnInit {
  private authService = inject(AuthService);
  private PostVoteService = inject(PostVoteService);

  post = input.required<Post>();
  voteStats = signal<VoteStats | null>(null);
  isVoting = signal(false);

  ngOnInit(): void {
    this.loadVoteStats();
  }

  loadVoteStats(): void {
    this.PostVoteService.getPostVoteStats(this.post().id).subscribe({
      next: (stats) => {
        this.voteStats.set(stats);
      },
      error: (error) => {
        console.error('Error loading post vote stats:', error);
      }
    });
  }

  handleVote(voteType: 'LIKE' | 'DISLIKE' | 'REMOVE'): void {
    this.isVoting.set(true);

    const request$ = voteType === 'REMOVE'
    ? this.PostVoteService.removePostVote(this.post().id)
    : this.PostVoteService.votePost(this.post().id, { type: voteType });

    request$.subscribe({
      next: (stats) => {
        this.voteStats.set(stats);
        this.isVoting.set(false);
      },
      error: (error) => {
        console.error('Error voting post:', error);
        this.isVoting.set(false);
      }
    });
  }
  
  // getAvatarUrl(avatar?: string | null): string {
  //   if (!avatar) return '/assets/defalut-avatar.png';
  //   return `/api/v1/uploads/${avatar}`;
  // }

  getAvatarUrl(avatarFilename?: string | null): string {
    if (!avatarFilename) {
      return '/assets/default-avatar.png';
    }
    return `/api/v1/uploads/${avatarFilename}`;
  }

  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}
