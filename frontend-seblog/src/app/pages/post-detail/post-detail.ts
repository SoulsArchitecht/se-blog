import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Post, Comment } from '../../models/post.model';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './post-detail.html',
  styleUrls: ['./post-detail.scss']
})
export class PostDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  
  post = signal<Post | null>(null);
  isLoading = signal(true);
  error = signal<string>('');
  
  commentForm: FormGroup;
  isSubmittingComment = signal(false);
  
  constructor() {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(5)]]
    });
  }
  
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadPost(String(params['id']));
    });
  }
  
  loadPost(id: string): void {
    this.isLoading.set(true);
    
    this.postService.getPost(id).subscribe({
      next: (response) => {
        this.post.set(response.data);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.error.set(error.message || 'Пост не найден');
        this.isLoading.set(false);
      }
    });
  }
  
  addComment(): void {
    if (this.commentForm.invalid || !this.post()) return;
    
    this.isSubmittingComment.set(true);
    
    // Здесь будет логика добавления комментария через API
    setTimeout(() => {
      const newComment: Comment = {
        //id: Math.random(),
        id: "1",
        content: this.commentForm.value.content,
        author: {
          id: this.authService.user()?.id || "0",
          username: this.authService.user()?.username || 'Аноним'
        },
        createdAt: new Date()
      };
      
      // Обновляем пост с новым комментарием
      this.post.set({
        ...this.post()!,
        commentsCount: this.post()!.commentsCount + 1
      });
      
      this.commentForm.reset();
      this.isSubmittingComment.set(false);
    }, 1000);
  }
  
  deletePost(): void {
    if (!this.post() || !confirm('Удалить этот пост?')) return;
    
    this.postService.deletePost(this.post()!.id).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error deleting post:', error);
      }
    });
  }
  
  get isAuthor(): boolean {
    return this.post()?.author.id === this.authService.user()?.id;
  }
  
  get canEdit(): boolean {
    return this.isAuthor || this.authService.user()?.username === 'admin';
  }
}