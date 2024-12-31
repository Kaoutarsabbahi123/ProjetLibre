import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LaboratoireService {
  private apiUrl = 'http://localhost:8222/api/laboratoires'; // URL de base

  constructor(private http: HttpClient) {}

  // Récupérer tous les laboratoires
  getLaboratoires(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  // Ajouter un laboratoire
  addLaboratoire(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/createlabo`, formData);
  }

  getLaboratoireById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
  updateLaboratoire(id: number, formData: FormData): Observable<any> {
     return this.http.put<any>(`${this.apiUrl}/update/${id}`, formData, {
         headers: new HttpHeaders({
             'Accept': 'application/json'
         }),
         withCredentials: true // if you are dealing with authentication tokens or cookies
     });
 }
  archiveLaboratoire(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/archive/${id}`, null, {
      responseType: 'text' // Force Angular à ne pas parser en JSON
    });
  }
 // Récupérer les noms et IDs des laboratoires
  getLaboratoiresNoms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/noms`);
  }
// Récupérer les noms et IDs des laboratoires
  getLaboratoiresNomsNonArchive(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/nomsNonArchive`);
  }


}
