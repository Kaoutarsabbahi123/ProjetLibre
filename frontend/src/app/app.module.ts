import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './features/login/login.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LaboratoiresComponent } from './features/laboratoires/laboratoires.component';
import { AddLaboratoireComponent } from './features/laboratoires/add-laboratoire/add-laboratoire.component';
import { ProfileComponent } from './features/profile/profile.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { InfoLaboratoireComponent } from './features/laboratoires/info-laboratoire/info-laboratoire.component';
import { UtilisateursComponent } from './features/utilisateurs/utilisateurs.component';
import { AddUtilisateurComponent } from './features/Utilisateurs/add-utilisateur/add-utilisateur.component';
import { InfoUtilisateurComponent } from './features/Utilisateurs/info-utilisateur/info-utilisateur.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SidebarComponent,
    LaboratoiresComponent,
    AddLaboratoireComponent,
    ProfileComponent,
    InfoLaboratoireComponent,
    UtilisateursComponent,
    AddUtilisateurComponent,
    InfoUtilisateurComponent

  ],
  imports: [
     ReactiveFormsModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    NgbModule,

  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
