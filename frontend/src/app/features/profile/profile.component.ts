import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { UserService } from '../../core/user.service';  // Import du UserService


@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  email: string = '';
  nomComplet: string = '';
  numTel: string = '';
  role: string = '';
  successMessage: string = '';
  errorMessage: string = '';
  passwordData = {
    oldPassword: '',
    newPassword: '',
  };
  message: string | null = null;
  success: boolean | null = null;
  constructor(
    private authService: AuthService,
    private userService: UserService  // Injection du UserService
  ) {}

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}'); // Convertit la chaîne JSON en objet
    const email = user?.email; // Vérifie si l'email existe
    if (email) {
      this.loadUserProfile(email);
    }
  }

  loadUserProfile(email: string): void {
    this.userService.getUserProfile(email).subscribe(
      (profile: any) => {
        this.email = profile.email;
        this.nomComplet = profile.nomComplet;
        this.numTel = profile.numTel;
        this.role = profile.role;
      },
      (error) => {
        this.errorMessage = 'Erreur lors du chargement du profil utilisateur';
      }
    );
  }

  updateProfile(): void {
    const updatedProfile = {
      nomComplet: this.nomComplet,
      numTel: this.numTel,
    };

    this.userService.updateUserProfile(this.email, updatedProfile).subscribe(
      () => {
        this.successMessage = 'Profil mis à jour avec succès';
      },
      (error) => {
        this.errorMessage = 'Erreur lors de la mise à jour du profil';
      }
    );
  }

updatePassword() {
    this.userService.updatePassword(this.email, this.passwordData.oldPassword, this.passwordData.newPassword).subscribe({
      next: () => {
        this.message = 'Mot de passe mis à jour avec succès.';
        this.success = true;
      },
      error: (err) => {
        this.message = 'Erreur lors de la mise à jour : ' + (err.error?.message || err.message);
        this.success = false;
      },
    });
  }

passwordFormVisible = false;

  // Méthodes
  togglePasswordForm() {
    this.passwordFormVisible = !this.passwordFormVisible;
  }
}
