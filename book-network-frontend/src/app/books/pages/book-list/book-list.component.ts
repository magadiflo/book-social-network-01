import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { BookService } from '../../../services/services';
import { PageResponseBookResponse } from '../../../services/models';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {

  private _router = inject(Router);
  private _bookService = inject(BookService);

  public bookResponse?: PageResponseBookResponse;
  public page = 0;
  public size = 5;

  ngOnInit(): void {
    this.findAllBooks();
  }

  public findAllBooks() {
    this._bookService.findAllBooks({ page: this.page, size: this.size })
      .subscribe({
        next: pageBookResponse => {
          this.bookResponse = pageBookResponse;
          console.log(this.bookResponse);
        }
      });
  }

}
