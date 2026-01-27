import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { Home } from './home/home';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, Home, Login, Register],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend-seblog');
  protected readonly year = new Date().getFullYear();
}
