import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { LaboratoiresComponent } from './features/laboratoires/laboratoires.component';
import { AddLaboratoireComponent } from './features/laboratoires/add-laboratoire/add-laboratoire.component';
import { ProfileComponent } from './features/profile/profile.component';
import { AuthGuard } from './core/auth.guard';
import { InfoLaboratoireComponent } from './features/laboratoires/info-laboratoire/info-laboratoire.component';
import { UtilisateursComponent } from './features/utilisateurs/utilisateurs.component';
import { AddUtilisateurComponent } from './features/Utilisateurs/add-utilisateur/add-utilisateur.component';
import { InfoUtilisateurComponent } from './features/Utilisateurs/info-utilisateur/info-utilisateur.component';
import { AnalysesComponent } from './features/analyses/analyses.component';
import { InfoAnalyseComponent } from './features/analyses/info-analyse/info-analyse.component';
import { AddAnalyseComponent } from './features/analyses/add-analyse/add-analyse.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  {
    path: '',
    component: SidebarComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'laboratoires', component: LaboratoiresComponent },
      { path: 'add-labo', component: AddLaboratoireComponent },
      { path: 'profile', component: ProfileComponent },
      { path: 'utilisateurs', component: UtilisateursComponent },
      { path: 'add-user', component: AddUtilisateurComponent },
      { path: 'info-labo', component: InfoLaboratoireComponent, canActivate: [AuthGuard]},
      { path: 'info-user', component: InfoUtilisateurComponent, canActivate: [AuthGuard]},
      { path: 'analyses', component: AnalysesComponent },
      { path: 'add-analysis', component: AddAnalyseComponent },
      { path: 'info-analysis', component: InfoAnalyseComponent }
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {

  }
