import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse } from '../models/api-response.model';
import { UserProfile, UserProfileUpdate } from '../models/user-profile.model';

@Injectable({ providedIn: 'root'})
export class UserProfileService {
    private apiService = inject(ApiService);

    private profileSignal = signal<UserProfile | null>(null);
    private loadingSignal = signal(false);

    readonly profile = this.profileSignal.asReadonly();
    readonly isLoading = this.loadingSignal.asReadonly();

    getProfile(): Observable<ApiResponse<UserProfile>> {
        this.loadingSignal.set(true);

        return this.apiService.get<UserProfile>('/users/profile/me').pipe(
            tap({
                next: (response) => {
                    if (response.success && response.data) {
                        this.profileSignal.set(response.data);
                    }
                    this.loadingSignal.set(false);
                },
                error: () => {
                    this.loadingSignal.set(false);
                }
            })
        );
    }

    updateProfile(data: UserProfileUpdate): Observable<ApiResponse<UserProfile>> {
        this.loadingSignal.set(true);

        return this.apiService.put<UserProfile>('/users/profile/me', data).pipe(
            tap({
                next: (response) => {
                if (response.success && response.data) {
                    this.profileSignal.set(response.data);
                }
                this.loadingSignal.set(false);
                },
                error: () => {
                this.loadingSignal.set(false);
                }
            })
        );
    }

    uploadAvatar(file: File): Observable<ApiResponse<string>> {
        this.loadingSignal.set(true);

        const formData = new FormData();
        formData.append('file', file);

        return this.apiService.post<string>('/users/profile/me/avatar', formData).pipe(
            tap({
                next: (response) => {
                    if (response.success && response.data) {
                        const currentProfile = this.profileSignal();
                        if (currentProfile) {
                            this.profileSignal.set({
                                ...currentProfile,
                                avatarUrl: response.data
                            });
                        }
                    }
                    this.loadingSignal.set(false);
                },
                error: () => {
                    this.loadingSignal.set(false);
                }
            })
        );
    }

    clearProfile(): void {
        this.profileSignal.set(null);
    }
}