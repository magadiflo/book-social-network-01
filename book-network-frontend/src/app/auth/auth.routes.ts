import { Routes } from '@angular/router';

import { AuthLayoutPageComponent } from './pages/auth-layout-page/auth-layout-page.component';
import { AuthLoginPageComponent } from './pages/auth-login-page/auth-login-page.component';

export default [
  {
    path: '',
    component: AuthLayoutPageComponent,
    children: [
      { path: 'login', component: AuthLoginPageComponent, },
      { path: '**', redirectTo: 'login', },
    ],
  }

] as Routes;
