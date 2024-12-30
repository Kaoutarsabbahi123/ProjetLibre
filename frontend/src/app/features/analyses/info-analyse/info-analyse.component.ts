import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AnalysisService } from '../../../core/analysis.service';

@Component({
  selector: 'app-info-analyse',
  templateUrl: './info-analyse.component.html',
  styleUrls: ['./info-analyse.component.css'],
})
export class InfoAnalyseComponent implements OnInit {
  laboratoires: any[] = [];
  analyseId: number = 0;
  analyse: any = {
    idLaboratoire: '',
    epreuves: [], // Initialise les épreuves
  };
  private initialAnalyse: any = {};
  successMessageVisible: boolean = false;
  successMessageSuppressionVisible: boolean = false;

  @ViewChild('confirmationModal') confirmationModal!: ElementRef;
  @ViewChild('confirmationModalSupp') confirmationModalSupp!: ElementRef;
  constructor(
    private route: ActivatedRoute,
    private analysisService: AnalysisService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['id']) {
        this.analyseId = parseInt(atob(params['id']), 10);
        if (this.analyseId && !isNaN(this.analyseId)) {
          this.analysisService.getAnalyseDetails(this.analyseId).subscribe({
            next: (data) => {
              this.analyse = data;
              this.initialAnalyse = JSON.parse(JSON.stringify(data));
              this.fetchLaboratoires(); // Déplacer l'appel de fetchLaboratoires après la récupération de l'analyse
            },
            error: (err) => {
              console.error('Erreur lors de la récupération de l\'analyse:', err);
            },
          });
        }
      }
    });
  }

 fetchLaboratoires(): void {
    this.analysisService.getLaboratoiresNoms().subscribe((data: any[]) => {
      this.laboratoires = data.map((item: any) => ({
        id: item[0], // Premier élément du tableau
        nom: item[1], // Deuxième élément du tableau
      }));
    });
  }

  onLaboratoireChange(laboId: number): void {
    console.log("Laboratoire sélectionné :", laboId);
    this.analyse.idLaboratoire = laboId;
  }
  // Ajouter une épreuve
  onAddEpreuve(): void {
    const nouvelleEpreuve = {
      nom: '',
      archive: false,
      testAnalyses: [],
    };
    this.analyse.epreuves.push(nouvelleEpreuve);
  }

  // Ajouter un test analyse à une épreuve
  onAddTestAnalyse(epreuveIndex: number): void {
    const nouveauTest = {
      nom: '',
      description: '',
      valeursDeReference: '',
      archive: false,
    };
    this.analyse.epreuves[epreuveIndex].testAnalyses.push(nouveauTest);
  }

  isFormModified(): boolean {
    return JSON.stringify(this.analyse) !== JSON.stringify(this.initialAnalyse);
  }

  showModal(): void {
    const modalElement = this.confirmationModal.nativeElement;
    modalElement.classList.add('show');
    modalElement.style.display = 'block';
  }

  closeModal(): void {
    const modalElement = this.confirmationModal.nativeElement;
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
  }

  showArchiveModal(): void {
    const modalElement = this.confirmationModalSupp.nativeElement;
    modalElement.classList.add('show');
    modalElement.style.display = 'block';
  }

  closeModalArchive(): void {
    const modalElement = this.confirmationModalSupp.nativeElement;
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
  }

  // Mise à jour de l'analyse
  confirmUpdate(): void {
    const formData = new FormData();
    formData.append('nom', this.analyse.nom);
    formData.append('description', this.analyse.description);
    formData.append('cout', String(this.analyse.cout));
    formData.append('idLaboratoire', String(this.analyse.idLaboratoire));
    formData.append('epreuves', JSON.stringify(this.analyse.epreuves));

    this.analysisService.updateAnalyse(this.analyseId, formData).subscribe(
      () => {
        console.log('Analyse mise à jour avec succès.');
         this.closeModal();
        this.successMessageVisible = true;
        this.initialAnalyse = JSON.parse(JSON.stringify(this.analyse));
        setTimeout(() => (this.successMessageVisible = false), 5000);
      },
      (error) => {
        console.error('Erreur lors de la mise à jour:', error);
      }
    );
  }

  // Archivage de l'analyse
  confirmArchive(): void {
    this.analysisService.archiveAnalyse(this.analyseId).subscribe({
      next: (response) => {
        console.log(response);
        this.closeModalArchive();
        this.successMessageSuppressionVisible = true;
        setTimeout(() => {
                 this.successMessageSuppressionVisible = false;
                 window.location.reload();
               }, 1000);
      },
      error: (err) => {
        console.error('Erreur lors de l\'archivage:', err);
      },
    });
  }
}
