import { Observable, tap } from "rxjs";
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { ApiResponse } from "../models/api-response.model";
import { ApiService } from "./api.service";
import { User, AuthRequest, RegisterRequest, AuthResponse } from '../models/user.model';
import { environment } from "../../environments/environment";



@Injectable({ providedIn: 'root' })
export class AuthService {
    private apiService = inject(ApiService);
    private router = inject(Router);

    private userSignal = signal<User | null>(null)
    private tokenSignal = signal<string | null>(null)

    readonly user = this.userSignal.asReadonly();
    readonly token = this.tokenSignal.asReadonly();
    readonly isAuthenticated = () => {
        const token = this.tokenSignal();
        if (!token) return false;
        return !this.isTokenExpired(token);
    }

    private BASE_URL = environment.apiUrl;

    constructor() {
        this.loadUserFromStorage();
    }

    private isTokenExpired(token: string): boolean {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const expiration = payload.exp * 1000;
            return Date.now() >= expiration;
        } catch {
            return true;
        }
    }

    private loadUserFromStorage(): void {
        const userJson = localStorage.getItem('user');
        const token = localStorage.getItem('token');

        if (userJson && token) {
            if (this.isTokenExpired(token)) {
                this.clearSession();
            } else {
                try {
                    const user = JSON.parse(userJson);
                    this.userSignal.set(user);
                    this.tokenSignal.set(token);
                } catch {
                    this.clearSession();
                }
            }
        }
    }

    public clearSession(): void {
        this.userSignal.set(null);
        this.tokenSignal.set(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    }

    private setSession(authResponse: AuthResponse): void {
        this.userSignal.set(authResponse.user);
        this.tokenSignal.set(authResponse.accessToken);

        localStorage.setItem('user', JSON.stringify(authResponse.user));
        localStorage.setItem('token', authResponse.accessToken);
        if (authResponse.refreshToken) {
            localStorage.setItem('refreshToken', authResponse.refreshToken);
        }
    }

    register(data: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
        //return this.apiService.post<AuthResponse>(`${this.BASE_URL}/auth/register`, data)
        return this.apiService.post<AuthResponse>(`/auth/register`, data)
        .pipe(
            tap(response => this.setSession(response.data))
        );
    }

    login(credentials: AuthRequest): Observable<ApiResponse<AuthResponse>> {
        //return this.apiService.post<AuthResponse>(`${this.BASE_URL}/auth/login`, credentials)
        return this.apiService.post<AuthResponse>(`/auth/login`, credentials)  
        .pipe(
            tap(response => this.setSession(response.data))
        );
    }

    logout(): void {
        this.clearSession();
        this.router.navigate(['/login']);
    }

    refreshTokens(): Observable<ApiResponse<AuthResponse>> {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
            this.logout();
            throw new Error('No refresh token');
        }

        return this.apiService.post<AuthResponse>('/auth/refresh', { refreshToken })
            .pipe(
                tap(response => {
                    this.tokenSignal.set(response.data.accessToken);
                    localStorage.setItem('token', response.data.accessToken);
                    localStorage.setItem('refreshToken', response.data.refreshToken)
                })
        );
    }
}
