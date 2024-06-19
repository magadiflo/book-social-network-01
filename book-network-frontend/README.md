# Frontend (Angular)

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 17.3.5.

---

## Agrega dependencia de Bootstrap y fontawesome

Luego crear el proyecto, se agregaron dos dependencias adicionales `bootstrap` y `fontawesome`.

```bash
$ npm i bootstrap@5.3.3
$ npm i @fortawesome/fontawesome-free
```

Luego, en la hoja de estilos globales `styles.scss` hacemos referencia a estas dos dependencias:

```scss
@import 'bootstrap/dist/css/bootstrap.min.css';
@import '@fortawesome/fontawesome-free/css/all.min.css';
```

Antes de finalizar, necesitamos agregar el script de bootstrap `bootstrap.bundle.min.js` en el proyecto. Este script, es importante porque permite que elementos como el `Navbar` funcionen en modo responsivo, así como otros elementos. Para eso, en el archivo `angular.json` agregaremos la referencia al script que viene con la instalación que hicimos de Bootstrap.

```json
"architect": {
  "build": {
    "builder": "@angular-devkit/build-angular:application",
    "options": {
      "outputPath": "dist/book-network-frontend",
      "index": "src/index.html",
      ...
      "assets": [
        "src/favicon.ico",
        "src/assets"
      ],
      "styles": [
        "src/styles.scss"
      ],
      "scripts": [
        "node_modules/bootstrap/dist/js/bootstrap.bundle.min.js"
      ]
    },
  }
}
```

Listo, ahora sí podemos usar `bootstrap` y `fontawesome` en nuestra aplicación de Angular.

## Genera los servicios HTTP usando el poder de OpenApi

**NOTA**

> Lo que veremos a continuación es algo que podría haberlo creado a mano, pero para acelerar el proceso de creación de la aplicación 
> utilizaremos una dependencia que usará el archivo json que genera swagger openApi del backend para construir en automático
> el servicio. Este servicio contendrá pues los métodos que permitan hacer las peticiones al backend.

