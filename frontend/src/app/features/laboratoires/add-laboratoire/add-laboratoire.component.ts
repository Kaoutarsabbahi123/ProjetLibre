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

  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private laboratoireService: LaboratoireService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  // Ajouter un contact
  onAddContact(): void {
    const nouveauContact = {
      numTel: '',
      fax: '',
      email: '',
      adresses: []
    };
    this.contacts.push(nouveauContact);
  }

  onRemoveContact(index: number): void {
    if (index >= 0 && index < this.contacts.length) {
      this.contacts.splice(index, 1);
    }
  }

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
    this.router.navigate(['/laboratoires']);
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
         this.router.navigate(['/laboratoires'], {
                  queryParams: { success: 'Laboratoire créé avec succès.' },
                });
      },
      (error: any) => {
        this.errorMessage = 'Erreur lors de la création du laboratoire.';
      }
    );
  }
}
