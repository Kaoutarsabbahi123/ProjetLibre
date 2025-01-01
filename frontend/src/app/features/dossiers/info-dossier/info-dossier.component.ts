import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { FolderService } from '../../../core/folder.service'; // Service for folder data
import { Router } from '@angular/router';
import { LaboratoireService } from '../../../core/laboratoire.service'; // Service for laboratoire data
import { AnalysisService } from '../../../core/analysis.service'; // Service for laboratoire data
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';


@Component({
  selector: 'app-info-dossier',
  templateUrl: './info-dossier.component.html',
  styleUrls: ['./info-dossier.component.css']
})
export class InfoDossierComponent implements OnInit {
  @ViewChild('confirmationModal') confirmationModal!: ElementRef;
  @ViewChild('confirmationModalArchiver') confirmationModalArchiver!: ElementRef;

  folder: any = {}; // To hold the fetched folder data
  laboratoires: { id: number; nom: string }[] = []; // List of laboratoires
  analyses: any[] = []; // Propriété pour stocker les analyses
  showAnalysesDropdown: boolean = false; // Propriété pour gérer l'affichage de la liste déroulante
  numDossier: number = 0;
  successMessageVisible: boolean = false;
  errorMessageVisible: boolean = false;
  messageContent: string = '';
  analyse: any = null;
  selectedAnalyse: string | null = null;
  printButtonDisabled = true;
  laboratoire: any = {
      contacts: [] // Assurez-vous que contacts est initialisé
    };

  constructor(
    private folderService: FolderService,
    private route: ActivatedRoute, // To extract folder ID from URL
    private router: Router,
    private laboratoireService: LaboratoireService, // Service to get laboratoires
    private analysisService: AnalysisService // Service to get analyses
  ) {}

  ngOnInit(): void {
    const encodedFolderId = this.route.snapshot.queryParamMap.get('id'); // Get encoded folder ID from queryParams

    if (encodedFolderId) {
      try {
        const folderId = +atob(encodedFolderId); // Decode and convert to number
        if (!isNaN(folderId)) { // Check if the decoded ID is a valid number
          this.getFolderDetails(folderId); // Fetch folder details based on decoded ID
        } else {
          throw new Error('Decoded folder ID is not a valid number.');
        }
      } catch (error) {
        this.messageContent = 'Erreur lors de la récupération de l\'ID du dossier.';
        this.errorMessageVisible = true; // Show error message
        setTimeout(() => {
          this.errorMessageVisible = false; // Hide error message after 3 seconds
        }, 3000);
        console.error('Erreur lors de la décodification de l\'ID du dossier :', error);
      }
    } else {
      this.messageContent = 'Aucun ID de dossier fourni.';
      this.errorMessageVisible = true; // Show error message if no ID is provided
      setTimeout(() => {
        this.errorMessageVisible = false; // Hide error message after 3 seconds
      }, 3000);
    }

    this.loadLaboratoires();
  }

  // Fetch the folder details using its ID
  getFolderDetails(id: number): void {
    this.folderService.getFolderById(id).subscribe(
      (data) => {
        data.dateNaissance = this.formatDate(data.dateNaissance);
        data.dateCreation = this.formatDate(data.dateCreation);
        this.folder = data;
        this.folder.laboratoire = data.fkIdLaboratoire;
        this.numDossier = data.numDossier;
        this.loadLaboratoireDetails();
      },
      (error) => {
        this.messageContent = 'Erreur lors de la récupération des détails du dossier.';
        this.errorMessageVisible = true; // Show error message
        setTimeout(() => {
          this.errorMessageVisible = false; // Hide error message after 3 seconds
        }, 3000);
        console.error('Erreur :', error); // Log the error
      }
    );
  }