Necesitamos agregar en nuestra aplicación de Angular la dependencia [ng-openapi-gen](https://www.npmjs.com/package/ng-openapi-gen) que **es un generador de código OpenAPI 3 para Angular.**

```bash
$ npm i ng-openapi-gen
```

Con esta dependencia, lo que haremos será generar en nuestra aplicación de Angular la clase de servicio que se ocupa de realizar las solicitudes http a nuestro backend. En otras palabras,
en vez de que nosotros seamos los que programemos el servicio, lo haremos en automático utilizando la dependencia `ng-openapi-gen`. Esto es posible, gracias a que en el backend estamos usando `swagger y openAPI`,
en ese sentido, aprovechamos en que `openAPI` de nuestro backend nos ofrece la especificación en un formato `.json`, mismo archivo que usaremos en esta aplicación de Angular.

Una vez instalada la dependencia de `ng-openapi-gen`, vamos a configurar el `package.json` para agregar el siguiente script en el apartado de `scripts`
que nos permitirá generar la clase de servicio en función de un archivo `.json`, aunque también es posible que pueda ser en función de un
archivo `.yml`, pero la especificación que nos retorna `openApi` del backend es en un archivo `.json`:


````json
"scripts": {
    "ng": "ng",
    "start": "ng serve",
    "build": "ng build",
    "watch": "ng build --watch --configuration development",
    "test": "ng test",
    "api-gen": "ng-openapi-gen --input ./src/openapi/openapi.json --output ./src/app/services"
}
````

Observemos que hemos agregado un script llamado `"api-gen"` al bloque de `"scripts"`. Este comando está haciendo referencia al archivo `openapi.json` que vamos a crear en un instante, y la salida de dicho archivo lo va a realizar en el directorio `./src/app/services` de nuestra aplicación de Angular.

Antes de ejecutar el script, vamos a crear un directorio llamado `/openapi` dentro del directorio `/src` de Angular. Dentro del directorio
`/openapi` agregaremos un archivo json llamado `openapi.json`.

En este archivo `openapi.json` colocaremos lo que `swagger y openAPI` del backend nos muestran:

```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "OpenAPI Specification - Magadiflo",
    "description": "Documentación OpenAPI para Spring Security",
    "termsOfService": "Términos de servicio",
    "contact": {
      "name": "Magadiflo",
      "url": "https://magadiflo.com/courses",
      "email": "contact@magadiflo.com"
    },
    "license": {
      "name": "Nombre de la licencia",
      "url": "https://some-url.com"
    },
    "version": "1.0"
  },
  "servers": [
    {
      "url": "http://localhost:8080",
      "description": "Local ENV"
    },
    {
      "url": "https://magadiflo.com",
      "description": "Prod ENV"
    }
  ],
  "security": [
    {
      "bearerAuth": []
    }
  ],
  "tags": [
    {
      "name": "Feedback",
      "description": "API Rest de la entidad Feedback"
    },
    {
      "name": "Book",
      "description": "API de Book"
    },
    {
      "name": "Authentication",
      "description": "API de autenticación de usuario"
    }
  ],
  "paths": {
    "/api/v1/feedbacks": {
      "post": {
        "tags": [
          "Feedback"
        ],
        "operationId": "saveFeedback",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/FeedbackRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "201": {
            "description": "Created",
            "content": {
              "application/json": {
                "schema": {
                  "type": "integer",
                  "format": "int64"
                }
              }
            }
          }
        }
      }
    },
    ...
    },
    ...
}
```

Listo, ahora sí ejecutamos el script:

```bash
M:\PROGRAMACION\DESARROLLO_JAVA_SPRING\02.youtube\18.bouali_ali\08.full_web_application\book-social-network-01\book-network-frontend (main -> origin)
$ npm run api-gen

> book-network-frontend@0.0.0 api-gen
> ng-openapi-gen --input ./src/openapi/openapi.json --output ./src/app/services

Wrote src\app\services\api-configuration.ts
Wrote src\app\services\api.module.ts
Wrote src\app\services\base-service.ts
Wrote src\app\services\fn\authentication\authenticate.ts
Wrote src\app\services\fn\authentication\confirm.ts
Wrote src\app\services\fn\authentication\register.ts
Wrote src\app\services\fn\book\approved-return-borrow-book.ts
Wrote src\app\services\fn\book\borrow-book.ts
Wrote src\app\services\fn\book\find-all-books-by-owner.ts
Wrote src\app\services\fn\book\find-all-books.ts
Wrote src\app\services\fn\book\find-all-borrowed-books.ts
Wrote src\app\services\fn\book\find-all-returned-books.ts
Wrote src\app\services\fn\book\find-book-by-id.ts
Wrote src\app\services\fn\book\return-borrow-book.ts
Wrote src\app\services\fn\book\save-book.ts
Wrote src\app\services\fn\book\update-archived-status.ts
Wrote src\app\services\fn\book\update-shareable-status.ts
Wrote src\app\services\fn\book\upload-book-cover-picture.ts
Wrote src\app\services\fn\feedback\find-all-feedback-by-book.ts
Wrote src\app\services\fn\feedback\save-feedback.ts
Wrote src\app\services\models\authentication-request.ts
Wrote src\app\services\models\authentication-response.ts
Wrote src\app\services\models\book-request.ts
Wrote src\app\services\models\book-response.ts
Wrote src\app\services\models\borrowed-book-response.ts
Wrote src\app\services\models\feedback-request.ts
Wrote src\app\services\models\feedback-response.ts
Wrote src\app\services\models\page-response-book-response.ts
Wrote src\app\services\models\page-response-borrowed-book-response.ts
Wrote src\app\services\models\page-response-feedback-response.ts
Wrote src\app\services\models\registration-request.ts
Wrote src\app\services\models.ts
Wrote src\app\services\request-builder.ts
Wrote src\app\services\services\authentication.service.ts
Wrote src\app\services\services\book.service.ts
Wrote src\app\services\services\feedback.service.ts
Wrote src\app\services\services.ts
Wrote src\app\services\strict-http-response.ts
Generation from ./src/openapi/openapi.json finished with 11 models and 3 services.
```
Como resultado de la ejecución del script, vemos que se crea el directorio `/services` dentro de `/app` y dentro de él toda la funcionalidad ya implementada del servicio, es decir, los métodos que hacen llamada a los endpoints del backend ya están implementadas.

**Importante**

> Recordemos que estamos dejando en manos de la librería `ng-openapi-gen` la generación de todo el servicio en función de la 
> especificación que obtuvimos de la documentación de `Swagger`.
>
> Ahora, si revisamos los endpoints `/register` y `/activate-account` del backend, veremos que dichos endpoints
> retornan `void`. No he investigado mucho, pero he deducido que retornar ese `void` hace que cuando
> usemos la librería `ng-openapi-gen`, cree la petición esperando que se obtenga un texto, de esta manera:
> `rb.build({ responseType: 'text', accept: '*/*', context })`, esto hace que cuando generemos un error en el endpoint, ya sea
> un error de validación (por ejemplo), la respuesta se obtenga en texto y no en objeto json. 
>
> Entonces, para solucionarlo, es que manualmente tenemos que ir a los archivos que esperan ese texto como respuesta 
> (`confirm.ts` y `register.ts`) y cambiar la respuesta que se generó en texto `rb.build({ responseType: 'text', accept: '*/*', context })` 
> por una respuesta esperada en json, tal como se ve a continuación `rb.build({ responseType: 'json', accept: 'application/json', context })`.
>
> Algo a tener en cuenta, es que si en el backend, en vez de retornar un `ResponseEntity<Void>` (`Void` que es el que nos está generando esta situación),
> retornáramos un `ResponseEntity<?>`, entonces tendríamos más flexibilidad, ya que usando el `?` estaríamos indicando que podemos devolver cualquier 
> tipo de dato, eso haría que al generar la especificación con `OpenAPI` nos genere la petición como un `json` y no como un `text`.
>
> Tengamos en cuenta, que esto ocurre proque estamos usamos una herramienta que nos ayuda a generar el servicio completo a partir
> de la especificación generada por `OpenAPI/Swagger`, pero si nosotros mismos construimos el servicio, no tendríamos que realizar ninguna 
> modificación, ni nada por el estilo, es decir trabajaríamos con el `ResponseEntity<Void>` sin ningún problema.

Finalmente, como estamos trabajando con `Angular 17` necesitaremos utilizar el `HttpClient` para realizar las peticiones al backend, para eso agregaremos la función `provideHttpClient()` en el `app.config.ts`:

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(APP_ROUTES),
    provideHttpClient(),
  ]
};
```

## Implementa el Login Page

Vamos a crear un directorio llamado `/auth` que albergará nuestros componentes de login, register y activate account, además definiremos nuestras rutas para dicho paquete. Empecemos creando el componente `AuthLayoutPageComponent`, este componente servirá como contenedor de los otros componentes.

```typescript
@Component({
  selector: 'auth-layout-page',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './auth-layout-page.component.html',
  styles: ``
})
export class AuthLayoutPageComponent {

}
```

```html
<router-outlet />
```

Ahora sí, creamos nuestro componente `AuthLoginPage` e implementamos la funcionalidad de inicio de sesión:

```typescript

