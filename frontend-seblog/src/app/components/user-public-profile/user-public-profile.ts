import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UserPublicProfileService } from '../../services/user-public-profile.service';
import { UserPublicProfile } from '../../models/user-public-profile.model';

@Component({
  selector: 'app-user-public-profile',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    RouterLink
  ],
  templateUrl: './user-public-profile.html',
  styleUrl: './user-public-profile.scss'
})
export class UserPublicProfileComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private profileService = inject(UserPublicProfileService);

  profile = signal<UserPublicProfile | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    const userId = this.route.snapshot.paramMap.get('userId');
    if (userId) {
      this.loadProfile(userId);
    } else {
      this.error.set('ID пользователя не указан');
      this.loading.set(false);
    }
  }

  private loadProfile(userId: string): void {
    this.profileService.getPublicProfile(userId).subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Ошибка загрузки профиля');
        this.loading.set(false);
      }
    })
  }

  getAvatarUrl(avatar?: string | null): string {
    if (!avatar) return '/assets/default-avatar.png';
    return `/api/v1/uploads/${avatar}`;
  }
}
