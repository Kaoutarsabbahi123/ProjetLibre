import { Component, OnInit } from '@angular/core';
import { LaboratoireService } from '../../../core/laboratoire.service';
import { FolderService } from '../../../core/folder.service';
import { Router } from '@angular/router';  // Ajout de l'importation du Router

@Component({
  selector: 'app-add-dossier',
  templateUrl: './add-dossier.component.html',
  styleUrls: ['./add-dossier.component.css']
})
export class AddDossierComponent implements OnInit {
  folder = {
    creationDate: '',  // Date de création par défaut
    nomComplet: '',
    dateNaissance: '',
    lieuDeNaissance: '',
    sexe: 'femme',
    typePieceIdentite: '',
    numPieceIdentite: '',
    adresse: '',
    numTel: '',
    email: '',
    laboratoire: null
  };

  laboratoires: { id: number; nom: string }[] = []; // Liste des laboratoires
  errorMessageVisible = false; // Pour afficher le message d'erreur
  messageContent = ''; // Contenu du message d'erreur

  constructor(
    private laboratoireService: LaboratoireService,
    private folderService: FolderService,
    private router: Router // Injection du Router pour la navigation
  ) {}

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
    // Crée un objet avec les données du formulaire
    const folderData = {
      nomComplet: this.folder.nomComplet,
      dateNaissance: this.folder.dateNaissance,
      lieuDeNaissance: this.folder.lieuDeNaissance,
      sexe: this.folder.sexe,
      typePieceIdentite: this.folder.typePieceIdentite,
      numPieceIdentite: this.folder.numPieceIdentite,
      adresse: this.folder.adresse,
      numTel: this.folder.numTel,
      email: this.folder.email,
      fkIdLaboratoire: this.folder.laboratoire,
      dateCreation: new Date().toISOString().split('T')[0] // Format de date sans heure
    };

    // Appel à la méthode de création du dossier avec un objet JSON
    this.folderService.createFolder(folderData).subscribe(
      (response) => {
        this.router.navigate(['/dossiers'], {
          queryParams: { success: 'Dossier créé avec succès.' },
        });
      },
      (error) => {
        // Affichage d'un message d'erreur générique
        this.messageContent = 'Erreur lors de la création du dossier.';
        this.errorMessageVisible = true;

        setTimeout(() => {
          this.errorMessageVisible = false;
        }, 3000); // Message d'erreur qui disparaît après 3 secondes

        console.error('Erreur :', error);
      }
    );
  }
onCancel(): void {
  // Check if at least one field is filled
  if (this.isFormFilled()) {
    // If any field is filled, reset the folder fields
    this.folder = {
      creationDate: '',
      nomComplet: '',
      dateNaissance: '',
      lieuDeNaissance: '',
      sexe: '',
      typePieceIdentite: '',
      numPieceIdentite: '',
      adresse: '',
      numTel: '',
      email: '',
      laboratoire: null,
    };
  } else {
    // If no field is filled, navigate to the /dossiers page
    this.router.navigate(['/dossiers']);
  }
}

isFormFilled(): boolean {
  // Check if at least one value in the folder object is truthy
  return Object.values(this.folder).some(value => value !== '' && value !== null);
}



}
