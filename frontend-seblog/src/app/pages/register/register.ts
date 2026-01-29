import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/user.model';
import { passwordValidator, confirmPasswordValidator } from '../../utils/validators';
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  
  registerForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string>('');
  
  constructor() {
    this.registerForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9_]+$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        passwordValidator()
      ]],
      confirmPassword: ['', Validators.required]
    }, { validators: confirmPasswordValidator });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.markFormAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const formData = this.registerForm.value as RegisterRequest;
    
    this.authService.register(formData).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.errorMessage.set(error.message);
        this.isLoading.set(false);
      }
    });
  }

  private markFormAsTouched(): void {
    Object.values(this.registerForm.controls).forEach(control => {
      control.markAsTouched();
    });
  }
}