import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { VoteRequest, VoteStats } from '../models/vote.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root'})
export class CommentVoteService {

    private apiService = inject(ApiService);

    voteComment(commentId: string, request: VoteRequest): Observable<ApiResponse<VoteStats>> {
        return this.apiService
            .post<VoteStats>(`/comments/${commentId}/vote`, request);
    }

    getCommentVoteStats(commentId: string): Observable<ApiResponse<VoteStats>> {
        return this.apiService
            .get<VoteStats>(`/comments/${commentId}/vote/stats`);
    }

    removeCommentVote(commentId: string): Observable<ApiResponse<VoteStats>> {
        return this.apiService
            .delete<VoteStats>(`/comments/${commentId}/vote`);
    }    
}