import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8222/users'; // URL du backend

  constructor(private http: HttpClient) {}

   createUser(userData: any): Observable<any> {
      return this.http.post<any>(`${this.apiUrl}/create`, userData);
   }

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
 getAllUsers(): Observable<any[]> {
   return this.http.get<any[]>(`${this.apiUrl}/list`);
 }
  getUser(email: string): Observable<any> {
     const url = `${this.apiUrl}/details/email/${email}`; // Mise à jour de l'URL
     return this.http.get(url);
   }
   updateUser(email: string, updatedUser: any): Observable<any> {
       const url = `${this.apiUrl}/update/${email}`; // Endpoint pour la mise à jour de l'utilisateur
       return this.http.put(url, updatedUser);
   }
  archiveUser(email: string): Observable<any> {
      const url = `${this.apiUrl}/archive/${email}`; // Endpoint pour archiver un utilisateur
      return this.http.put(url, {}); // Envoie d'une requête vide car le serveur s'attend à ce type de requête
  }

  activateUser(email: string): Observable<any> {
      const url = `${this.apiUrl}/activate/${email}`; // Endpoint pour activer un utilisateur
      return this.http.put(url, {}); // Envoie d'une requête vide car le serveur s'attend à ce type de requête
  }


}