@Component({
  selector: 'auth-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './auth-login-page.component.html',
  styleUrl: './auth-login-page.component.scss'
})
export class AuthLoginPageComponent {

  private _formBuilder = inject(NonNullableFormBuilder);
  private _router = inject(Router);
  private _authenticationService = inject(AuthenticationService);
  private _tokenService = inject(TokenService);

  public errorMessages: string[] = [];
  public form: FormGroup = this._formBuilder.group({
    email: this._formBuilder.control<string>('martin@gmail.com'),
    password: this._formBuilder.control<string>('12345678'),
  });

  public login(): void {
    this.errorMessages = [];
    this._authenticationService.authenticate({ body: this.form.value })
      .subscribe({
        next: response => {
          console.log(response);
          this._tokenService.token = response.token as string;
          this._router.navigate(['/books']);
        },
        error: err => {
          console.log(err);
          if (err.error.validationErrors) {
            this.errorMessages = err.error.validationErrors;
          } else {
            console.log(err.error);
            this.errorMessages.push(err.error.businessErrorDescription);
          }
        }
      });
  }

  public register(): void {
    this._router.navigate(['/auth', 'register']);
  }

}
```

**¡IMPORTANTE!**, notar que en el componente anterior estamos haciendo uso de la clase de servicios que creamos automáticamente con la ayuda de `ng-openapi-gen`.

```html
<div class="container-fluid card auth-container">
  <h3 class="text-center mt-3">Login</h3>
  <hr>
  @if (errorMessages.length) {
  <div class="alert alert-danger" role="alert">
    @for (msg of errorMessages; track $index) {
    <p>{{ msg }}</p>
    }
  </div>
  }
  <form [formGroup]="form">
    <div class="mb-3">
      <label for="email" class="form-label">Email address</label>
      <input type="email" class="form-control" formControlName="email" id="email" placeholder="name@example.com">
    </div>
    <div class="mb-3">
      <label for="password" class="form-label">Password</label>
      <input type="password" class="form-control" formControlName="password" id="password" placeholder="Password">
    </div>
    <div class="d-flex justify-content-between mb-3">
      <button type="button" (click)="login()" class="btn btn-primary">
        <em class="fas fa-sign-in-alt">&nbsp;Sign in</em>
      </button>
      <div>
        ¿No tienes una cuenta?&nbsp;
        <button type="button" (click)="register()" class="btn btn-link">
          <i class="fas fa-sign-in-alt">&nbsp;Register</i>
        </button>
      </div>
    </div>
  </form>
</div>
```

En el archivo de estilos globales `styles.scss` agregamos el siguiente estilo:

```scss
.auth-container {
  max-width: 800px;
  margin-top: 15%;
}
```

Si observamos nuestro componente de Login, podemos ver que está haciendo uso de un servicio llamado `TokenService`. Este servicio nos permitirá almacenar y recuperar el token del `localStorage`. Entonces, crearemos dicho servicio dentro de nuestro paquete de auth: `/auth/services/token.service.ts`.

```typescript
@Injectable({
  providedIn: 'root'
})
export class TokenService {

  public set token(token: string) {
    localStorage.setItem('token', token);
  }

  public get token(): string {
    return localStorage.getItem('token') as string;
  }

}
```

Finalmente, necesitamos crear las rutas con las que va a trabajar nuestros componentes. Para eso crearemos una ruta dentro del paquete `/auth` y configuraremos las rutas del login, register y activate account. Además, agregaremos rutas en el archivo rutas principales `app.routes.ts`:


```typescript
//book-network-frontend\src\app\auth\auth.routes.ts
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
```

```typescript
//book-network-frontend\src\app\app.routes.ts
export const APP_ROUTES: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes'),
  },
  { path: '**', redirectTo: '/auth', },
];
```

## Prueba el Login Page

Validando solicitud desde el backend:

![test login 1](./src/assets/01.test-login-1.png)

Login exitoso, registro de token en el localStorage:

![test login 2](./src/assets/02.test-login-2.png)

> Notar que aunque el login fue exitoso, estamos en la misma página dado que aún no hay otras páginas que hayamos implementado.

## Implementa Register Page

En esta sección crearemos la página para el registro de usuarios.

```typescript
@Component({
  selector: 'app-auth-register-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './auth-register-page.component.html',
  styleUrl: './auth-register-page.component.scss'
})
export default class AuthRegisterPageComponent {

  private _formBuilder = inject(NonNullableFormBuilder);
  private _router = inject(Router);
  private _authenticationService = inject(AuthenticationService);

  public errorMessages: string[] = [];
  public form: FormGroup = this._formBuilder.group({
    email: [''],
    firstName: [''],
    lastName: [''],
    password: [''],
  });

  public login(): void {
    this._router.navigate(['/auth', 'login']);
  }

