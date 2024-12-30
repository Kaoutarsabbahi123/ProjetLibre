import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AnalysisService {
  private apiUrl = 'http://localhost:8222/analyses';
  private lienlabo = 'http://localhost:8222/api/laboratoires';

  constructor(private http: HttpClient) {}

  getAnalyses(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getAnalyseDetails(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  addAnalysis(analysis: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/create`, analysis);
  }
  updateAnalyse(id: number, formData: FormData): Observable<any> {
     return this.http.put<any>(`${this.apiUrl}/update/${id}`, formData, {
         headers: new HttpHeaders({
             'Accept': 'application/json'
         }),
         withCredentials: true // if you are dealing with authentication tokens or cookies
     });
 }
  archiveAnalyse(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/archive/${id}`, null, {
      responseType: 'text' // Force Angular à ne pas parser en JSON
    });
  }

  getLaboratoiresNoms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.lienlabo}/noms`);
  }
}