  formatDate(date: string): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0'); // Mois commence à 0
    const day = String(d.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  // Load the laboratoires list
  loadLaboratoires(): void {
    this.laboratoireService.getLaboratoiresNoms().subscribe(
      (data) => {
        this.laboratoires = data.map((item: any) => ({
          id: item[0], // First element as id
          nom: item[1], // Second element as name
        }));
      },
      (error) => {
        console.error('Erreur lors du chargement des laboratoires :', error);
      }
    );
  }

  onLaboratoireChange(laboId: number): void {
    console.log("Laboratoire sélectionné :", laboId);
    this.folder.fkIdLaboratoire = laboId;  // Met à jour l'ID du laboratoire sélectionné
  }

  toggleDropdown(): void {
    this.showAnalysesDropdown = !this.showAnalysesDropdown; // Toggle visibility of the dropdown
    if (this.showAnalysesDropdown) {
      this.loadAnalyses(); // Load analyses when dropdown is shown
    }
  }

  loadAnalyses(): void {
    // Appel au service pour charger les analyses
    this.analysisService.getNonArchivedAnalysesByLaboratoire(this.folder.fkIdLaboratoire).subscribe(
      (data) => {
        this.analyses = data; // Remplissage des analyses récupérées
      },
      (error) => {
        console.error('Erreur lors du chargement des analyses', error);
      }
    );
  }

  confirmUpdate(): void {
    if (this.folder.laboratoire === null) {
      // Assurez-vous qu'un ID valide est sélectionné dans le formulaire avant de procéder.
      console.error("L'ID du laboratoire est requis.");
      return;
    }

    this.folderService.updateFolder(this.numDossier, this.folder).subscribe(
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

  confirmArchive(): void {
    this.folderService.archiveFolder(this.numDossier).subscribe(
      () => {
        this.successMessageVisible = true;
        this.messageContent = "Dossier archivé avec succès.";  // Message en français
        setTimeout(() => {
          this.successMessageVisible = false;
          window.location.reload();
        }, 3000);
        this.closeModalArchiver(); // Fermer la modale d'archivage
      },
      (error) => {
        this.errorMessageVisible = true;
        this.messageContent = 'Dossier non trouvé.';  // Message en français
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

  onSubmit(): void {
    // Gestion de la soumission du formulaire, si nécessaire
    this.openModal('confirmationModal'); // Ouvre la modale pour confirmation
  }

  onArchive(): void {
    this.openModal('confirmationModalArchiver'); // Ouvre la modale pour l'archivage
  }

  openModal(modalId: string): void {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.style.display = 'block';
      modal.classList.add('show');
    }
  }

onAnalyseChange(event: Event): void {
  this.printButtonDisabled = false;
   const SelectedValue= (event.target as HTMLSelectElement).value;
    const analyseId = parseInt(SelectedValue, 10);
   this.analysisService.getAnalyseDetails(analyseId).subscribe(
     (data) => {
       this.analyse = data; // Stocker les détails de l'analyse
     },
     (error) => {
       this.messageContent = 'Erreur lors de la récupération des détails de l\'analyse.';
       this.errorMessageVisible = true;
       setTimeout(() => (this.errorMessageVisible = false), 5000);
     }
   );
 }
  loadLaboratoireDetails() {
    if (!this.folder.laboratoire) {
      console.warn('ID du laboratoire non défini.');
      return;
    }

    this.laboratoireService.getLaboratoireById(this.folder.laboratoire).subscribe({
      next: (data) => {
        this.laboratoire = data;
        console.log('Données du laboratoire:', this.laboratoire);
      },
      error: (err) => {
        console.error('Erreur lors de la récupération du laboratoire:', err);
      }
    });
  }

imprimerAnalyse(): void {
  const doc = new jsPDF();
    let yOffset = 10; // Position verticale initiale
    const pageWidth = doc.internal.pageSize.width;

    const logoBase64 = this.laboratoire.logo; // Logo en base64
    const textYOffset = yOffset + 5; // Position du texte

    if (logoBase64) {
      // Afficher le logo à gauche
      doc.addImage(logoBase64, 'PNG', 10, yOffset, 30, 20); // Position et taille de l'image

      // Afficher les informations du laboratoire à droite
      doc.setFontSize(12);
      doc.setFont("helvetica", "bold");
      doc.text(this.laboratoire.nom || 'Laboratoire d’Analyses Médicales', 50, textYOffset);

      doc.setFontSize(10);
      doc.setFont("helvetica", "normal");
      doc.text(
        this.laboratoire.contacts?.[0]?.nom || 'Dr. Médecin Biologiste',
        50,
        textYOffset + 5
      );
      doc.text(
        `Adresse : ${this.laboratoire.contacts?.[0]?.adresses?.[0]?.numVoie || ''} ${this.laboratoire.contacts?.[0]?.adresses?.[0]?.nomVoie || ''}, ${this.laboratoire.contacts?.[0]?.adresses?.[0]?.ville || 'Non renseignée'}`,
        50,
        textYOffset + 10
      );
      doc.text(
        `Téléphone : ${this.laboratoire.contacts?.[0]?.numTel || 'Non renseigné'} | Fax : ${this.laboratoire.contacts?.[0]?.fax || 'Non renseigné'} | Email : ${this.laboratoire.contacts?.[0]?.email || 'Non renseigné'}`,
        50,
        textYOffset + 15
      );
    } else {
      // Si aucun logo, centrer le texte
      doc.setFontSize(12);
      doc.setFont("helvetica", "bold");
      doc.text(
        this.laboratoire.nom || 'Laboratoire d’Analyses Médicales',
        pageWidth / 2,
        textYOffset,
        { align: 'center' }
      );
    }

    // Augmenter l'espace vertical pour la section suivante
    yOffset += 30;
   // Ligne séparatrice très fine et plus courte
   doc.setDrawColor(0); // Couleur noire
   doc.setLineWidth(0.1); // Ligne très fine
   const lineStartX = 20; // Position de départ (plus à droite)
   const lineEndX = pageWidth - 20; // Position de fin (plus à gauche)
   doc.line(lineStartX, yOffset, lineEndX, yOffset); // Dessiner la ligne
   yOffset += 8; // Ajuster le décalage vertical




 // Contenu du tableau
 doc.setFont("helvetica", "normal");
 doc.setFontSize(10);

 // Définir les marges pour un style équilibré
 const columnLeftX = 30; // Position de la première colonne
 const columnRightX = pageWidth - 90; // Position de la deuxième colonne

 // Première colonne
 doc.text(`Nom : ${this.folder.nomComplet || 'Non renseigné'}`, columnLeftX, yOffset);
 doc.text(`Numéro : ${this.folder.numDossier || 'Non renseigné'}`, columnLeftX, yOffset + 8);
 doc.text(`Téléphone : ${this.folder.numTel || 'Non renseigné'}`, columnLeftX, yOffset + 16);

 // Deuxième colonne
 doc.text(`Sexe : ${this.folder.sexe || 'Non renseigné'}`, columnRightX, yOffset);
 doc.text(`Email : ${this.folder.email || 'Non renseigné'}`, columnRightX, yOffset + 8);
 doc.text(`Adresse : ${this.folder.adresse || 'Non renseignée'}`, columnRightX, yOffset + 16);

 // Ajouter une ligne séparatrice en dessous des informations
 yOffset += 30; // Espacement vertical pour éviter le chevauchement



  // Centrer le nom de l'analyse
  doc.setFont("helvetica", "bold");
  doc.setFontSize(14);
  doc.text(this.analyse?.nom || 'Nom de l’analyse non renseigné', pageWidth / 2, yOffset, { align: 'center' });
  yOffset += 16;

  // Résultats sous forme de liste, sans bordure
  if (this.analyse && this.analyse.epreuves && Array.isArray(this.analyse.epreuves)) {
    this.analyse.epreuves.forEach((epreuve: any) => {
      // Titre de l'épreuve
      doc.setFont("helvetica", "bold");
      doc.setFontSize(11);
      doc.text(epreuve.nom || 'Nom de l’épreuve non renseigné', 10, yOffset);
      yOffset += 12;

      // En-tête des résultats
      doc.setFont("helvetica", "bold");
      doc.text('Paramètre', 10, yOffset);
      doc.text('Résultat', pageWidth / 2, yOffset, { align: 'center' });
      doc.text('Valeurs de Référence', pageWidth - 10, yOffset, { align: 'right' });
      yOffset += 6;

      // Parcourir les tests d'analyse associés
      if (epreuve.testAnalyses && Array.isArray(epreuve.testAnalyses)) {
        epreuve.testAnalyses.forEach((test: any) => {
          doc.setFont("helvetica", "normal");
          doc.text(
            `${test.nom || 'Paramètre non renseigné'}`,
            10,
            yOffset
          );
          doc.text(
            `${test.resultat || 'N/A'} ${test.unite || ''}`,
            pageWidth / 2,
            yOffset,
            { align: 'center' }
          );
          doc.text(
            `${test.valeursDeReference || 'N/A'}`,
            pageWidth - 10,
            yOffset,
            { align: 'right' }
          );
          yOffset += 6;

          // Vérifier si l'espace est suffisant pour une nouvelle ligne
          if (yOffset > 280) {
            doc.addPage();
            yOffset = 10;
          }
        });
      } else {
        // Si aucun test d'analyse
        doc.setFont("helvetica", "italic");
        doc.text('Aucun test disponible pour cette épreuve.', 15, yOffset);
        yOffset += 6;
      }

      // Ajouter un espacement entre les épreuves
      yOffset += 4;
      if (yOffset > 280) {
        doc.addPage();
        yOffset = 10;
      }
    });
  } else {
    doc.setFont("helvetica", "italic");
    doc.text('Aucune épreuve disponible pour cette analyse.', 10, yOffset);
    yOffset += 8;
  }

  // Footer avec texte centré et taille petite
  const footerText = "Laboratoire d’Analyses Médicales - Rapport généré automatiquement.";
  doc.setFontSize(8);
  doc.setTextColor(100);
  doc.text(footerText, pageWidth / 2, 290, { align: "center" });

  // Téléchargement du PDF
  doc.save('rapport_analyse.pdf');
}







}