  public register(): void {
    this.errorMessages = [];
    this._authenticationService.register({ body: this.form.value })
      .subscribe({
        next: () => this._router.navigate(['/auth', 'activate-account']),
        error: err => {
          console.log(err);
          this.errorMessages = err.error.validationErrors;
        },
      });
  }

}
```

Notar que la ruta `/activate-account` aún no lo hemos implementado, eso lo haremos en la siguiente sección.

```html
<div class="container-fluid card auth-container">
  <h3 class="text-center">Register</h3>
  <hr>
  @if (errorMessages.length) {
  <div class="alert alert-danger" role="alert">
    @for (msg of errorMessages; track $index) {
    <p>{{ msg }}</p>
    }
  </div>
  }
  <form [formGroup]="form">
    <div class="mb-3">
      <label for="email" class="form-label">Email address</label>
      <input type="email" class="form-control" formControlName="email" id="email" placeholder="name@example.com">
    </div>
    <div class="mb-3">
      <label for="firstName" class="form-label">First Name</label>
      <input type="text" class="form-control" formControlName="firstName" id="firstName">
    </div>
    <div class="mb-3">
      <label for="lastName" class="form-label">Last Name</label>
      <input type="text" class="form-control" formControlName="lastName" id="lastName">
    </div>
    <div class="mb-3">
      <label for="password" class="form-label">Password</label>
      <input type="password" class="form-control" formControlName="password" id="password" placeholder="Password">
    </div>
    <div class="d-flex justify-content-between mb-3">
      <button type="button" (click)="register()" class="btn btn-primary">
        <i class="fas fa-sign-in-alt">&nbsp;Register</i>
      </button>
      <div>
        ¿Ya tienes una cuenta?&nbsp;
        <button type="button" (click)="login()" class="btn btn-link">
          <em class="fas fa-sign-in-alt">&nbsp;Sign in</em>
        </button>
      </div>
    </div>
  </form>
</div>
```

Finalmente, agregamos la ruta para este nuevo componente:

```typescript
export default [
  {
    path: '',
    component: AuthLayoutPageComponent,
    children: [
      { path: 'login', component: AuthLoginPageComponent, },
      {
        path: 'register',
        loadComponent: () => import('./pages/auth-register-page/auth-register-page.component'),
      },
      { path: '**', redirectTo: 'login', },
    ],
  }
] as Routes;
```

## Prueba el Register Page

Vamos a registrar un usuario y ver la validación que nos realiza el backend:

![test register 1](./src/assets/03.test-register-1.png)

Ahora realizamos un registro con todos los campos correctos. Cuando realizamos el registro, en automático nos redirecciona a la página para activar la cuenta, pero como todavía no lo tenemos implementado nos mostrará el login por defecto. Lo que sí debemos observar es que cuando nos registramos el código de activación llega al correo del usuario.

![test register 2](./src/assets/04.test-register-2.png)

## Implementa el Activate Account Page

Una vez que un usuario se registra es redireccionado a la página de activación de de su cuenta para que ingrese el código de activación que se le envía a su correo. Para acelerar la implementación de esta página, vamos a agregar una dependencia para que nos cree una entrada (input) donde agregaremos el código de activación, aunque lo podríamos hacer de manera manual.

Entonces, necesitamos instalar [Angular Code Input](https://www.npmjs.com/package/angular-code-input).

```bash
$ npm i angular-code-input
```

Una vez instalada la dependencia procedemos a constuir el componente `AuthActivateAccountPage`.

```typescript
@Component({
  selector: 'auth-activate-account-page',
  standalone: true,
  imports: [CodeInputModule],
  templateUrl: './auth-activate-account-page.component.html',
  styleUrl: './auth-activate-account-page.component.scss'
})
export default class AuthActivateAccountPageComponent {

  private _router = inject(Router);
  private _authenticationService = inject(AuthenticationService);

  public message = '';
  public isOk = true;
  public submitted = false;

  public onCodeCompleted(activationCode: string) {
    console.log({ activationCode });
    this._authenticationService.confirm({ token: activationCode })
      .subscribe({
        next: () => {
          this.message = 'Tu cuenta ha sido activada exitosamente.\nAhora puedes proceder a iniciar sesión.';
          this.submitted = true;
          this.isOk = true;
        },
        error: err => {
          console.log(err);
          this.message = err.error.error;
          this.submitted = true;
          this.isOk = false;
        }
      });

  }

  public redirectToLogin() {
    this._router.navigate(['/auth', 'login']);
  }

}
```

**NOTA**, fijémonos que estmos usando la palabra reservada `default` en la definición de la clase, esto lo hacemos porque en el archivo de rutas `auth.routes.ts` utilizamos la siguiente instrucción para cargar este componente:
> `loadComponent: () => import('./pages/auth-activate-account-page/auth-activate-account-page.component')` 

Si no usáramos la palabra `default` en la definición de la clase, entonces tendríamos que importar el componente de esta otra manera:
> `loadComponent: () => import('./pages/auth-activate-account-page/auth-activate-account-page.component').then(c => c.AuthActivateAccountPageComponent)`

```html
@if (submitted) {
<div class="container">
  @if (isOk) {
  <div class="activate-message">
    <h2 class="successful">Activación exitosa</h2>
    <p>{{ message }}</p>
    <button type="button" class="btn btn-primary" (click)="redirectToLogin()">Ir al login</button>
  </div>
  }@else {
  <div class="activate-errro text-center">
    <h2 class="failed text-center">Activación fallida</h2>
    <p class="text-center">{{ message }}</p>
    <button type="button" class="btn btn-primary" (click)="submitted = false">Intentar nuevamente</button>
  </div>
  }
</div>
} @else {
<div class="container">
  <div class="text-center" style="width: 800px;">
    <h2>Ingrese su código de activación</h2>
    <code-input [isCodeHidden]="false" [codeLength]="6" [code]="'number'" (codeCompleted)="onCodeCompleted($event)"
      [className]="'large-text'"></code-input>
  </div>
</div>
}
```

```scss
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.successful {
  color: green;
}

.failed {
  color: red;
}

