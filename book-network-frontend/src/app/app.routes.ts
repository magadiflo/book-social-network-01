import { Routes } from '@angular/router';

export const APP_ROUTES: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes'),
  },
  {
    path: 'books',
    loadChildren: () => import('./books/books.routes'),
  },
  { path: '**', redirectTo: '/auth', },
];
