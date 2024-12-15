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
  logo: File | null = null;
  nom: string = '';
  nrc: string = '';
  active: boolean = true;
  dateActivation: string = new Date().toISOString().split('T')[0]; // Date d'activation actuelle
  @ViewChild('confirmationModal') confirmationModal!: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private laboratoireService: LaboratoireService // Injection du service
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['id']) {
        // Décodage de l'ID en Base64 et conversion en nombre
        this.laboratoireId = parseInt(atob(params['id']), 10);

        // Vérification que l'ID est valide
        if (this.laboratoireId && !isNaN(this.laboratoireId)) {
          this.laboratoireService.getLaboratoireById(this.laboratoireId).subscribe({
            next: (data) => {
              this.laboratoire = data;
            },
            error: (err) => {
              console.error('Erreur lors de la récupération du laboratoire:', err);
            },
          });
        } else {
          console.error('ID du laboratoire invalide.');
        }
      }
    });
  }

  onLogoChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.logo = file; // Directement affecter le fichier sans conversion en Base64
    }
  }

  // Ajouter un contact
  onAddContact(): void {
    const nouveauContact = {
      numTel: '',
      fax: '',
      email: '',
      adresses: [] // Liste d'adresses pour chaque contact
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
    if (index >= 0 && index < this.laboratoire.contacts.length) {
      this.laboratoire.contacts.splice(index, 1);
    }
  }

  // Supprimer une adresse d'un contact
  onRemoveAdresse(contactIndex: number, adresseIndex: number): void {
    if (contactIndex >= 0 && contactIndex < this.laboratoire.contacts.length) {
      const contact = this.laboratoire.contacts[contactIndex];
      if (adresseIndex >= 0 && adresseIndex < contact.adresses.length) {
        contact.adresses.splice(adresseIndex, 1);
      }
    }
  }

  // Afficher la modale
  showModal(): void {
    const modalElement = this.confirmationModal.nativeElement;
    modalElement.classList.add('show');
    modalElement.style.display = 'block';
  }

  // Méthode pour fermer la modale
  closeModal(): void {
    const modalElement = this.confirmationModal.nativeElement;
    modalElement.classList.remove('show');
    modalElement.style.display = 'none';
  }

  // Confirmer la mise à jour
  confirmUpdate(): void {
    const formData = new FormData();
    formData.append('nom', this.laboratoire.nom); // Assuming 'nom' is a field in your laboratoire object
    formData.append('nrc', this.laboratoire.nrc); // Assuming 'nrc' is a field in your laboratoire object
    formData.append('active', String(this.laboratoire.active)); // Assuming 'active' is a field in your laboratoire object
    formData.append('dateActivation', this.laboratoire.dateActivation); // Assuming 'dateActivation' is a field in your laboratoire object

    if (this.logo) {
      formData.append('logo', this.logo, this.logo.name); // Ajout du logo
    }

    formData.append('contacts', JSON.stringify(this.laboratoire.contacts)); // Assuming contacts is an array

    this.laboratoireService.updateLaboratoire(this.laboratoireId, formData).subscribe(
      (response: any) => {
        console.log('Laboratoire mis à jour avec succès.');
        this.closeModal();
      },
      (error: any) => {
        console.error('Erreur lors de la mise à jour du laboratoire:', error);
      }
    );
  }
}