.large-text {
  font-size: 40px;
}
```

Finalmente, agregamos la ruta correspondiente a este componente en el archivo `auth.routes.ts`:

```typescript
export default [
  {
    path: '',
    component: AuthLayoutPageComponent,
    children: [
      { path: 'login', component: AuthLoginPageComponent, },
      {
        path: 'register',
        loadComponent: () => import('./pages/auth-register-page/auth-register-page.component'),
      },
      {
        path: 'activate-account',
        loadComponent: () => import('./pages/auth-activate-account-page/auth-activate-account-page.component'),
      },
      { path: '**', redirectTo: 'login', },
    ],
  }
] as Routes;
```

## Prueba el Activate Account Page

En la siguiente imagen observamos un código de activación que ha recibido el usuario en su correo. Ese código deberá ser agregada en los input de la página de angular.

![test activate 1](./src/assets/05.test-activate-1.png)

La siguiente imagen muestra lo que se produce cuando ingresamos un código de activación que ha expirado. En la parte derecha observamos que nos llega un mensaje con un nuevo código de activación.

![test activate 2](./src/assets/06.test-activate-2.png)

Ahora utilizamos el nuevo código de activación que se nos envía al correo:

![test activate 3](./src/assets/07.test-activate-3.png)

Como observamos el código fue aceptado, por lo tanto el usuario ahora está habilitado para poder iniciar sesión.

![test activate 4](./src/assets/08.test-activate-4.png)

Ahora intentemos iniciar sesión con el usuario activado. Observamos que efectivamente, estamos recibiendo un token de acceso, eso significa que el usuario está dado de alta correctamente.

![test activate 5](./src/assets/09.test-activate-5.png)

## Crea el paquete de books

Vamos a agrupar toda la funcionalidad de `books` dentro de un paquete con el mismo nombre. Empezaremos creando la ruta hija para books luego modificaremos la ruta principal:

```typescript
//book-network-frontend\src\app\books\books.routes.ts
export default [
  {
    path: '',
    component: BookLayoutPageComponent,
  }
] as Routes;
```

```typescript
//book-network-frontend\src\app\app.routes.ts
export const APP_ROUTES: Routes = [
  // another path
  {
    path: 'books',
    loadChildren: () => import('./books/books.routes'),
  },
  // another path
];
```

Si observamos, la ruta hija, tiene un componente llamado `BookLayoutPageComponent` que hemos creado para poder contener todos los componentes que se creen para esta funcionalidad de books.

```typescript
//book-network-frontend\src\app\books\pages\book-layout-page\book-layout-page.component.ts
@Component({
  selector: 'app-book-layout-page',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './book-layout-page.component.html',
  styles: ``
})
export class BookLayoutPageComponent {

}
```

```html
<div>
  Menú de la aplicación de book
</div>
<main>
  <router-outlet />
</main>
```

## Implementa el MenuComponent

Crearemos un componente que contendrá el menú de nuestra aplicación.

```typescript
//book-network-frontend\src\app\books\components\menu\menu.component.ts
@Component({
  selector: 'books-menu',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent {

  public logout() {
    console.log('logout()...');
  }

}
```
```html
<!--book-network-frontend\src\app\books\components\menu\menu.component.html-->
<nav class="navbar navbar-expand-lg bg-body-tertiary bg-dark" data-bs-theme="dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="javascript:void(0);">BSN</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
      aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link" [routerLink]="['/books']" routerLinkActive="active"
            [routerLinkActiveOptions]="{exact: true}">
            <i class="fas fa-home-alt"></i>&nbsp;Inicio
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" [routerLink]="['/books', 'my-books']" routerLinkActive="active"
            [routerLinkActiveOptions]="{exact: true}">
            <i class="fas fa-book"></i>&nbsp;Mis libros
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" [routerLink]="['/books', 'my-waiting-list']" routerLinkActive="active"
            [routerLinkActiveOptions]="{exact: true}">
            <i class="fas fa-clipboard-list"></i>&nbsp;Mi lista de espera
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" [routerLink]="['/books', 'my-returned-books']" routerLinkActive="active"
            [routerLinkActiveOptions]="{exact: true}">
            <i class="fas fa-retweet"></i>&nbsp;Mis libros devueltos
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" [routerLink]="['/books', 'my-borrowed-books']" routerLinkActive="active"
            [routerLinkActiveOptions]="{exact: true}">
            <i class="fas fa-book-open"></i>&nbsp;Mis libros prestados
          </a>
        </li>
      </ul>
      <form class="d-flex gap-2 align-items-center" role="search">
        <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">
        <button class="btn btn-outline-success" type="submit" aria-label="Start search">
          <i class="fas fa-search"></i>
        </button>
        <span class="text-secondary">Welcome</span>
        <span class="text-capitalize fw-bold text-white">Martín</span>
        <button class="btn btn-link text-danger" type="button" (click)="logout()" aria-label="logout">
          <i class="fas fa-door-open"></i>
        </button>
      </form>
    </div>
  </div>
</nav>
```

Importante observar que estamos haciendo uso de algunas características que nos brinda Angular,  como el uso del `routerLinkActive="active"` quien nos permite agregar la clase `active` cuando estamos en la ruta especificada. Además, estamos haciendo uso del `[routerLinkActiveOptions]="{exact: true}"`, esto nos dice que la ruta debe ser exactamente igual con la ruta que hemos especificado en el router link. Si no usamos esta útlima característica, como todos los menú tienen el `routerLinkActive="active"`, cada vez que vayamos a un link, al menú se le agregará la clase `active` (hasta ahí todo correcto), pero cuando vayamos a otro menú, el menú anterior seguirá con la clase active.


```scss
li>a {
  &:hover {
    background-color: #d6e5f1;
    border-radius: 5px;
    color: #2B3035;
  }
}
```

Finalmente, el componente de menú lo agregamos al layout de books:

```typescript
@Component({
  selector: 'app-book-layout-page',
  standalone: true,
  imports: [RouterOutlet, MenuComponent],
  templateUrl: './book-layout-page.component.html',
  styles: ``
})
export class BookLayoutPageComponent {

}
```

```html
<books-menu />
<main>
  <router-outlet />
</main>
```

## Implementa la página de lista de libros

Creamos el componente para listrar los libros

```typescript

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
        }
      });
  }

}
```

```html
<div class="container mt-4">
  <h3>Lista de libros</h3>
  <hr>
  <div class="d-flex justify-content-start gap-2 flex-wrap">
    @if (bookResponse) {

    } @else {
      <div class="alert alert-info">Recuperando lista de libros...</div>
    }
  </div>
