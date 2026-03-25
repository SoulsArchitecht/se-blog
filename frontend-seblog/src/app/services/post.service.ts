import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';
import { Post, PostCreate, PostUpdate } from '../models/post.model';
import { ApiResponse } from '../models/api-response.model';
import { PagedResponse } from '../models/paged-response.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiService = inject(ApiService);
  
  private postsSignal = signal<Post[]>([]);
  private loadingSignal = signal(false);
  private currentPageSignal = signal(1);
  private totalPagesSignal = signal(1);
  private hasMoreSignal = signal(true);
  
  readonly posts = this.postsSignal.asReadonly();
  readonly isLoading = this.loadingSignal.asReadonly();
  readonly currentPage = this.currentPageSignal.asReadonly();
  readonly totalPages = this.totalPagesSignal.asReadonly();
  readonly hasMore = this.hasMoreSignal.asReadonly();

  getPosts(params?: { page?: number; limit?: number; category?: string }): Observable<ApiResponse<PagedResponse<Post>>> {
    const page = params?.page ?? 1;
    const size = params?.limit ?? 10;

    this.loadingSignal.set(true);
    
    return this.apiService.get<PagedResponse<Post>>('/posts/published', { page: page - 1, size })
      .pipe(
        tap({
          next: (response) => {
            if (!response.success) return;

            const paged = response.data;
            this.totalPagesSignal.set(paged.totalPages);
            this.hasMoreSignal.set(page < paged.totalPages);

            if (page === 1) {
              this.postsSignal.set(paged.content);
            } else {
              this.postsSignal.set([...this.postsSignal(), ...paged.content]);
            }
            this.currentPageSignal.set(page);
        },
        complete: () => this.loadingSignal.set(false),
        error: () => {
          this.loadingSignal.set(false);
          this.postsSignal.set([]);
        }
      })
    );
  }

  getPost(id: string): Observable<ApiResponse<Post>> {
    return this.apiService.get<Post>(`/posts/${id}`);
  }

  createPost(data: PostCreate): Observable<ApiResponse<Post>> {
    return this.apiService.post<Post>('/posts', data);
  }

  updatePost(id: string, data: PostUpdate): Observable<ApiResponse<Post>> {
    return this.apiService.put<Post>(`/posts/${id}`, data);
  }

  deletePost(id: string): Observable<ApiResponse<void>> {
    return this.apiService.delete<void>(`/posts/${id}`);
  }

  clearPosts(): void {
    this.postsSignal.set([]);
  }
}