import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LaboratoireService {
  private apiUrl = 'http://localhost:8222/api/laboratoires'; // URL de base

  constructor(private http: HttpClient) {}


  getLaboratoires(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  // Méthode existante pour ajouter un laboratoire
  addLaboratoire(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/createlabo`, formData);
  }
}
