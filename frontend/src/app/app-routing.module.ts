import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { AuthGuard } from './core/auth.guard'; // Import your AuthGuard

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'sidebar', component: SidebarComponent ,canActivate: [AuthGuard] },// Protect this route
  { path: '', redirectTo: '/login', pathMatch: 'full' }, // Redirect to login by default
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