</div>
```

Finalmente, en la ruta de libros, agregamos la ruta a este nuevo componente

```typescript
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
```

## Injecta el Jwt usando un HTTP Interceptor

Una vez que nos hemos logueado, necesitamos agregar el `jwt` a la cabecera de la solicitud cada vez que hagamos una petición al backend. Para eso necesitamos crear un interceptor que será el encargado de agregar a los headers, el token almacenado en el localStorage.

```typescript
export const httpTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const token: string = tokenService.token;

  let reqClone = req;

  if (token) {
    reqClone = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  return next(reqClone);
};
```

Luego debemos agregar el interceptor creado al archivo `app.config.ts`

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(APP_ROUTES),
    provideHttpClient(withInterceptors([httpTokenInterceptor])),
  ]
};
```

## Implementa el componente Book Card 

Crearemos el componente `BookCard` que mostrará los detalles de los libros.

```typescript

@Component({
  selector: 'book-card',
  standalone: true,
  imports: [BookImagePipe],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss'
})
export class BookCardComponent {

  private _book!: BookResponse;
  private _manage = false;

  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  @Input({ required: true })
  public set book(book: BookResponse) {
    this._book = book;
  }

  @Input()
  public set manage(value: boolean) {
    this._manage = value;
  }

  public get book(): BookResponse {
    return this._book;
  }

  public get manage(): boolean {
    return this._manage;
  }

  public onShowDetails(): void {
    this.details.emit(this._book);
  }

  public onBorrow(): void {
    this.borrow.emit(this._book);
  }

  public onAddToWaitingList(): void {
    this.addToWaitingList.emit(this._book);
  }

  public onEdit(): void {
    this.edit.emit(this._book);
  }

  public onShare(): void {
    this.share.emit(this._book);
  }

  public onArchive(): void {
    this.archive.emit(this._book);
  }

}
```

```html
<div class="card" style="width: 18rem;">
  <img height="200" [src]="book | bookImage" class="card-img-top" alt="...">
  <div class="card-body overflow-scroll">
    <h5 class="card-title fs-6 text-nowrap fw-bold mb-1">
      <i class="fa-solid fa-book"></i>&nbsp;{{ book.title }}
    </h5>
    <h5 class="card-subtitle fs-6 text-secondary mb-1">
      <i class="fa-solid fa-user-check"></i>&nbsp;{{ book.authorName }}
    </h5>
    <h6 class="card-subtitle fs-6 text-secondary mb-1">
      <i class="fas fa-code"></i>&nbsp;{{ book.isbn }}
    </h6>
    <h6 class="card-subtitle fs-6 text-secondary">
      <i class="fas fa-user"></i>&nbsp;{{ book.owner }}
    </h6>
    <hr>
    <p class="card-text">{{ book.synopsis }}</p>
    <a href="#" class="btn btn-primary">Go somewhere</a>
  </div>
  <div class="card-footer d-flex gap-2 justify-content-between align-items-center">
    <div class="d-flex gap-2">
      5 starts
    </div>
    @if (!manage) {
    <div class="d-flex gap-2">
      <i (click)="onShowDetails()" class="fas fa-circle-info text-primary"></i>
      <i (click)="onBorrow()" class="fas fa-list-check text-primary"></i>
      <i (click)="onAddToWaitingList()" class="fas fa-heart text-danger"></i>
    </div>
    } @else {
    <div class="d-flex gap-2">
      <i (click)="onEdit()" class="fas fa-edit text-success"></i>
      <i (click)="onShare()" class="fas fa-share-nodes text-primary"></i>
      <i (click)="onArchive()" class="fas fa-archive text-danger"></i>
    </div>
    }
  </div>
</div>
```

```scss
div {
  &.card {
    max-height: 450px;
    min-height: 450px;
  }
}

i {
  cursor: pointer;
}
```

Si observamos el componente de html de nuestro card de book, veremos que estamos haciendo uso de un pipe `bookImage`. Debemos crear dicho pipe para mostrar la imagen del libro o mostrar por defecto una imagen si es que el libro aún no tienen una imagen asignada.

```typescript

@Pipe({
  name: 'bookImage',
  standalone: true
})
export class BookImagePipe implements PipeTransform {

  transform(book: BookResponse): string {
    return !!book.cover ? `data:image/jpg;base64,${book.cover}` : './assets/books/no_image_available.svg';
  }
}
```

Finalmente, una vez que hayamos construido nuestro card de book, debemos usarlo en el componente `BookList`.

