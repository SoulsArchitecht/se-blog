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
  public router = inject(Router);
  
  postForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string>('');
  
  categories = [
    'Hardware',
    'Software',
    'Music',
    'Humor',
    'Поэзия',
    'Проза'
  ];
  
  constructor() {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      content: ['', [Validators.required, Validators.minLength(50)]],
      postType: ['', Validators.required],
      tags: [''],
      isPublished: [true],
      customSlug: ['']
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
      content: formValue.content,
      postTypeName: formValue.postType,
      title: formValue.title,
      status: 'DRAFT',
      tagNames: formValue.tags.split(',').map((tag: string) => tag.trim()).filter(Boolean),
      customSlug: formValue.customSlug || undefined,
    };

    console.log('Raw form value:', this.postForm.value);
    console.log('JSON for request: ' + JSON.stringify(postData, null, 2));
    
    this.postService.createPost(postData).subscribe({
      next: (response) => {
        this.router.navigate(['/posts', response.data.id]);
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