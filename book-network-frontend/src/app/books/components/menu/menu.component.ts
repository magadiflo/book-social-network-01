import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { TokenService } from '../../../auth/services/token.service';

@Component({
  selector: 'books-menu',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent {

  private _tokenService = inject(TokenService);
  private _router = inject(Router);

  public logout() {
    console.log('logout()...');
    this._tokenService.logout();
    this._router.navigate(['/auth', 'login']);
  }

}
