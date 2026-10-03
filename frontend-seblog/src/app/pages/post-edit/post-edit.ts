import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostForm } from '../../components/post-form/post-form';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-post-edit',
  standalone: true,
  imports: [PostForm],
  templateUrl: './post-edit.html',
  styleUrl: './post-edit.scss'
})
export class PostEdit implements OnInit {
  private postService = inject(PostService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  postData = signal<any>(null);
  isLoading = signal(true);
  errorMessage = signal<string>('');

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/']);
      return;
    }

    this.postService.getPost(id).subscribe({
      next: (response) => {
        this.postData.set(response.data);
        this.isLoading.set(false);
      },
      error: () => {
        this.router.navigate(['/404']);
      }
    });
  }

  onSubmit(postData: any): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.errorMessage.set('ID поста не найден');
      return;
    }
    this.postService.updatePost(id, postData).subscribe({
      next: () => {
        this.router.navigate(['/post', id]);
      },
      error: (error) => {
        this.errorMessage.set(error.message || 'Ошибка обновления поста');
      }
    });
  }

  onCancel(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.router.navigate(['/']);
      return;
    }
    this.router.navigate(['/post', id]);
  }
}
