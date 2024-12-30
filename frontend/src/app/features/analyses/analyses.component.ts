import { Component, OnInit } from '@angular/core';
import { AnalysisService } from '../../core/analysis.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-analyses',
  templateUrl: './analyses.component.html',
  styleUrls: ['./analyses.component.css'],
})
export class AnalysesComponent implements OnInit {
  analyses: any[] = [];
  allAnalyses: any[] = [];
  searchText: string = '';
  paginatedData: any[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 4;
  pages: number[] = [];
  showEllipsis: boolean = false;

  constructor(private analysisService: AnalysisService, private router: Router) {}

  ngOnInit(): void {
    this.fetchAnalyses();
  }

  fetchAnalyses(): void {
    this.analysisService.getAnalyses().subscribe(
      (data: any[]) => {
        this.allAnalyses = data;
        this.analyses = [...this.allAnalyses];
        this.updatePagination();
      },
      (error: any) => {
        console.error('Erreur lors de la récupération des analyses:', error);
      }
    );
  }

  onSearch(): void {
    if (this.searchText.trim()) {
      const searchTextLower = this.searchText.toLowerCase();

      this.analyses = this.allAnalyses.filter((analysis) => {
        const nom = analysis.nom ? analysis.nom.toLowerCase() : '';
        const description = analysis.description ? analysis.description.toLowerCase() : '';
        const archived = analysis.archived ? 'archivé' : 'actif';
        const laboratoireNom = analysis.laboratoireNom ? analysis.laboratoireNom.toLowerCase() : '';
        const cout = analysis.cout !== undefined ? analysis.cout.toString().toLowerCase() : ''; // Vérification du coût

        return (
          nom.includes(searchTextLower) ||
          description.includes(searchTextLower) ||
          archived.includes(searchTextLower) ||
          laboratoireNom.includes(searchTextLower) ||
          cout.includes(searchTextLower) // Recherche sur le coût
        );
      });
    } else {
      this.analyses = [...this.allAnalyses];
    }
    this.updatePagination();
  }

  updatePagination(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.analyses.slice(startIndex, endIndex);

    const totalPages = Math.ceil(this.analyses.length / this.itemsPerPage);
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
    this.router.navigate(['/add-analysis']);
  }

  navigateToInfo(analysisId: number): void {
    const encodedId = btoa(analysisId.toString());
    this.router.navigate(['/info-analysis'], { queryParams: { id: encodedId } });
  }
}
