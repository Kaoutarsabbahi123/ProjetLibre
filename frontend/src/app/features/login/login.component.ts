import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  styleUrls: ['./login.component.css'],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = ''; // Variable pour stocker le message d'erreur

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    const credentials = { email: this.email, password: this.password };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        // Sauvegarde des données après une connexion réussie
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));

        // Redirection vers la page principale
        this.router.navigate(['/laboratoires']);
      },
      error: () => {
        // Affiche le message d'erreur et réinitialise les champs
        this.errorMessage = 'Email ou mot de passe incorrect'; // Message en français
        this.email = '';
        this.password = '';
      },
    });
  }
}
