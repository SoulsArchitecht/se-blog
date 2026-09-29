import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  
  loginForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string>('');
  
  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }
  
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.markFormAsTouched();
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set('');
    
    const { email, password } = this.loginForm.value;
    
    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/']);
      },
      error: (error) => {
        //this.errorMessage.set(error.message || 'Неверный email или пароль');
        this.isLoading.set(false);

        const backendMessage = error.error?.userMessage;

        if (error.status === 401) {
          this.errorMessage.set(backendMessage || 'Неверный email или пароль');
        } else if (error.status === 400) {
          this.errorMessage.set(backendMessage || 'Ошибка валидации данных');
        } else if (error.status === 0) {
          this.errorMessage.set('нет соединения с сервером. Проверьте интернет.');
        } else {
          this.errorMessage.set('Произошла ошибка сервера. Попробуйте позже');
        }
      },
      complete: () => {
        this.isLoading.set(false);
      }
    });
  }
  
  private markFormAsTouched(): void {
    Object.values(this.loginForm.controls).forEach(control => {
      control.markAsTouched();
    });
  }
}