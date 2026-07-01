import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '../services/api.service';
import { VoteRequest, VoteStats } from '../models/vote.model';

@Injectable({ providedIn: 'root'})
export class PostVoteService {

    private apiService = inject(ApiService);


    votePost(postId: string, request: VoteRequest): Observable<VoteStats> {
        return this.apiService
        .post<VoteStats>(`/api/v1/posts/${postId}/vote`, request)
        .pipe(map(response => response.data));
    }

    getPostVoteStats(postId: string): Observable<VoteStats> {
        return this.apiService
        .get<VoteStats>(`/api/v1/posts/${postId}/vote/stats`)
        .pipe(map(response => response.data));
    }

    removePostVote(postId: string): Observable<VoteStats> {
        return this.apiService
        .delete<VoteStats>(`/api/v1/posts/${postId}/vote`)
        .pipe(map(response => response.data));
    }
}  