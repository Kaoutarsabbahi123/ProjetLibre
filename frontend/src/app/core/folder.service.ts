import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

export interface Folder {
  id: number;
  name: string;
  creationDate: string;
  archived?: boolean; // Optionnel pour marquer si le dossier est archivé
}

@Injectable({
  providedIn: 'root'
})
export class FolderService {
  private apiUrl = 'http://localhost:8222/api/folders'; // URL de base de l'API

  constructor(private http: HttpClient) {}

  // Méthode pour récupérer tous les dossiers
  getFolders(): Observable<Folder[]> {
    return this.http.get<Folder[]>(this.apiUrl);
  }

  createFolder(folderData: any): Observable<Folder> {
    return this.http.post<Folder>(`${this.apiUrl}/create`, folderData, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Méthode pour mettre à jour un dossier
  updateFolder(numDossier: number, folder: any): Observable<Folder> {
    return this.http.put<Folder>(`${this.apiUrl}/update/${numDossier}`, folder);
  }


  // Méthode pour archiver un dossier
  archiveFolder(numDossier: number): Observable<string> {
      return this.http.patch(`${this.apiUrl}/archive/${numDossier}`, null, { responseType: 'text' });
  }

  getFolderById(id: number): Observable<any> {
      return this.http.get<any>(`${this.apiUrl}/${id}`);
    }
}
