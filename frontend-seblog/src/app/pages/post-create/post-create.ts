import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { PostCreate } from '../../models/post.model';

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-create.html',
  styleUrls: ['./post-create.scss']
})
export class PostCreateComponent {
  private fb = inject(FormBuilder);
  private postService = inject(PostService);
  private router = inject(Router);
  
  postForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string>('');
  
  categories = [
    'Технологии',
    'Программирование',
    'Дизайн',
    'Бизнес',
    'Маркетинг',
    'Образование'
  ];
  
  constructor() {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      content: ['', [Validators.required, Validators.minLength(50)]],
      category: ['', Validators.required],
      tags: [''],
      isPublished: [true]
    });
  }
  
  onSubmit(): void {
    if (this.postForm.invalid) {
      this.markFormAsTouched();
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set('');
    
    const formValue = this.postForm.value;
    const postData: PostCreate = {
      title: formValue.title,
      content: formValue.content,
      category: formValue.category,
      tags: formValue.tags.split(',').map((tag: string) => tag.trim()).filter(Boolean)
    };
    
    this.postService.createPost(postData).subscribe({
      next: (response) => {
        this.router.navigate(['/post', response.data.id]);
      },
      error: (error) => {
        this.errorMessage.set(error.message);
        this.isLoading.set(false);
      }
    });
  }
  
  private markFormAsTouched(): void {
    Object.values(this.postForm.controls).forEach(control => {
      control.markAsTouched();
    });
  }
}