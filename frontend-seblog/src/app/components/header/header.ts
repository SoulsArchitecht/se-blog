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

  readonly isAuthenticated = this.authService.isAuthenticated;
  readonly user = this.authService.user;
  readonly profile = this.UserProfileService.profile;

  readonly isAdmin = computed(() => {
    const user = this.authService.user();
    return user?.role === 'ROLE_ADMIN';
  });

  readonly isModerator = computed(() => {
    const user = this.authService.user();
    return user?.role === 'MODERATOR';
  });

  readonly isUser = computed(() => {
    const user = this.authService.user();
    return user?.role === 'USER';
  });

  constructor() {
    if (this.isAuthenticated()) {
      this.UserProfileService.getProfile().subscribe();
    }
  }

  toggleProfileMenu(): void {
    this.profileMenuOpen.update(v => !v);
  }

  closeProfileMenu(): void {
    this.profileMenuOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.UserProfileService.clearProfile();
  }

  //TODO
  // // проверка роли для отображения админки 
  // hasRole(role: string): boolean {
  //   const user = this.authService.user();
  //   return user?.role?.includes(role) || false;
  // }
}

//<app-register></app-register>
