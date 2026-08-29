import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '../services/api.service';
import { VoteRequest, VoteStats } from '../models/vote.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root'})
export class PostVoteService {

    private apiService = inject(ApiService);


    votePost(postId: string, request: VoteRequest): Observable<ApiResponse<VoteStats>> {
        return this.apiService
        .post<VoteStats>(`/posts/${postId}/vote`, request);
    }

    getPostVoteStats(postId: string): Observable<ApiResponse<VoteStats>> {
        return this.apiService
        .get<VoteStats>(`/posts/${postId}/vote/stats`);
    }

    removePostVote(postId: string): Observable<ApiResponse<VoteStats>> {
        return this.apiService
        .delete<VoteStats>(`/posts/${postId}/vote`);
    }
}  