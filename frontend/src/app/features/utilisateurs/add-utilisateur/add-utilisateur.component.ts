import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../../../core/user.service';
import { LaboratoireService } from '../../../core/laboratoire.service';
import { Router } from '@angular/router';

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
  errorMessageVisible: boolean = false;
  messageContent: string = ''; // Contenu du message


  constructor(
    private userService: UserService,
    private laboratoireService: LaboratoireService,
    private router: Router
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
      fkIdLaboratoire: this.laboratoire,
    };

    this.userService.createUser(utilisateur).subscribe(
      (response) => {
        // Redirige avec un message de succès
        this.router.navigate(['/utilisateurs'], {
          queryParams: { success: 'Utilisateur créé avec succès.' },
        });
      },
      (error) => {
        if (
          error.error &&
          typeof error.error === 'string' &&
          error.error.includes('Un utilisateur avec cet email existe déjà')
        ) {
          // Affiche un message d'erreur spécifique pour l'email existant
          this.messageContent = 'Cet email est déjà utilisé.';
        } else {
          // Message générique pour d'autres erreurs
          this.messageContent = 'Erreur lors de la création de l\'utilisateur.';
        }

        this.errorMessageVisible = true;
        setTimeout(() => {
          this.errorMessageVisible = false;
        }, 3000);
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

 onCancel(): void {
   this.router.navigate(['/utilisateurs']);
 }

}
