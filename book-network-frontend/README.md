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