```typescript
@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {

  /* code */

}
```
```html
<div class="container mt-4">
  <h3>Lista de libros</h3>
  <hr>
  <div class="d-flex justify-content-start gap-2 flex-wrap">
    @if (bookResponse) {
    @for (book of bookResponse.content; track $index) {
    <book-card [book]="book" />
    }
    } @else {
    <div class="alert alert-info">Recuperando lista de libros...</div>
    }
  </div>
</div>
```

## Implementa el RatingComponent

En el apartado anterior dejamos escrito `5 starts` en el componente BookCard. En este apartado crearemos un componente que nos permitirá mostrar gráficamente las 5 estrellas coloreadas en función del feedback que otorgen los usuarios.

```typescript
@Component({
  selector: 'rating',
  standalone: true,
  imports: [],
  templateUrl: './rating.component.html',
  styleUrl: './rating.component.scss'
})
export class RatingComponent {

  @Input() rating: number = 0;
  public maxRating: number = 5;

  public get fullStarts(): number {
    return Math.floor(this.rating);
  }

  public get hasHalfStart(): boolean {
    return this.rating % 1 !== 0;
  }

  public get emptyStarts(): number {
    return this.maxRating - Math.ceil(this.rating);
  }
}
```
```html
<div class="rating">
  @for (_ of [].constructor(fullStarts); track $index) {
  <i class="fas fa-star text-warning"></i>
  }
  @if(hasHalfStart) {
  <i class="fas fa-star-half-alt text-warning"></i>
  }
  @for (_ of [].constructor(emptyStarts); track $index) {
  <i class="fas fa-star"></i>
  }
</div>
```

Ahora, en el componente `BookCardComponent` agregamos nuestro componente de rating:

```typescript
@Component({
  selector: 'book-card',
  standalone: true,
  imports: [RatingComponent, BookImagePipe],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss'
})
export class BookCardComponent {
  /* code */
}
```

Finalmente, en su elemento html agregamos el componente de rating y valor del mismo:

```html
<div class="d-flex gap-2">
  <rating [rating]="book.rate || 0"/>
  @if(book.rate || 0 > 0) {
    <span class="fw-bold">
      {{ book.rate }}
    </span>
  }
</div>
```
## Implementa la paginación

Implementaremos la paginación utilizando el mismo objeto que nos retorna desde el backend.

```typescript

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
```

```html
<div class="container mt-4">
  <h3>Lista de libros</h3>
  <hr>
  <div class="d-flex justify-content-start gap-2 flex-wrap">
    @if (bookResponse) {
    @for (book of bookResponse.content; track $index) {
    <book-card [book]="book" />
    }
    } @else {
    <div class="alert alert-info">Recuperando lista de libros...</div>
    }
  </div>
  @if (bookResponse) {
  <div class="d-flex justify-content-center mt-3">
    <nav aria-label="Page navigation example">
      <ul class="pagination">
        <li class="page-item" [class.disabled]="bookResponse.first">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToFirstPage();" aria-label="Previous">
            <i class="fa-solid fa-angles-left"></i>
          </a>
        </li>
        <li class="page-item" [class.disabled]="bookResponse.first">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToPreviousPage();" aria-label="Previous">
            <i class="fa-solid fa-angle-left"></i>
          </a>
        </li>

        @for (page of [].constructor(bookResponse.totalPages); track $index) {
        <li class="page-item" [class.active]="bookResponse.number == $index">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToPage($index);">{{ $index + 1 }}</a>
        </li>
        }

        <li class="page-item" [class.disabled]="bookResponse.last">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToNextPage();" aria-label="Next">
            <i class="fa-solid fa-angle-right"></i>
          </a>
        </li>
        <li class="page-item" [class.disabled]="bookResponse.last">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToLastPage();" aria-label="Next">
            <i class="fa-solid fa-angles-right"></i>
          </a>
        </li>
      </ul>
    </nav>
  </div>
  }
</div>
```

## Implementa la acción de pedir prestado

Cada vez que demos click en el botón del card, el de pedir prestado, se enviará la solicitud al backend para que podamos prestar el libro. Dependiendo de si el libro aún no ha sido prestado lanzaremos un mensaje de confirmación.

```typescript

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {

  /* other properties*/

  public message = '';
  public level = 'success';

  /* other methods */

  public borrowBook(book: BookResponse) {
    this.message = '';
    this._bookService.borrowBook({ 'bookId': book.id! })
      .subscribe({
        next: transactionHistoryId => {
          console.log({ transactionHistoryId });
          this.level = 'success';
          this.message = 'El libro se ha agregado correctamente a tu lista';
        },
        error: err => {
          console.log(err);
          this.level = 'error';
          this.message = err.error.error;
        }
      });
  }

}
```

```html
<div class="container mt-4">
  <h3>Lista de libros</h3>
  @if (message) {
  <div class="alert" [class.alert-success]="level == 'success'" [class.alert-danger]="level == 'error'">
    {{ message }}
  </div>
  }
  <hr>
  <div class="d-flex justify-content-start gap-2 flex-wrap">
    @if (bookResponse) {
    @for (book of bookResponse.content; track $index) {
    <book-card [book]="book" (borrow)="borrowBook($event)" />
    }
    } @else {
    <div class="alert alert-info">Recuperando lista de libros...</div>
    }
  </div>
  <!-- Etiquetas de la paginación -->
</div>
```
## Implementa la página my-book

La implementación de esta página será similar a la de la lista de libros. La diferencia es que aquí llamaremos a otro endpoint que nos traerá los libros que son propios del usuario logueado.

