import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { User } from '../../models/user.model';
import { RouterLink } from '@angular/router';
import { UserProfileService } from '../../services/user-profile.service';
import { UserProfileUpdate } from '../../models/user-profile.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.scss']
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private postService = inject(PostService);
  private userProfileService = inject(UserProfileService);
  private fb = inject(FormBuilder);
  
  //user = signal<User | null>(null);
  //userPosts = signal<Post[]>([]);
  isLoading = signal(false);
  //isEditing = signal(false);
  errorMessage = signal<string>('');
  successMessage = signal<string>('')
  
  profileForm: FormGroup;
  
  constructor() {
    this.profileForm = this.fb.group({
      displayName: ['', [Validators.required, Validators.minLength(3)]],
      firstName: [''],
      lastName: [''],
      birthDate: [''],
      avatarUrl: [''],
      phone: [''],
      bio: [''],
      location: [''],
      optionalEmail: ['', [Validators.email]],
    });
  }
  
  ngOnInit() {
    this.loadProfile();
  }
  
  private loadProfile(): void {
    this.userProfileService.getProfile().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          let formattedDate = '';
          if (response.data.birthDate) {
            const d = new Date(response.data.birthDate);
            formattedDate = new Date(d.getTime() - (d.getTimezoneOffset() * 60000))
              .toISOString()
              .split('T')[0];
          }

          this.profileForm.patchValue({
            displayName: response.data.displayName || '',
            firstName: response.data.firstName || '',
            lastName: response.data.lastName || '',
            birthDate: formattedDate,
            avatarUrl: response.data.avatarUrl || '',
            phone: response.data.phone || '',
            bio: response.data.bio || '',
            location: response.data.locationAt || '',
            optionalEmail: response.data.optionalEmail || ''
          });
        }
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Ошибка загрузки профиля');
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.markFormAsTouched();
      return;
    }
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    //const formData: UserProfileUpdate = this.profileForm.value;
    const formValue = this.profileForm.value;

    const formData: UserProfileUpdate = {
      ...formValue,
      birthDate: formValue.birthDate ? new Date(formValue.birthDate) : null
    };

    this.userProfileService.updateProfile(formData).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        if (response.success) {
          this.successMessage.set('Профиль успешно обновлен');
          setTimeout(() => this.successMessage.set(''), 300);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Ошибка обновления профиля');
      }
    });
  }

  onAvatarUpload(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      if(!file.type.startsWith('image/')) {
        this.errorMessage.set('Пожалуйста, выберите изображение');
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage.set('Файл слишком боольшой. Максимум 5 МБ');
        return;
      }

      this.userProfileService.uploadAvatar(file).subscribe({
        next: (response) => {
          if (response.success) {
            this.successMessage.set('Аватар успешно загружен');
            setTimeout(() => this.successMessage.set(''), 3000);
            this.loadProfile();
          }
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message || 'Ошибка загрузки аватара');
        }
      });
    }
  }

  private markFormAsTouched(): void {
    Object.values(this.profileForm.controls).forEach(control => {
      control.markAsTouched();
    })
  }

  get user() {
    return this.authService.user();
  }

  get profile() {
    return this.userProfileService.profile();
  }

  //getAvatarUrl = this.userProfileService.getAvatarUrl.bind(this.userProfileService);

  getAvatarUrl(avatarFilename?: string | null): string {
    if (!avatarFilename) {
      return '/assets/default-avatar.png';
    }
    return `/api/v1/uploads/${avatarFilename}`;
  }

  


  // loadUserPosts(): void {
  //   this.isLoading.set(true);
    
  //   // Здесь будет загрузка постов пользователя
  //   setTimeout(() => {
  //     this.userPosts.set([
  //       {
  //         id: "1",
  //         title: 'Пример поста',
  //         content: 'Содержимое поста...',
  //         //excerpt: 'Краткое описание...',
  //         slug: 'primer-posta',
  //         author: this.user()!,
  //         postType: 'Программирование',
  //         tags: ['angular', 'typescript'],
  //         commentsCount: 5,
  //         likesCount: 10,
  //         viewsCount: 100,
  //         isPublished: true,
  //         createdAt: new Date(),
  //         updatedAt: new Date(),
  //         status: ''
  //       }
  //     ]);
  //     this.isLoading.set(false);
  //   }, 1000);
  // }
  
  // enableEdit(): void {
  //   this.isEditing.set(true);
  // }
  
  // saveProfile(): void {
  //   if (this.profileForm.invalid) return;
    
  //   // Здесь будет обновление профиля через API
  //   console.log('Profile updated:', this.profileForm.value);
    
  //   this.isEditing.set(false);
  // }
  
  // cancelEdit(): void {
  //   this.isEditing.set(false);
  //   this.loadProfile(); // Сбрасываем форму
  // }
  
  // deleteAccount(): void {
  //   if (confirm('Вы уверены? Это действие нельзя отменить.')) {
  //     // Здесь будет удаление аккаунта
  //     this.authService.logout();
  //   }
  // }
}