import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean | UrlTree {
    const token = localStorage.getItem('token'); // Check if a token exists in localStorage
    if (token) {
      return true; // User is authenticated, allow access
    } else {
      return this.router.createUrlTree(['/login']); // Redirect to login page
    }
  }
}
