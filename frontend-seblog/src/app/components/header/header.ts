import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  authService = inject(AuthService);

  logout() {
    this.authService.logout();
  }
}

//<app-register></app-register>
