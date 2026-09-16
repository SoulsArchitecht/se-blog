import { Injectable, inject } from "@angular/core";
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '../services/api.service';
import { Post } from '../models/post.model';
import { Comment } from '../models/comment.model';
import { PagedResponse } from "../models/paged-response.model";

export interface NewsPanelData {
    popularPosts: Post[];
    recentComments: Comment[];
}

@Injectable({ providedIn: 'root'} )
export class NewsPanelService {
    private apiService = inject(ApiService);

    getPopularPosts(limit: number = 5): Observable<PagedResponse<Post>> {
        return this.apiService.get<PagedResponse<Post>>('/posts/popular', { limit })
            .pipe(map(response => response.data));
    }

    getRecentComments(limit: number = 5): Observable<PagedResponse<Comment>> {
        return this.apiService.get<PagedResponse<Comment>>('/comments/recent', { limit })
            .pipe(map(response => response.data));
    }

    getNewsPanelData(postLimit: number = 5, commentLimit: number = 5): Observable<NewsPanelData> {
        return forkJoin({
            popularPosts: this.getPopularPosts(postLimit)
                .pipe(map(page => page.content)),
            recentComments: this.getRecentComments(commentLimit)
                .pipe(map(page => page.content))
        });
    }
}
