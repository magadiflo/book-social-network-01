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
      { path: '**', redirectTo: '', },
    ],
  }
] as Routes;
