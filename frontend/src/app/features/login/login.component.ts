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

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    const credentials = { email: this.email, password: this.password };

    // Appel au service Auth pour envoyer les credentials à l'API backend
    this.authService.login(credentials).subscribe({
      next: (response) => {
        // Enregistrer le token et l'objet utilisateur dans localStorage
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));  // Sauvegarde de l'utilisateur

        // Rediriger l'utilisateur après une connexion réussie
        this.router.navigate(['/sidebar']); // Modifier avec votre route de destination
      },
      error: () => {
        alert('Invalid username or password');
      },
    });
  }
}