```typescript

@Component({
  selector: 'app-my-books',
  standalone: true,
  imports: [BookCardComponent, RouterLink],
  templateUrl: './my-books.component.html',
  styleUrl: './my-books.component.scss'
})
export default class MyBooksComponent implements OnInit {

  private _bookService = inject(BookService);

  public bookResponse?: PageResponseBookResponse;
  public page = 0;
  public size = 4;

  ngOnInit(): void {
    this.findAllBooks();
  }

  public findAllBooks() {
    this._bookService.findAllBooksByOwner({ page: this.page, size: this.size })
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

  public archiveBook(book: BookResponse) {

  }

  public shareBook(book: BookResponse) {

  }

  public editBook(book: BookResponse) {

  }

}
```

```html
<div class="container mt-4">
  <h3>Mi lista de libros</h3>
  <hr>
  <div class="d-flex justify-content-end mb-3">
    <a [routerLink]="['/books', 'manage']" class="btn btn-outline-primary">
      <i class="fas fa-plus"></i>&nbsp; Nuevo libro
    </a>
  </div>
  <div class="d-flex justify-content-start gap-2 flex-wrap">
    @if (bookResponse) {
    @for (book of bookResponse.content; track $index) {
    <book-card [book]="book" [manage]="true" (archive)="archiveBook($event)" (share)="shareBook($event)"
      (edit)="editBook($event)" />
    } @empty {
    <div class="alert alert-warning">
      Aún no hay libros en su lista.
    </div>
    }
    } @else {
    <div class="alert alert-info">Recuperando lista de libros...</div>
    }
  </div>
  @if (bookResponse) {
  <div class="d-flex justify-content-center mt-3">
    <nav aria-label="Page navigation example">
      <ul class="pagination">
        <li class="page-item" [class.disabled]="bookResponse.first">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToFirstPage();" aria-label="Previous">
            <i class="fa-solid fa-angles-left"></i>
          </a>
        </li>
        <li class="page-item" [class.disabled]="bookResponse.first">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToPreviousPage();" aria-label="Previous">
            <i class="fa-solid fa-angle-left"></i>
          </a>
        </li>

        @for (page of [].constructor(bookResponse.totalPages); track $index) {
        <li class="page-item" [class.active]="bookResponse.number == $index">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToPage($index);">{{ $index + 1 }}</a>
        </li>
        }

        <li class="page-item" [class.disabled]="bookResponse.last">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToNextPage();" aria-label="Next">
            <i class="fa-solid fa-angle-right"></i>
          </a>
        </li>
        <li class="page-item" [class.disabled]="bookResponse.last">
          <a class="page-link" href="#" (click)="$event.preventDefault();goToLastPage();" aria-label="Next">
            <i class="fa-solid fa-angles-right"></i>
          </a>
        </li>
      </ul>
    </nav>
  </div>
  }
</div>
```

## Implementa el método crear libro


Vamos a crear un componente donde estará nuestro formulario para crear libros, además irá acompañada de de la funcionalidad para agregar una imagen al libro.

```typescript
//book-network-frontend\src\app\books\pages\manage-book\manage-book.component.ts
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
```
```html
<div class="container p-2">
  <h2>Administrar mi libro</h2>
  <hr>
  @if (errorMessages.length) {
  <div class="alert alert-danger mt-2" role="alert">
    @for (message of errorMessages; track $index) {
    <p class="p-0 m-0">{{ message }}</p>
    }
  </div>
  }
  <div class="d-flex gap-2">
    <div class="col-3">
      <img [src]="imagePreview || './assets/books/no_image_available.svg'" class="rounded-1" width="100%" height="100%">
      <div class="mt-2">
        <input type="file" (change)="onFileSelected($event)" accept="image/*" class="form-control" id="formFile">
      </div>
    </div>
    <div class="col-9">
      <form [formGroup]="form" class="row g-3" autocomplete="off">
        <div class="col-12">
          <label for="title" class="form-label">Título del libro</label>
          <input type="text" class="form-control" id="title" formControlName="title">
        </div>
        <div class="col-6">
          <label for="authorName" class="form-label">Nombre del autor</label>
          <input type="text" class="form-control" id="authorName" formControlName="authorName">
        </div>
        <div class="col-6">
          <label for="isbn" class="form-label">ISBN</label>
          <input type="text" class="form-control" id="isbn" formControlName="isbn">
        </div>
        <div class="col-12">
          <label for="synopsis" class="form-label">Sinopsis</label>
          <textarea id="synopsis" class="form-control" rows="4" formControlName="synopsis"></textarea>
        </div>
        <div class="col-12 form-check">
          <input type="checkbox" class="form-check-input" id="shareable" formControlName="shareable">
          <label class="form-check-label" for="shareable">Compárteme</label>
        </div>
        <div class="d-flex justify-content-end gap-2 col-12">
          <button type="button" (click)="saveBook()" class="btn btn-outline-primary">
            <i class="fas fa-save"></i>&nbsp; Guardar
          </button>
          <a [routerLink]="['/books', 'my-books']" class="btn btn-link text-danger">
            <i class="fas fa-times"></i>&nbsp;Cancelar
          </a>
        </div>
      </form>
    </div>
  </div>
</div>
```
Luego, debemos agregar una nueva ruta para nuestro componente `ManageBookComponent`:

```typescript
//book-network-frontend\src\app\books\books.routes.ts
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
      {
        path: 'manage',
        loadComponent: () => import('./pages/manage-book/manage-book.component')
      },
      {
        path: 'manage/:bookId',
        loadComponent: () => import('./pages/manage-book/manage-book.component')
      },
      { path: '**', redirectTo: '', },
    ],
  }
] as Routes;
```

Notar que hemos agregado dos rutas `manage`, una para guardar un libro y la otra para editar.
