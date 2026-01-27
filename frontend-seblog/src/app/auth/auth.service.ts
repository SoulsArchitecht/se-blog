import { Observable } from "rxjs";
import { AuthResponse, LoginRequest, RegisterRequest } from "../shared/models/auth.model";
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from "../../environments/environment";

@Injectable({ providedIn: 'root' })
export class AuthService {
    
    private readonly API_URL = environment.apiUrl + 'auth';

    constructor(private http: HttpClient) {}

    login(credentials: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials);
    }

    register(credentials: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API_URL}/register`, credentials);
    }

    logout(): void {
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
    }

    setToken(token: string): void {
        localStorage.setItem('authToken', token);
    }

    getToken(): string | null {
        return localStorage.getItem('authToken');
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }
}

