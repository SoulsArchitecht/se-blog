import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { UserPublicProfile } from '../models/user-public-profile.model';
import { ApiResponse } from '../models/api-response.model';
import { Observable } from 'rxjs';

@Injectable({providedIn: 'root'})
export class UserPublicProfileService {
    private http = inject(HttpClient);
    private apiUrl = '/api/v1/users/profile';

    getPublicProfile(userId: string): Observable<UserPublicProfile> {
        return this.http
            .get<ApiResponse<UserPublicProfile>>(`${this.apiUrl}/${userId}/public`)
            .pipe(map(response => response.data));
    }
}