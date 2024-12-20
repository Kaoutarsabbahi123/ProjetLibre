import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/user.service';  // Import du UserService
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-utilisateurs',
  templateUrl: './utilisateurs.component.html',
  styleUrls: ['./utilisateurs.component.css']
})
export class UtilisateursComponent implements OnInit {
  users: any[] = []; // Tableau pour stocker les utilisateurs
  allUsers: any[] = []; // Copie complète des utilisateurs
  searchText: string = ''; // Texte de recherche
  paginatedData: any[] = []; // Données paginées
  currentPage: number = 1; // Page actuelle
  itemsPerPage: number = 4; // Nombre d'éléments par page
  pages: number[] = []; // Liste des pages
  showEllipsis: boolean = false; // Indicateur pour afficher "..."
  successMessageVisible: boolean = false;
  messageContent: string = '';

  constructor(private userService: UserService,
              private router: Router,
              private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.fetchUsers(); // Charger les utilisateurs dès le début
    this.route.queryParams.subscribe((params) => {
      if (params['success']) {
        this.messageContent = params['success'];
        this.successMessageVisible = true;

        setTimeout(() => {
          this.successMessageVisible = false;
        }, 3000);

        const currentUrl = this.router.url.split('?')[0];
        this.router.navigate([], {
          replaceUrl: true,
          queryParams: {},
        });
      }
    });
  }

  fetchUsers(): void {
    this.userService.getAllUsers().subscribe(
      (usersData: any[]) => {
        this.allUsers = usersData;  // Conserver la copie complète
        this.users = [...this.allUsers]; // Initialiser la liste affichée
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

      // Filtrer la liste des utilisateurs
      this.users = this.allUsers.filter(user => {
        const nomComplet = user.nomComplet ? user.nomComplet.toLowerCase() : '';
        const email = user.email ? user.email.toLowerCase() : '';
        const role = user.role ? user.role.toLowerCase() : '';
        const statut = user.active ? 'actif' : 'inactif';  // Convertir l'état en texte
        const numTel = user.numTel ? user.numTel.toString().toLowerCase() : '';
        const nomLaboratoire = user.nomLaboratoire ? user.nomLaboratoire.toLowerCase() : '';

        return (
          nomComplet.includes(searchTextLower) ||
          email.includes(searchTextLower) ||
          role.includes(searchTextLower) ||
          statut.includes(searchTextLower) ||  // Recherche sur l'état actif/inactif
          numTel.includes(searchTextLower) ||
          nomLaboratoire.includes(searchTextLower)
        );
      });
    } else {
      this.users = [...this.allUsers]; // Réinitialiser la liste des utilisateurs si la recherche est vide
    }

    this.updatePagination(); // Mettre à jour la pagination après filtrage
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

  navigateToInfo(email: string): void {
    if (email) {  // Vérifier si l'email est valide
      const encodedEmail = btoa(email); // Encodage en Base64
      this.router.navigate(['/info-user'], { queryParams: { id: encodedEmail } });
    } else {
      console.error('Email utilisateur invalide:', email);
    }
  }
}

