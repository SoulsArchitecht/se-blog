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
  authService = inject(AuthService);
  private PostVoteService = inject(PostVoteService);

  post = input.required<Post>();
  voteStats = signal<VoteStats | null>(null);
  isVoting = signal(false);

  ngOnInit(): void {
    this.loadVoteStats();
  }

  loadVoteStats(): void {
    this.PostVoteService.getPostVoteStats(this.post().id).subscribe({
      next: (response) => {
        this.voteStats.set(response.data);
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
      next: (response) => {
        this.voteStats.set(response.data);
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

  getTextColor(backgroundColor?: string): string {
    if (!backgroundColor) return '#1f2937';
    
    const hex = backgroundColor.replace('#', '');
    const r = parseInt(hex.substr(0, 2), 16);
    const g = parseInt(hex.substr(2, 2), 16);
    const b = parseInt(hex.substr(4, 2), 16);
    
    const brightness = (r * 299 + g * 587 + b * 114) / 1000;
    return brightness > 128 ? '#1f2937' : '#ffffff';
  }

  getCategoryIcon(iconName?: string): string {
    const iconMap: { [key: string]: string } = {
      'cpu': '💻',
      'code': '⌨️',
      'music-note': '🎵',
      'laugh': '😄',
      'poem': '✍️',
      'book-open': '📖',
      'default': '📌'
    };
    
    return iconName ? (iconMap[iconName] || iconMap['default']) : iconMap['default'];
  }
}
