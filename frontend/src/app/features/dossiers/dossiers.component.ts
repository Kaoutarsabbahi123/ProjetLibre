import { Component, OnInit } from '@angular/core';
import { FolderService} from '../../core/folder.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-folder',
  templateUrl: './dossiers.component.html',
  styleUrls: ['./dossiers.component.scss']
})
export class DossiersComponent implements OnInit {
  folders: any[] = []; // Dossiers filtrés
  allFolders: any[] = []; // Tous les dossiers (non filtrés)
  searchText: string = ''; // Texte de recherche
  paginatedData: any[] = []; // Données paginées
  currentPage: number = 1; // Page actuelle
  itemsPerPage: number = 8; // Nombre d'éléments par page
  pages: number[] = []; // Liste des pages
  showEllipsis: boolean = false; // Indicateur pour afficher "..."

  constructor(private folderService: FolderService,
    private router: Router) {}

  ngOnInit(): void {
    this.fetchFolders();
  }

  fetchFolders(): void {
    this.folderService.getFolders().subscribe(
      (data: any[]) => {
        this.allFolders = data; // Stocker tous les dossiers
        this.folders = [...this.allFolders]; // Initialiser avec tous les dossiers
        this.updatePagination();
      },
      (error: any) => {
        console.error('Erreur lors de la récupération des dossiers :', error);
      }
    );
  }

  onSearch(): void {
    if (this.searchText.trim()) {
      const searchTextLower = this.searchText.toLowerCase();

      this.folders = this.allFolders.filter((folder) => {
        const nomComplet = folder.nomComplet ? folder.nomComplet.toLowerCase() : '';
        const statut = folder.active ? 'actif' : 'inactif';
        const dateCreation = folder.dateCreation
          ? new Date(folder.dateCreation).toLocaleDateString().toLowerCase()
          : '';

        return (
          nomComplet.includes(searchTextLower) ||
          statut.includes(searchTextLower) ||
          dateCreation.includes(searchTextLower)
        );
      });
    } else {
      this.folders = [...this.allFolders]; // Réinitialiser si le champ est vide
    }
    this.updatePagination(); // Mettre à jour la pagination
  }

  updatePagination(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.folders.slice(startIndex, endIndex);

    const totalPages = Math.ceil(this.folders.length / this.itemsPerPage);
    this.pages = [];

    if (totalPages <= 7) {
      this.pages = Array.from({ length: totalPages }, (_, i) => i + 1);
      this.showEllipsis = false;
    } else {
      let startPage = Math.max(1, this.currentPage - 3);
      let endPage = Math.min(totalPages, startPage + 6);

      if (endPage - startPage < 6) {
        startPage = Math.max(1, endPage - 6);
      }

      for (let i = startPage; i <= endPage; i++) {
        this.pages.push(i);
      }

      this.showEllipsis = this.currentPage < totalPages - 3;
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.updatePagination();
  }

 onAdd(): void {
     this.router.navigate(['/add-dossier']);
   }

}
