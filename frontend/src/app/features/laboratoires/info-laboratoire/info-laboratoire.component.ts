import { ActivatedRoute } from '@angular/router';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { LaboratoireService } from '../../../core/laboratoire.service';

@Component({
  selector: 'app-info-labo',
  templateUrl: './info-laboratoire.component.html',
  styleUrls: ['./info-laboratoire.component.css'],
})
export class InfoLaboratoireComponent implements OnInit {
  laboratoireId: number = 0;
  laboratoire: any = {
    contacts: [] // Assurez-vous que contacts est initialisé
  };
  isModified = false;
  private initialLaboratoire: any = {}; // Pour stocker les données initiales
  logo: File | null = null;
  successMessageVisible: boolean = false;
  successMessageSuppressionVisible: boolean = false; // Pour afficher un message de succès pour la suppression


  @ViewChild('confirmationModal') confirmationModal!: ElementRef;
  @ViewChild('confirmationModalSupp') confirmationModalSupp!: ElementRef;
  constructor(
    private route: ActivatedRoute,
    private laboratoireService: LaboratoireService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['id']) {
        this.laboratoireId = parseInt(atob(params['id']), 10);

        if (this.laboratoireId && !isNaN(this.laboratoireId)) {
          this.laboratoireService.getLaboratoireById(this.laboratoireId).subscribe({
            next: (data) => {
              this.laboratoire = data;

              // Sauvegarder une copie des données initiales
              this.initialLaboratoire = JSON.parse(JSON.stringify(data));
            },
            error: (err) => {
              console.error('Erreur lors de la récupération du laboratoire:', err);
            },
          });
        }
      }
    });
  }

  onLogoChange(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput && fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];

      if (file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = () => {
          // Met à jour l'aperçu du logo en Base64
          this.laboratoire.logo = reader.result?.toString().split(',')[1]; // Stock Base64
        };
        reader.readAsDataURL(file);

        // Stocke le fichier pour l'envoi au backend
        this.logo = file;
      } else {
        alert('Veuillez sélectionner une image valide.');
      }
    } else {
      alert('Aucun fichier sélectionné. Veuillez réessayer.');
    }
  }




  // Ajouter un contact
  onAddContact(): void {
    const nouveauContact = {
      numTel: '',
      fax: '',
      email: '',
      adresses: []
    };
    this.laboratoire.contacts.push(nouveauContact);
  }

  // Ajouter une adresse à un contact
  onAddAdresse(contactIndex: number): void {
    const nouvelleAdresse = {
      numVoie: '',
      nomVoie: '',
      codePostal: '',
      ville: '',
      commune: ''
    };
    this.laboratoire.contacts[contactIndex].adresses.push(nouvelleAdresse);
  }

  // Supprimer un contact
  onRemoveContact(index: number): void {
    this.laboratoire.contacts.splice(index, 1);
  }

  // Supprimer une adresse d'un contact
  onRemoveAdresse(contactIndex: number, adresseIndex: number): void {
    this.laboratoire.contacts[contactIndex].adresses.splice(adresseIndex, 1);
  }

  // Afficher la modale
  showModal(): void {
    const modalElement = this.confirmationModal.nativeElement;
    modalElement.classList.add('show');
    modalElement.style.display = 'block';
  }

  // Fermer la modale
  closeModal(): void {
    const modalElement = this.confirmationModal.nativeElement;
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
  }
  closeModalArchiver(): void {
    const modalElement = this.confirmationModalSupp.nativeElement;
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
  }

  // Confirmer la mise à jour
  confirmUpdate(): void {
    const formData = new FormData();
    formData.append('nom', this.laboratoire.nom);
    formData.append('nrc', this.laboratoire.nrc);
    formData.append('active', String(this.laboratoire.active));
    formData.append('dateActivation', this.laboratoire.dateActivation);

   if (this.logo) {
       formData.append('logo', this.logo);
     }

    formData.append('contacts', JSON.stringify(this.laboratoire.contacts));

    this.laboratoireService.updateLaboratoire(this.laboratoireId, formData).subscribe(
      () => {
        console.log('Laboratoire mis à jour avec succès.');
        this.closeModal();

        // Mettre à jour les données initiales après modification
        this.initialLaboratoire = JSON.parse(JSON.stringify(this.laboratoire));

        // Afficher le message de succès
        this.successMessageVisible = true;
        setTimeout(() => {
                  this.successMessageVisible = false;
                  window.location.reload();
                }, 3000);
      },
      (error: any) => {
        console.error('Erreur lors de la mise à jour du laboratoire:', error);
      }
    );
  }
// Nouvelle méthode pour afficher la modale de confirmation d'archivage
showArchiveModal(): void {
  const modalElement = this.confirmationModalSupp.nativeElement;
  modalElement.classList.add('show');
  modalElement.style.display = 'block';
}

// Méthode pour archiver le laboratoire
confirmArchive(): void {
  this.laboratoireService.archiveLaboratoire(this.laboratoireId).subscribe({
    next: (response) => {
      // Traitement de la réponse en texte brut
      if (typeof response === 'string') {
        console.log(response);
        this.closeModalArchiver();// "Laboratoire archivé avec succès"
        this.successMessageSuppressionVisible = true;
        setTimeout(() => {
          this.successMessageSuppressionVisible = false;
          window.location.reload();
        }, 3000);
      }
    },
    error: (err) => {
      console.error('Erreur lors de l\'archivage du laboratoire:', err);
    },
  });
}
onInputChange(): void {
    this.isModified = this.isFormModified(); // Vérifier si le formulaire est modifié
  }

  isFormModified(): boolean {
    const laboratoireUnchanged = JSON.stringify(this.laboratoire) === JSON.stringify(this.initialLaboratoire);
    const logoChanged = this.logo !== null;
    return !laboratoireUnchanged || logoChanged;
  }



}
