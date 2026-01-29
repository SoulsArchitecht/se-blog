import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.scss']
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private postService = inject(PostService);
  private fb = inject(FormBuilder);
  
  user = signal<User | null>(null);
  userPosts = signal<Post[]>([]);
  isLoading = signal(true);
  isEditing = signal(false);
  
  profileForm: FormGroup;
  
  constructor() {
    this.profileForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      bio: ['', Validators.maxLength(500)]
    });
  }
  
  ngOnInit() {
    this.loadProfile();
  }
  
  loadProfile(): void {
    this.user.set(this.authService.user());
    
    if (this.user()) {
      this.profileForm.patchValue({
        username: this.user()?.username,
        email: this.user()?.email,
        bio: this.user()?.bio || ''
      });
      
      this.loadUserPosts();
    }
  }
  
  loadUserPosts(): void {
    this.isLoading.set(true);
    
    // Здесь будет загрузка постов пользователя
    setTimeout(() => {
      this.userPosts.set([
        {
          id: 1,
          title: 'Пример поста',
          content: 'Содержимое поста...',
          excerpt: 'Краткое описание...',
          slug: 'primer-posta',
          author: this.user()!,
          category: 'Программирование',
          tags: ['angular', 'typescript'],
          commentsCount: 5,
          likesCount: 10,
          viewsCount: 100,
          isPublished: true,
          createdAt: new Date(),
          updatedAt: new Date()
        }
      ]);
      this.isLoading.set(false);
    }, 1000);
  }
  
  enableEdit(): void {
    this.isEditing.set(true);
  }
  
  saveProfile(): void {
    if (this.profileForm.invalid) return;
    
    // Здесь будет обновление профиля через API
    console.log('Profile updated:', this.profileForm.value);
    
    this.isEditing.set(false);
  }
  
  cancelEdit(): void {
    this.isEditing.set(false);
    this.loadProfile(); // Сбрасываем форму
  }
  
  deleteAccount(): void {
    if (confirm('Вы уверены? Это действие нельзя отменить.')) {
      // Здесь будет удаление аккаунта
      this.authService.logout();
    }
  }
}