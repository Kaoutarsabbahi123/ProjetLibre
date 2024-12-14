import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { LaboratoiresComponent } from './features/laboratoires/laboratoires.component';
import { AddLaboratoireComponent } from './features/laboratoires/add-laboratoire/add-laboratoire.component';

import { AuthGuard } from './core/auth.guard';

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
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {

  }
