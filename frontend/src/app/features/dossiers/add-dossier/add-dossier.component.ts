import { Component, OnInit } from '@angular/core';
import { LaboratoireService } from '../../../core/laboratoire.service';

@Component({
  selector: 'app-add-dossier',
  templateUrl: './add-dossier.component.html',
  styleUrls: ['./add-dossier.component.css']
})
export class AddDossierComponent implements OnInit {
  folder = {
    nomComplet: '',
    dateNaissance: '',
    lieuDeNaissance: '',
    sexe: 'femme',
    typePieceIdentite: '',
    numPieceIdentite: '',
    adresse: '',
    numTel: '',
    email: '',
    laboratoire: null // Pour stocker l'ID du laboratoire sélectionné
  };

  laboratoires: { id: number; nom: string }[] = []; // Liste des laboratoires

  constructor(private laboratoireService: LaboratoireService) {}

  ngOnInit(): void {
    this.loadLaboratoires();
  }

  loadLaboratoires(): void {
      this.laboratoireService.getLaboratoiresNomsNonArchive().subscribe((data) => {
        this.laboratoires = data.map((item: any) => ({
          id: item[0], // Premier élément du tableau
          nom: item[1], // Deuxième élément du tableau
        }));
      });
    }


  onSubmit(): void {
    console.log('Dossier soumis :', this.folder);
    // Logique pour soumettre le dossier
  }
}
