import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Folder {
  id: number;
  name: string;
  creationDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class FolderService {
  private apiUrl = 'http://localhost:8222/api/folders'; // Remplacez par l'URL de votre API

  constructor(private http: HttpClient) {}

  // Méthode pour récupérer la liste des dossiers
  getFolders(): Observable<Folder[]> {
    return this.http.get<Folder[]>(this.apiUrl);
  }
}
