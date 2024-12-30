import { Component } from '@angular/core';

@Component({
  selector: 'app-add-dossier',
  templateUrl: './add-dossier.component.html',
  styleUrl: './add-dossier.component.css'
})
export class AddDossierComponent {
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
        selectedAnalyse: ''
      };

      analyses = [
        { id: 1, nom: 'Analyse 1' },
        { id: 2, nom: 'Analyse 2' },
        { id: 3, nom: 'Analyse 3' }
      ];

  constructor() {}

  ngOnInit(): void {}

  onSubmit(): void {
    console.log('Dossier soumis :', this.folder);

    // Ajouter la logique pour enregistrer le dossier ici
    // Par exemple, appeler un service pour envoyer les données au backend
  }
}
