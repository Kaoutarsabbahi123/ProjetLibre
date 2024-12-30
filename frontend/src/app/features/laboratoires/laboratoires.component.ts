import { Component, OnInit } from '@angular/core';
import { LaboratoireService } from '../../core/laboratoire.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-laboratoires',
  templateUrl: './laboratoires.component.html',
  styleUrls: ['./laboratoires.component.css'],
})
export class LaboratoiresComponent implements OnInit {
  laboratoires: any[] = []; // Tableau pour stocker les laboratoires filtrés
  allLaboratoires: any[] = []; // Tableau pour stocker tous les laboratoires (non filtrés)
  searchText: string = ''; // Texte de recherche
  paginatedData: any[] = []; // Données paginées
  currentPage: number = 1; // Page actuelle
  itemsPerPage: number = 4; // Nombre d'éléments par page
  pages: number[] = []; // Liste des pages
  showEllipsis: boolean = false; // Indicateur pour afficher "..."
  successMessage: string = '';
  successMessageVisible=false;
  constructor(private laboratoireService: LaboratoireService, private router: Router
    ,private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.fetchLaboratoires();
    this.route.queryParams.subscribe((params) => {
          if (params['success']) {
            this.successMessage = params['success'];
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

  fetchLaboratoires(): void {
    this.laboratoireService.getLaboratoires().subscribe(
      (data: any[]) => {
        this.allLaboratoires = data; // Stocke tous les laboratoires dans allLaboratoires
        this.laboratoires = [...this.allLaboratoires]; // Initialise laboratoires avec tous les laboratoires
        this.updatePagination();
      },
      (error: any) => {
        console.error('Erreur lors de la récupération des laboratoires:', error);
      }
    );
  }

  onSearch(): void {
    if (this.searchText.trim()) {
      const searchTextLower = this.searchText.toLowerCase();

      // Filtrer la liste des laboratoires (sans affecter allLaboratoires)
      this.laboratoires = this.allLaboratoires.filter(lab => {
        const nom = lab.nom ? lab.nom.toLowerCase() : '';
        const nrc = lab.nrc ? lab.nrc.toLowerCase() : '';
        const statut = lab.active ? 'actif' : 'inactif';
        const dateActivation = lab.dateActivation
          ? new Date(lab.dateActivation).toLocaleDateString().toLowerCase()
          : ''; // Conversion de la date en chaîne lisible

        return (
          nom.includes(searchTextLower) ||
          nrc.includes(searchTextLower) ||
          statut.includes(searchTextLower) ||
          dateActivation.includes(searchTextLower)
        );
      });
    } else {
      // Réinitialiser la liste des laboratoires si searchText est vide
      this.laboratoires = [...this.allLaboratoires];
    }
    this.updatePagination(); // Mettre à jour la pagination après le filtrage
  }

  updatePagination(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.laboratoires.slice(startIndex, endIndex);

    const totalPages = Math.ceil(this.laboratoires.length / this.itemsPerPage);
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
    this.router.navigate(['/add-labo']);
  }

  navigateToInfo(laboratoireId: number): void {
    const encodedId = btoa(laboratoireId.toString());
    this.router.navigate(['/info-labo'], { queryParams: { id: encodedId } });
  }
}
