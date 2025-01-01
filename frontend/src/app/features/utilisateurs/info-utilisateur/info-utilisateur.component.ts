import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../core/user.service';
import { LaboratoireService } from '../../../core/laboratoire.service';

@Component({
  selector: 'app-info-utilisateur',
  templateUrl: './info-utilisateur.component.html',
  styleUrls: ['./info-utilisateur.component.css']
})

export class InfoUtilisateurComponent implements OnInit {

  user: any = {};  // Pour stocker les détails de l'utilisateur
  laboratoires: any[] = []; // Liste des laboratoires
  successMessageVisible: boolean = false;
  errorMessageVisible: boolean = false;
  messageContent: string = '';
  email: string = ''; // Pour stocker l'email extrait de l'URL
  passwordsMatch: boolean = true;  // Cette propriété vérifie si les mots de passe correspondent

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private laboratoireService: LaboratoireService  // Injecter LaboratoireService
  ) {}

  ngOnInit(): void {
    // Extraire l'email de l'URL (décoder l'email encodé en Base64)
    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.email = atob(params['id']);  // Décoder l'email à partir de Base64
        this.loadUserProfile(this.email);  // Charger les données de l'utilisateur avec l'email
      }
    });

    this.loadLaboratoires();  // Charger la liste des laboratoires si nécessaire
  }

  loadUserProfile(email: string): void {
    this.userService.getUser(email).subscribe(
      (data) => {
        this.user = data; // Récupérer toutes les données
        this.user.laboratoire = data.fkIdLaboratoire; // Associer l'ID du labo pour le select
      },
      (error) => {
        console.error('Erreur lors du chargement de l\'utilisateur', error);
      }
    );
  }

  loadLaboratoires(): void {
    this.laboratoireService.getLaboratoiresNoms().subscribe((data: any[]) => {
      this.laboratoires = data.map((item: any) => ({
        id: item[0], // Premier élément du tableau
        nom: item[1], // Deuxième élément du tableau
      }));
    });
  }

  checkPasswordsMatch(): void {
    this.passwordsMatch = this.user.password === this.user.confirmPassword;
  }

  onCancel(): void {
    // Annuler l'édition et revenir à l'état initial
  }

  onLaboratoireChange(laboId: number): void {
    console.log("Laboratoire sélectionné :", laboId);
    this.user.fkIdLaboratoire = laboId;  // Met à jour l'ID du laboratoire sélectionné
  }

  confirmUpdate(): void {
    if (this.user.laboratoire === null) {
      // Assurez-vous qu'un ID valide est sélectionné dans le formulaire avant de procéder.
      console.error("L'ID du laboratoire est requis.");
      return;
    }

    this.userService.updateUser(this.email, this.user).subscribe(
      (response) => {
        this.successMessageVisible = true;
        this.messageContent = 'Les modifications ont été enregistrées avec succès.';

        // Masquer le message après 3 secondes
        setTimeout(() => {
          this.successMessageVisible = false;
        }, 3000);

        this.closeModal(); // Fermer la modale après la confirmation
      },
      (error) => {
        this.errorMessageVisible = true;
        this.messageContent = 'Une erreur s\'est produite lors de l\'enregistrement.';

        // Masquer le message après 3 secondes
        setTimeout(() => {
          this.errorMessageVisible = false;
        }, 3000);

        console.error('Erreur lors de la mise à jour', error);
      }
    );
  }

 confirmActivate(): void {
     this.userService.activateUser(this.email).subscribe(
       () => {
         this.successMessageVisible = true;
         this.messageContent = "Utilisateur activé avec succès.";  // Message en français
         setTimeout(() => {
           this.successMessageVisible = false;
           window.location.reload();
         }, 3000);
         this.closeModalActiver(); // Fermer la modale d'activation
       },
       (error) => {
         this.errorMessageVisible = true;
         this.messageContent = 'Utilisateur non trouvé.';  // Message en français
         setTimeout(() => {
           this.errorMessageVisible = false;
         }, 3000);
         console.error('Erreur lors de l\'activation', error);  // Message d'erreur en français
       }
     );
 }

 confirmArchive(): void {
     this.userService.archiveUser(this.email).subscribe(
       () => {
         this.successMessageVisible = true;
         this.messageContent = "Utilisateur archivé avec succès.";  // Message en français
         setTimeout(() => {
           this.successMessageVisible = false;
            window.location.reload();
         }, 3000);
         this.closeModalArchiver(); // Fermer la modale d'archivage
       },
       (error) => {
         this.errorMessageVisible = true;
         this.messageContent = 'Utilisateur non trouvé.';  // Message en français
         setTimeout(() => {
           this.errorMessageVisible = false;
         }, 3000);
         console.error('Erreur lors de l\'archivage', error);  // Message d'erreur en français
       }
     );
 }


  closeModal(): void {
    const modal = document.getElementById('confirmationModal');
    if (modal) {
      modal.style.display = 'none';
      modal.classList.remove('show'); // Cacher la modale
    }
  }

  closeModalArchiver(): void {
    const modal = document.getElementById('confirmationModalArchiver');
    if (modal) {
      modal.style.display = 'none';
      modal.classList.remove('show'); // Cacher la modale
    }
  }

  closeModalActiver(): void {
    const modal = document.getElementById('confirmationModalActiver');
    if (modal) {
      modal.style.display = 'none';
      modal.classList.remove('show'); // Cacher la modale
    }
  }

  onSubmit(): void {
    // Gestion de la soumission du formulaire, si nécessaire
    this.openModal('confirmationModal'); // Ouvre la modale pour confirmation
  }

  onArchive(): void {
    this.openModal('confirmationModalArchiver'); // Ouvre la modale pour l'archivage
  }

  onActivate(): void {
    this.openModal('confirmationModalActiver'); // Ouvre la modale pour l'activation
  }

  openModal(modalId: string): void {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.style.display = 'block';
      modal.classList.add('show');
    }
  }
}
