import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ApiService {

    private readonly API_URL = 'http://localhost:8080/api/v1';

    constructor(private http: HttpClient) {}

    getPosts(): Observable<any> {
        return this.http.get(`${this.API_URL}/posts`);
    }
}