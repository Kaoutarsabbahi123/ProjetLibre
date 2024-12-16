import { Component, OnInit } from '@angular/core';
import { LaboratoireService } from '../../core/laboratoire.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-laboratoires',
  templateUrl: './laboratoires.component.html',
  styleUrls: ['./laboratoires.component.css'],
})
export class LaboratoiresComponent implements OnInit {
  laboratoires: any[] = []; // Tableau pour stocker les laboratoires
  searchText: string = ''; // Texte de recherche
  paginatedData: any[] = []; // Données paginées
  currentPage: number = 1; // Page actuelle
  itemsPerPage: number = 4; // Nombre d'éléments par page
  pages: number[] = []; // Liste des pages
  showEllipsis: boolean = false; // Indicateur pour afficher "..."

  constructor(private laboratoireService: LaboratoireService, private router: Router) {}

  ngOnInit(): void {
    this.fetchLaboratoires();
  }

  fetchLaboratoires(): void {
    this.laboratoireService.getLaboratoires().subscribe(
      (data: any[]) => {
        this.laboratoires = data;
        this.updatePagination();
      },
      (error: any) => {
        console.error('Erreur lors de la récupération des laboratoires:', error);
      }
    );
  }

  onSearch(): void {
    if (this.searchText.trim()) {
      // Filtrage en fonction de searchText
      this.laboratoires = this.laboratoires.filter(lab =>
        lab.nom.toLowerCase().includes(this.searchText.toLowerCase()) ||
        lab.nrc.toLowerCase().includes(this.searchText.toLowerCase()) ||
        (lab.active ? 'Actif' : 'Inactif').toLowerCase().includes(this.searchText.toLowerCase())
      );
    } else {
      // Réinitialiser la liste des laboratoires si searchText est vide
      this.fetchLaboratoires();
    }
    this.updatePagination();
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
    const encodedId = btoa(laboratoireId.toString()); // Encodage en Base64
    this.router.navigate(['/info-labo'], { queryParams: { id: encodedId } });
  }
}
