import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../../../core/user.service';
import { LaboratoireService } from '../../../core/laboratoire.service';

@Component({
  selector: 'app-add-utilisateur',
  templateUrl: './add-utilisateur.component.html',
  styleUrls: ['./add-utilisateur.component.css'],
})
export class AddUtilisateurComponent {
 @ViewChild('userForm', { static: false }) userForm?: NgForm;
  nomComplet: string = '';
  numTel: string = '';
  email: string = '';
  laboratoire: number | null = null;
  password: string = '';
  confirmPassword: string = '';
  laboratoires: any[] = [];
  role: string = 'technicien'; // Valeur par défaut
  active: boolean = true; // État par défaut : Actif
  successMessageVisible: boolean = false;
  errorMessageVisible: boolean = false;
  messageContent: string = ''; // Contenu du message


  constructor(
    private userService: UserService,
    private laboratoireService: LaboratoireService
  ) {
    this.loadLaboratoires();
  }

  loadLaboratoires(): void {
    this.laboratoireService.getLaboratoiresNoms().subscribe((data) => {
      this.laboratoires = data.map((item: any) => ({
        id: item[0], // Premier élément du tableau
        nom: item[1], // Deuxième élément du tableau
      }));
    });
  }

  // Méthode pour vérifier si les mots de passe correspondent
  get passwordsMatch(): boolean {
    return this.password === this.confirmPassword;
  }

onSubmit(): void {
  if (this.nomComplet && this.numTel && this.email && this.laboratoire && this.password) {
    const utilisateur = {
      nomComplet: this.nomComplet,
      numTel: this.numTel,
      email: this.email,
      password: this.password,
      role: 'technicien',
      active: true,
      fkIdLaboratoire: this.laboratoire
    };

    this.userService.createUser(utilisateur).subscribe(
      (response) => {
        // Message de succès
        this.messageContent = 'Utilisateur créé avec succès.';
        this.successMessageVisible = true;

        // Masquer après 2 secondes
        setTimeout(() => {
          this.successMessageVisible = false;
        }, 3000);

        // Réinitialiser le formulaire et cacher les erreurs
        this.onCancel();
        this.clearFormErrors();  // Effacer les erreurs après la soumission
      },
      (error) => {
        // Message d'échec
        this.messageContent = 'Erreur lors de la création de l\'utilisateur.';
        this.errorMessageVisible = true;

        // Masquer après 2 secondes
        setTimeout(() => {
          this.errorMessageVisible = false;
        }, 2000);

        console.error('Erreur :', error);
      }
    );
  } else {
    console.error('Formulaire invalide');
  }
}

clearFormErrors(): void {
    if (this.userForm) {
      this.userForm.resetForm();  // Réinitialise le formulaire et efface les erreurs
    }
  }



 // Réinitialiser le formulaire
 onCancel(): void {
   this.nomComplet = '';
   this.numTel = '';
   this.email = '';
   this.laboratoire = null;
   this.password = '';
   this.confirmPassword = '';
   this.role = 'technicien'; // réinitialiser à la valeur par défaut
   this.active = true; // réinitialiser à la valeur par défaut
   // Si vous souhaitez également cacher les erreurs après la réinitialisation, vous pouvez mettre errorMessageVisible à false
   this.errorMessageVisible = false;
 }

}
