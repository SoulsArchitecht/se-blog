import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';
import { Post, PostCreate, PostUpdate } from '../models/post.model';
import { ApiResponse, PaginatedResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiService = inject(ApiService);
  
  private postsSignal = signal<Post[]>([]);
  private loadingSignal = signal(false);
  
  readonly posts = this.postsSignal.asReadonly();
  readonly isLoading = this.loadingSignal.asReadonly();

  getPosts(params?: { page?: number; limit?: number; category?: string }): Observable<ApiResponse<PaginatedResponse<Post>>> {
    this.loadingSignal.set(true);
    
    return this.apiService.get<PaginatedResponse<Post>>('/posts', params)
      .pipe(
        tap({
          next: (response) => {
            if (params?.page && params.page > 1) {
              this.postsSignal.set([...this.postsSignal(), ...response.data.items]);
            } else {
              this.postsSignal.set(response.data.items);
            }
          },
          complete: () => this.loadingSignal.set(false),
          error: () => this.loadingSignal.set(false)
        })
      );
  }

  getPost(id: number): Observable<ApiResponse<Post>> {
    return this.apiService.get<Post>(`/posts/${id}`);
  }

  createPost(data: PostCreate): Observable<ApiResponse<Post>> {
    return this.apiService.post<Post>('/posts', data);
  }

  updatePost(id: number, data: PostUpdate): Observable<ApiResponse<Post>> {
    return this.apiService.put<Post>(`/posts/${id}`, data);
  }

  deletePost(id: number): Observable<ApiResponse<void>> {
    return this.apiService.delete<void>(`/posts/${id}`);
  }

  clearPosts(): void {
    this.postsSignal.set([]);
  }
}