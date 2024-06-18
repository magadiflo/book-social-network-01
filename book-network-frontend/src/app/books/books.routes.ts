import { Routes } from '@angular/router';

import { BookLayoutPageComponent } from './pages/book-layout-page/book-layout-page.component';
import { BookListComponent } from './pages/book-list/book-list.component';

export default [
  {
    path: '',
    component: BookLayoutPageComponent,
    children: [
      {
        path: '',
        component: BookListComponent,
      },
      {
        path: 'my-books',
        loadComponent: () => import('./pages/my-books/my-books.component')
      },
      { path: '**', redirectTo: '', },
    ],
  }
] as Routes;
