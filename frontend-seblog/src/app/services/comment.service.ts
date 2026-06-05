import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse } from '../models/api-response.model';
import { PagedResponse } from '../models/paged-response.model';
import { Comment } from '../models/comment.model';
import { map } from 'rxjs/operators';
import { CommentCreateRequest } from '../models/comment.model';


@Injectable({ providedIn: 'root' })
export class CommentService {
    private apiService = inject(ApiService);
    //private resource = '/posts';

    getCommentsByPost(
        postId: string,
        page = 0,
        size = 20,
        sort = 'createdAt,asc'
    ): Observable<PagedResponse<Comment>> {
        const params = { page, size, sort };

        return this.apiService
            .get<PagedResponse<Comment>>(`/posts/${postId}/comments`, params)
            .pipe(map(response => response.data));
    }

    getCommentTree(postId: string): Observable<Comment[]> {
        return this.apiService
            .get<Comment[]>(`/posts/${postId}/commetns/tree`)
            .pipe(map(response => response.data));
    }

    getComment(postId: string, commentId: string): Observable<Comment> {
        return this.apiService
            .get<Comment>(`/posts/${postId}/comments/${commentId}`)
            .pipe(map(response => response.data));
    }

    createComment(
        postId: string,
        request: CommentCreateRequest
    ): Observable<Comment> {
        return this.apiService
            .post<Comment>(`/posts/${postId}/comments`, request)
            .pipe(map(response => response.data));
    }

    updateComment(
        postId: string,
        commentId: string,
        request: CommentCreateRequest
    ): Observable<Comment> {
        return this.apiService
            .patch<Comment>(`/posts/${postId}/comments/${commentId}`, request)
            .pipe(map(response => response.data));
    }

    deleteComment(postId: string, commentId: string): Observable<void> {
        return this.apiService
            .delete<void>(`/posts/${postId}/comments/${commentId}`)
            .pipe(map(response => response.data));
    }


    // getComments(postId: string, page: number = 1): Observable <ApiResponse<PagedResponse<Comment>>> {
    //     return this.apiService.get<PagedResponse<Comment>>(
    //         `/posts/${postId}/comments`,
    //         { page: page - 1, size: 10}
    //     );
    // }

    // createComment(postId: string, data: { content: string }): Observable<ApiResponse<Comment>> {
    //     return this.apiService.post<Comment>(`/posts/${postId}/comments`, data);
    // }

    // deleteComment(commentId: string): Observable<ApiResponse<void>> {
    //     return this.apiService.delete<void>(`/comments/${commentId}`);
    // }
}