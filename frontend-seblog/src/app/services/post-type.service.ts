import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { PostType} from '../models/post.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root'})
export class PostTypeService {
    private apiService = inject(ApiService);

    getAllPostTypes(): Observable<PostType[]> {
        return this.apiService.get<PostType[]>('/post-types').pipe(
            map(response => response.data)
        );
    }

    getPostTypeById(id: string): Observable<PostType> {
        return this.apiService.get<PostType>(`/post-types/${id}`).pipe(
            map(response => response.data)
        );
    }
}