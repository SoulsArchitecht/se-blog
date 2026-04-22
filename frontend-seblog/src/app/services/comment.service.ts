import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse } from '../models/api-response.model';
import { PagedResponse } from '../models/paged-response.model';
import { Comment } from '../models/post.model';


@Injectable({ providedIn: 'root' })
export class CommentService {
    private apiService = inject(ApiService);

    getComments(postId: string, page: number = 1): Observable <ApiResponse<PagedResponse<Comment>>> {
        return this.apiService.get<PagedResponse<Comment>>(
            `/posts/${postId}/comments`,
            { page: page - 1, size: 10}
        );
    }

    createComment(postId: string, data: { content: string }): Observable<ApiResponse<Comment>> {
        return this.apiService.post<Comment>(`/posts/${postId}/comments`, data);
    }

    deleteComment(commentId: string): Observable<ApiResponse<void>> {
        return this.apiService.delete<void>(`/comments/${commentId}`);
    }
}