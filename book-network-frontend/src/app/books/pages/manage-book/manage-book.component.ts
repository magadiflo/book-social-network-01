import { Component, inject } from '@angular/core';
import { FormGroup, NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable, concatMap, of } from 'rxjs';

import { BookService } from '../../../services/services';
import { BookRequest } from '../../../services/models';

@Component({
  selector: 'app-manage-book',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './manage-book.component.html',
  styleUrl: './manage-book.component.scss'
})
export default class ManageBookComponent {

  private _router = inject(Router);
  private _formBuilder = inject(NonNullableFormBuilder);
  private _bookService = inject(BookService);

  public form: FormGroup = this._formBuilder.group({
    id: [null],
    shareable: [null],
    authorName: [''],
    isbn: [''],
    synopsis: [''],
    title: [''],
  });
  public errorMessages: string[] = [];
  public selectedImageFile?: File;
  public imagePreview?: string;

  public onFileSelected(event: Event) {
    this.selectedImageFile = (event.target as HTMLInputElement).files![0];
    console.log(this.selectedImageFile);

    if (!this.selectedImageFile) {
      this.imagePreview = undefined;
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    }
    reader.readAsDataURL(this.selectedImageFile);
  }

  public saveBook() {
    const request = this.form.value as BookRequest;
    this._bookService.saveBook({ body: request })
      .pipe(
        concatMap(bookId => this.selectedImageFile ? this.uploadImage(bookId) : of(bookId))
      )
      .subscribe({
        next: bookId => {
          console.log(bookId);
          this._router.navigate(['/books', 'my-books']);
        },
        error: err => {
          console.log(err);
          this.errorMessages = err.error.validationErrors;
        }
      });
  }

  private uploadImage(bookId: number): Observable<void> {
    return this._bookService.uploadBookCoverPicture({ bookId, body: { file: this.selectedImageFile! } });
  }

}
