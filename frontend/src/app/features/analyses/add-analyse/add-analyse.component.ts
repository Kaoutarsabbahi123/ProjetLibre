import { Component, OnInit } from '@angular/core';
import { AnalysisService } from '../../../core/analysis.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-analyse',
  templateUrl: './add-analyse.component.html',
  styleUrls: ['./add-analyse.component.css']
})
export class AddAnalyseComponent implements OnInit {
  laboratoires: any[] = [];
  epreuves: any[] = [];
  nom: string = '';
  description: string = '';
  cout: number = 0;
  selectedLaboratoire: number | null = null;
  isModalVisible: boolean = false; // Contrôle de la visibilité de la modale
  isError: boolean = false;
  logoError: boolean = false; // Contrôle si c'est un message d'erreur

  constructor(
    private analysisService: AnalysisService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchLaboratoires();
  }

  // Récupérer la liste des laboratoires pour la liste déroulante
 fetchLaboratoires(): void {
     this.analysisService.getLaboratoiresNomsNonArchive().subscribe((data: any[]) => {
       this.laboratoires = data.map((item: any) => ({
         id: item[0], // Premier élément du tableau
         nom: item[1], // Deuxième élément du tableau
       }));
     });
   }
 onLaboratoireChange(laboId: number): void {
    console.log("Laboratoire sélectionné :", laboId);
    this.selectedLaboratoire = laboId;
  }

  // Ajouter une épreuve
  addEpreuve(): void {
    const nouvelleEpreuve = {
      nom: '',
      testAnalyses: []
    };
    this.epreuves.push(nouvelleEpreuve);
  }

  // Supprimer une épreuve
  removeEpreuve(index: number): void {
    if (index >= 0 && index < this.epreuves.length) {
      this.epreuves.splice(index, 1);
    }
  }

  // Ajouter un test à une épreuve
  addTestToEpreuve(epreuveIndex: number): void {
    const nouveauTest = {
      nom: '',
      description: '',
      valeursDeReference: ''
    };
    this.epreuves[epreuveIndex].testAnalyses.push(nouveauTest);
  }

  // Supprimer un test d'une épreuve
  removeTestFromEpreuve(epreuveIndex: number, testIndex: number): void {
    if (
      epreuveIndex >= 0 &&
      epreuveIndex < this.epreuves.length &&
      testIndex >= 0 &&
      testIndex < this.epreuves[epreuveIndex].testAnalyses.length
    ) {
      this.epreuves[epreuveIndex].testAnalyses.splice(testIndex, 1);
    }
  }
onCancel(): void {
  this.router.navigate(['/analyses']);
}
  // Envoyer le formulaire
  onSubmit(): void {
      const formData = new FormData();
      formData.append('nom', this.nom);
      formData.append('description', this.description);
      formData.append('cout', String(this.cout));
      formData.append('fkIdLaboratoire', String(this.selectedLaboratoire));

      // Ajouter les épreuves et leurs testanalyses sous forme JSON
      formData.append('epreuves', JSON.stringify(this.epreuves));

      this.analysisService.addAnalysis(formData).subscribe(
        (response: any) => {
          console.log('Analyse ajoutée avec succès:', response);
          this.isModalVisible = true; // Afficher la modale de succès
          this.isError = false; // Pas d'erreur
        },
        (error: any) => {
          console.error('Erreur lors de l\'ajout de l\'analyse:', error);
          this.isModalVisible = true; // Afficher la modale d'erreur
          this.isError = true;
        }
      );
  }

onModalOk(): void {
    this.isModalVisible = false; // Fermer la modale
    if (!this.isError) {
      this.router.navigate(['/analyses']); // Rediriger après succès
    }
  }

}
