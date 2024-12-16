import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8222/users'; // URL du backend

  constructor(private http: HttpClient) {}

  getUserProfile(email: string): Observable<any> {
    const url = `${this.apiUrl}/profile?email=${email}`;
    return this.http.get(url);
  }

  updateUserProfile(email: string, profileData: any): Observable<any> {
    const url = `${this.apiUrl}/profile?email=${email}`;
    return this.http.put(url, profileData);
  }
 updatePassword(email: string, oldPassword: string, newPassword: string): Observable<any> {
    const url = `${this.apiUrl}/password?email=${email}`;
    return this.http.put(url, { oldPassword, newPassword });
  }
}
