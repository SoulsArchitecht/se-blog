import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { VoteRequest, VoteStats } from '../models/vote.model';

@Injectable({ providedIn: 'root'})
export class CommentVoteService {

    private apiService = inject(ApiService);

    voteComment(commentId: string, request: VoteRequest): Observable<VoteStats> {
        return this.apiService
            .post<VoteStats>(`/comments/${commentId}/vote`, request)
            .pipe(map(response => response.data));
    }

    getCommentVoteStats(commentId: string): Observable<VoteStats> {
        return this.apiService
            .get<VoteStats>(`/comments/${commentId}/vote/stats`)
            .pipe(map(response => response.data));
    }

    removeCommentVote(commentId: string): Observable<VoteStats> {
        return this.apiService
            .delete<VoteStats>(`/comments/${commentId}/vote`)
            .pipe(map(response => response.data));
    }    
}