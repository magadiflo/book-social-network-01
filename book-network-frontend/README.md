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
            "url": "http://localhost:8080/api/v1",
            "description": "Local ENV"
        },
        {
            "url": "https://magadiflo.com/courses",
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
                    "200": {
                        "description": "OK",
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

Finalmente, como estamos trabajando con `Angular 17` necesitaremos utilizar el `HttpClient` para realizar las peticiones al backend, para eso agregaremos la función `provideHttpClient()` en el `app.config.ts`:

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(APP_ROUTES),
    provideHttpClient(),
  ]
};
```
