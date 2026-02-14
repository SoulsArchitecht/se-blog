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
    readonly isAuthenticated = () => !!this.tokenSignal();

    private BASE_URL = environment.apiUrl;

    constructor() {
        this.loadUserFromStorage();
    }

    private loadUserFromStorage(): void {
        const userJson = localStorage.getItem('user');
        const token = localStorage.getItem('token');

        if (userJson && token) {
            try {
                const user = JSON.parse(userJson);
                this.userSignal.set(user);
                this.tokenSignal.set(token);
            } catch {
                this.clearSession();
            }
        }
    }

    private clearSession(): void {
        this.userSignal.set(null);
        this.tokenSignal.set(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    }

    private setSession(authResponse: AuthResponse): void {
        this.userSignal.set(authResponse.user);
        this.tokenSignal.set(authResponse.accessToken);

        localStorage.setItem('user', JSON.stringify(authResponse.user));
        localStorage.setItem('token', authResponse.accessToken);
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
}
