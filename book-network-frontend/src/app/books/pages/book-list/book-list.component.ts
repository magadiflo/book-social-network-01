import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { BookService } from '../../../services/services';
import { PageResponseBookResponse } from '../../../services/models';
import { BookCardComponent } from '../../components/book-card/book-card.component';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {

  private _router = inject(Router);
  private _bookService = inject(BookService);

  public bookResponse?: PageResponseBookResponse;
  public page = 0;
  public size = 4;

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

  public goToPage(page: number) {
    this.page = page;
    this.findAllBooks();
  }

  public goToFirstPage() {
    this.page = 0;
    this.findAllBooks();
  }

  public goToLastPage() {
    this.page = this.bookResponse?.totalPages as number - 1;
    this.findAllBooks();
  }

  public goToPreviousPage() {
    this.page--;
    this.findAllBooks();
  }

  public goToNextPage() {
    this.page++;
    this.findAllBooks();
  }

}
