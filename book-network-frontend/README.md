# Frontend (Angular)

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 17.3.5.

---

## Agrega dependencia de Bootstrap y fontawesome

Luego crear el proyecto, se agregaron dos dependencias adicionales `bootstrap` y `fontawesome`.

```bash
$ npm i bootstrap@5.3.3
$ npm i @fortawesome/fontawesome-free
```

Luego, en la hoja de estilos globales `styles.scss` se hizo referencia a estas dos dependencias:

```scss
@import 'bootstrap/dist/css/bootstrap.min.css';
@import '@fortawesome/fontawesome-free/css/all.min.css';
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
