import { Component } from '@angular/core';
import { AuthService } from '../../core/auth.service';
@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  constructor(private authService: AuthService) {}
  isSidebarHidden = false; // État de la sidebar

  toggleSidebar(): void {
    this.isSidebarHidden = !this.isSidebarHidden;
  }
 logout(): void {
    this.authService.logout();
  }
}
