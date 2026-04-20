import { Component, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Header } from './components/header/header';
import { Sidebar } from './components/sidebar/sidebar';
import { NewsPanel } from './components/news-panel/news-panel';
import { Footer } from './components/footer/footer';
import { AuthService } from './services/auth.service';
import { UserProfileService } from './services/user-profile.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    Header,
    Sidebar,
    NewsPanel,
    Footer
  ],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  authService = inject(AuthService);
  private userProfileService = inject(UserProfileService);
  
  constructor() {
    effect(() => {
      const isAuthenticated = this.authService.isAuthenticated();
      if (isAuthenticated) {
        this.userProfileService.getProfile().subscribe();
      }
    });
    // Инициализация приложения
    console.log('App initialized');
  }
}