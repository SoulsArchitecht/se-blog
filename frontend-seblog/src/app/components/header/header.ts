import { Component, inject, signal, computed } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { UserProfileService } from '../../services/user-profile.service';
import { UserSummary } from '../../models/user-profile.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  private authService = inject(AuthService);
  private UserProfileService = inject(UserProfileService);

  profileMenuOpen = signal(false);
  isDropdownVisible = signal(false);
  menuCloseDelay: ReturnType<typeof setTimeout> | null = null;

  readonly isAuthenticated = this.authService.isAuthenticated;
  readonly user = this.authService.user;
  readonly profile = this.UserProfileService.profile;

  readonly isAdmin = computed(() => {
    const user = this.authService.user();
    return user?.role === 'ROLE_ADMIN';
  });

  readonly isModerator = computed(() => {
    const user = this.authService.user();
    return user?.role === 'ROLE_MODERATOR';
  });

  readonly isUser = computed(() => {
    const user = this.authService.user();
    return user?.role === 'ROLE_USER';
  });

  constructor() {
    if (this.isAuthenticated()) {
      this.UserProfileService.getProfile().subscribe();
    }
  }

  toggleProfileMenu(): void {
    if (this.menuCloseDelay) {
      clearTimeout(this.menuCloseDelay);
      this.menuCloseDelay = null;
    }
    this.profileMenuOpen.update(v => !v);
  }

  closeProfileMenu(): void {
    this.menuCloseDelay = setTimeout(() => {
      this.profileMenuOpen.set(false);
      this.menuCloseDelay = null;
    }, 5000)
  }

  logout(): void {
    this.authService.logout();
    this.UserProfileService.clearProfile();
  }

  getAvatarUrl = this.UserProfileService.getAvatarUrl.bind(this.UserProfileService);

  //TODO
  // // проверка роли для отображения админки 
  // hasRole(role: string): boolean {
  //   const user = this.authService.user();
  //   return user?.role?.includes(role) || false;
  // }
}

//<app-register></app-register>
