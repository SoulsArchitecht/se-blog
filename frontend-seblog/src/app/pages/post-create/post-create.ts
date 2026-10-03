import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostForm } from '../../components/post-form/post-form';
import { Router, RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { PostCreate } from '../../models/post.model';

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [
    CommonModule, 
    PostForm,
    RouterLink
  ],
  templateUrl: './post-create.html',
  styleUrls: ['./post-create.scss']
})
export class PostCreateComponent {
  private postService = inject(PostService);
  public router = inject(Router);
  
  errorMessage = signal<string>('');
  
  onSubmit(postData: any): void {
    this.postService.createPost(postData).subscribe({
      next: (response) => {
        this.router.navigate(['/post', response.data.id]);
      },
      error: (error) => {
        this.errorMessage.set(error.messgae || 'Ошибка создания поста');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/']);
  }
}