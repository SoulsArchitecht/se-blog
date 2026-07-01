import { Component, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { VoteStats } from '../../models/vote.model';

@Component({
  selector: 'app-vote-buttons',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vote-buttons.html',
  styleUrl: './vote-buttons.scss'
})
export class VoteButtons {
  protected authService = inject(AuthService);

  stats = input.required<VoteStats | null>();
  isVoting = input<boolean>(false);

  voteAction = output<'LIKE' | 'DISLIKE'| 'REMOVE'> ();

  isLiked(): boolean {
    return this.stats()?.userVote === 'LIKE';
  }

  isDisliked(): boolean {
    return this.stats()?.userVote === 'DISLIKE';
  }

  vote(voteType: 'LIKE' | 'DISLIKE'): void {
    if (!this.authService.isAuthenticated() || this.isVoting()) return;

    const currentVote = this.stats()?.userVote || null;
    const newVote = (currentVote === voteType) ? 'REMOVE' : voteType;

    this.voteAction.emit(newVote);
  }
}
