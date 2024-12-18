import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/user.service';  // Import du UserService
import { Router } from '@angular/router';

@Component({
  selector: 'app-utilisateurs',
  templateUrl: './utilisateurs.component.html',
  styleUrls: ['./utilisateurs.component.css']
})
export class UtilisateursComponent implements OnInit {
  users: any[] = []; // Tableau pour stocker les utilisateurs
  searchText: string = ''; // Texte de recherche
  paginatedData: any[] = []; // Données paginées
  currentPage: number = 1; // Page actuelle
  itemsPerPage: number = 4; // Nombre d'éléments par page
  pages: number[] = []; // Liste des pages
  showEllipsis: boolean = false; // Indicateur pour afficher "..."

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.userService.getAllUsers().subscribe(
      (usersData: any[]) => {
        this.users = usersData;  // Les utilisateurs sont déjà complétés avec leur nom de laboratoire

        this.updatePagination();
      },
      (error: any) => {
        console.error('Erreur lors de la récupération des utilisateurs:', error);
      }
    );
  }

  onSearch(): void {
    if (this.searchText.trim()) {
      const searchTextLower = this.searchText.toLowerCase();

      this.users = this.users.filter(user => {
        const nomComplet = user.nomComplet ? user.nomComplet.toLowerCase() : '';
        const email = user.email ? user.email.toLowerCase() : '';
        const role = user.role ? user.role.toLowerCase() : '';
        const statut = user.active ? 'actif' : 'inactif';
        const numTel = user.numTel ? user.numTel.toString().toLowerCase() : '';
        const nomLaboratoire = user.nomLaboratoire ? user.nomLaboratoire.toLowerCase() : ''; // Ajout du nom du labo

        return (
          nomComplet.includes(searchTextLower) ||
          email.includes(searchTextLower) ||
          role.includes(searchTextLower) ||
          statut.includes(searchTextLower) ||
          numTel.includes(searchTextLower) ||
          nomLaboratoire.includes(searchTextLower) // Filtrage par nom du labo
        );
      });
    } else {
      this.fetchUsers(); // Réinitialiser la liste des utilisateurs si searchText est vide
    }
    this.updatePagination();
  }


  updatePagination(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.users.slice(startIndex, endIndex);

    const totalPages = Math.ceil(this.users.length / this.itemsPerPage);
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
    this.router.navigate(['/add-user']);
  }

}
