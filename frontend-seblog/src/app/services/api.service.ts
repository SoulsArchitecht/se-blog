import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
//import { consumerPollProducersForChange } from '@angular/core/primitives/signals';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private http = inject(HttpClient);
    private baseUrl = environment.apiUrl;

    get<T>(endpoint: string, params?: any): Observable<ApiResponse<T>> {
        const options = params ? { params: new HttpParams({ fromObject: params }) } : {};
        return this.http.get<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, options)
            .pipe(catchError(this.handleError));
    }

    // post<T>(endpoint: string, body: any): Observable<ApiResponse<T>> {
    //     const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    //     return this.http.post<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, body, { headers })
    //         .pipe(catchError(this.handleError));
    // }

    post<T>(endpoint: string, body: any, options?: { headers?: HttpHeaders }): Observable<ApiResponse<T>> {
        if (body instanceof FormData) {
            return this.http.post<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, body, {
                headers: options?.headers || new HttpHeaders()
            });
        }

        const headers = (options?.headers || new HttpHeaders())
            .set('Content-Type', 'application/json');

        return this.http.post<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, body, { headers })
            .pipe(catchError(this.handleError));
    }

    put<T>(endpoint: string, body: any): Observable<ApiResponse<T>> {
        return this.http.put<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, body)
            .pipe(catchError(this.handleError));
    }

    delete<T>(endpoint: string): Observable<ApiResponse<T>> {
        return this.http.delete<ApiResponse<T>>(`${this.baseUrl}${endpoint}`)
            .pipe(catchError(this.handleError));
    }

    patch<T>(endpoint: string, body: any): Observable<ApiResponse<T>> {
        return this.http.patch<ApiResponse<T>>(`${this.baseUrl}${endpoint}`, body)
            .pipe(catchError(this.handleError));
    }



    private handleError(error: HttpErrorResponse): Observable<never> {
        let errorMessage = 'Произошла ошибка';

        if (error.error instanceof ErrorEvent) {
            errorMessage = `Клиентская ошибка: ${error.error.message}`;
        } else {
            errorMessage = `Серверная ошибка: ${error.status}: ${error.error?.message || error.message}`;
        }

        console.error('API Error: ', error);
        return throwError(() => new Error(errorMessage));
    }
}