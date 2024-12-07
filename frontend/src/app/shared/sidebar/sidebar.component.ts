import { Component } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  isSidebarHidden = false; // État de la sidebar

  toggleSidebar(): void {
    this.isSidebarHidden = !this.isSidebarHidden;
  }
}
