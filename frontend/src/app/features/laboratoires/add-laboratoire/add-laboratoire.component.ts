import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; // Pour redirection
import { LaboratoireService } from '../../../core/laboratoire.service';

@Component({
  selector: 'app-add-laboratoire',
  templateUrl: './add-laboratoire.component.html',
  styleUrls: ['./add-laboratoire.component.css']
})
export class AddLaboratoireComponent implements OnInit {
  contacts: any[] = [];
  logo: File | null = null;

  nom: string = '';
  nrc: string = '';
  active: boolean = true;
  dateActivation: string = new Date().toISOString().split('T')[0];


  isModalVisible: boolean = false; // Contrôle de la visibilité de la modale
  isError: boolean = false;
  logoError: boolean = false; // Contrôle si c'est un message d'erreur

  constructor(
    private laboratoireService: LaboratoireService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  // Ajouter un contact
  onAddContact(): void {
    const nouveauContact = {
      numTel: '',
      fax:'',
      email: '',
      adresses: [] // Liste d'adresses pour chaque contact
    };
    this.contacts.push(nouveauContact);
  }

  // Supprimer un contact
  onRemoveContact(index: number): void {
    if (index >= 0 && index < this.contacts.length) {
      this.contacts.splice(index, 1);
    }
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
    this.contacts[contactIndex].adresses.push(nouvelleAdresse);
  }

  // Supprimer une adresse d'un contact
  onRemoveAdresse(contactIndex: number, adresseIndex: number): void {
    if (contactIndex >= 0 && contactIndex < this.contacts.length) {
      const contact = this.contacts[contactIndex];
      if (adresseIndex >= 0 && adresseIndex < contact.adresses.length) {
        contact.adresses.splice(adresseIndex, 1);
      }
    }
  }

  onFileChange(event: any): void {
    this.logo = event.target.files[0];
  }

  onCancel(): void {
    this.router.navigate(['/laboratoires']); // Redirection directe
  }

  onSubmit(): void {
    const formData = new FormData();
    formData.append('nom', this.nom);
    formData.append('nrc', this.nrc);
    formData.append('active', String(this.active));
    formData.append('dateActivation', this.dateActivation);


    if (this.logo) {
      formData.append('logo', this.logo, this.logo.name);
    }

    formData.append('contacts', JSON.stringify(this.contacts));

    this.laboratoireService.addLaboratoire(formData).subscribe(
      (response: any) => {
        console.log('Laboratoire créé avec succès:', response);
        this.isModalVisible = true; // Afficher la modale de succès
        this.isError = false; // Pas d'erreur
      },
      (error: any) => {
        console.error('Erreur lors de la création du laboratoire:', error);
        this.isModalVisible = true; // Afficher la modale en cas d'échec
        this.isError = true; // Afficher le message d'erreur
      }
    );
  }

  onModalOk(): void {
    this.isModalVisible = false; // Fermer la modale
    if (!this.isError) {
      this.router.navigate(['/laboratoires']); // Rediriger après succès
    }
  }
}
