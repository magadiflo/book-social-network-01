# Backend (Spring Boot)

---

## Dependencias

````xml
<!--Spring Boot 3.2.6-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!--INICIO: Dependencias agregadas manualmente-->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>

    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>
    <!--FIN: Dependencias agregadas manualmente-->

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````

**Nota 01**  
Al agregar la dependencia de `Thymeleaf` junto a la dependencia de `Spring Security` nos agrega automáticamente la
dependencia que vemos en la parte inferior. Nosotros la eliminaremos, dado que usaremos únicamente `Thymeleaf` como un
motor de plantilla html para diseñar nuestra interfaz de email y no lo usaremos como elemento principal para desarrollar
nuestro frontend. Recordemos que el frontend lo trabajaremos con Angular.

````xml
<!--Elimina esta dependencia que se agrega automáticamente-->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
````

**Nota 02**  
Observar que hemos agregado 4 dependencias manuales que están delimitadas por comentarios. Las dependencias de
`io.jsonwebtoken` nos permitirán trabajar con `JWT`, mientras que la dependencia de `org.springdoc` nos permitirá
trabajar con `Swagger` para la documentación de nuestras apis.

## Descripción general

A continuación se muestra a modo de `diagrama de clase` lo que se pretende construir en este proyecto de backend.

![01.class-diagram.png](assets/01.class-diagram.png)

## Preparando infraestructura con Docker Compose

En este proyecto trabajaremos con docker. Crearemos el contenedor de `postgres` y de una aplicación que nos permitirá
probar los emails en la etapa de desarrollo `(mail-dev)`. Como vamos a trabajar con docker, nos vamos a apoyar
de `docker compose` para poder ejecutar fácilmente los contendores.

Creamos el archivo `compose.yml` en el directorio raíz de este repositorio y agregamos los siguientes servicios:

````yml
services:
  postgres:
    image: postgres:15.2-alpine
    container_name: c-postgres-bsn
    restart: unless-stopped
    environment:
      POSTGRES_DB: db_book_social_network
      POSTGRES_USER: magadiflo
      POSTGRES_PASSWORD: magadiflo
    ports:
      - 5435:5432
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - spring-net

  mail-dev:
    image: maildev/maildev
    container_name: c-mail-dev-bsn
    restart: unless-stopped
    ports:
      - 1080:1080
      - 1025:1025
    networks:
      - spring-net

volumes:
  postgres_data:
    name: postgres_data

networks:
  spring-net:
    name: spring-net
````

De la configuración anterior, podemos observar que estamos creando la base de datos de postgres con un volumen con
nombre llamado `postgres_data`, eso significa que si se elimina el contenedor, los datos almacenados en la base de
datos se mantendrán persistidos.

Algo distinto a lo que siempre he venido trabajando es el uso de la imagen
[maildev/maildev](https://github.com/maildev/maildev).

> "`MailDev` es una forma sencilla de probar el correo electrónico generado por su proyecto durante el desarrollo, con
> una interfaz web fácil de usar que se ejecuta en su máquina construida sobre Node.js."

En nuestro caso, vamos a aprovechar que estamos trabajando con `docker` para crear un contenedor de `MailDev` por esa
razón es que en el archivo `compose.yml` hemos creado el servicio `mail-dev` junto a las configuraciones requeridas
para ejecutarlo desde `docker`.

## Ejecutando docker compose

Luego de haber agregado los dos servicios en nuestro `compose.yml`, vamos a ejecutarlo para verificar que se crean
correctamente:

````bash
M:\PROGRAMACION\DESARROLLO_JAVA_SPRING\02.youtube\18.bouali_ali\08.full_web_application\book-social-network-01 (main -> origin)
$ docker compose up -d

[+] Running 4/4                      
 ✔ Network spring-net        Created 
 ✔ Volume "postgres_data"    Created 
 ✔ Container c-mail-dev-bsn  Started 
 ✔ Container c-postgres-bsn  Started 
````

Verificamos que los contenedores, el volumen y la red se hayan creado correctamente:

````bash
$ docker container ls -a
CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS                            PORTS                                            NAMES
31df57141df9   postgres:15.2-alpine   "docker-entrypoint.s…"   10 seconds ago   Up 9 seconds                      0.0.0.0:5435->5432/tcp                           c-postgres-bsn
77398ada4676   maildev/maildev        "bin/maildev"            10 seconds ago   Up 9 seconds (health: starting)   0.0.0.0:1025->1025/tcp, 0.0.0.0:1080->1080/tcp   c-mail-dev-bsn

$ docker volume ls
DRIVER    VOLUME NAME
local     postgres_data

$ docker network ls
NETWORK ID     NAME         DRIVER    SCOPE
8f1eee366dbc   bridge       bridge    local
6dac92048c81   host         host      local
4eea7e69fe4f   none         null      local
67fe3511e814   spring-net   bridge    local
````

Verificamos que la base de datos se haya creado correctamente, así que ingresamos al contenedor `c-postgres-bsn` y
luego a la base de datos:

````bash
$ docker container exec -it c-postgres-bsn /bin/sh
/ # psql -U magadiflo -d db_book_social_network
psql (15.2)
Type "help" for help.

db_book_social_network=# \dt
Did not find any relations.
db_book_social_network=#
````

Finalmente, observemos que el contenedor `c-mail-dev-bsn` tiene dos puertos definidos. El puerto `1025`, es el que nos
permitirá enviar correos desde el backend, mientras que el puerto `1080` es el que nos permitirá acceder a la aplicación
web del servidor de correo:

![02.mail-dev-web.png](assets/02.mail-dev-web.png)

## Configurando propiedades de la aplicación

Toda aplicación de Spring Boot tiene el `perfil por default` que es representado por el archivo `application.yml`. En
nuestro caso, además de agregar configuraciones globales en el archivo `application.yml` crearemos un nuevo perfil para
desarrollo que estará representado por el archivo `application-dev.yml`.

**NOTA**
> Es importante observar que el perfil que creemos debe tener la siguiente estructura
> `application-<NOMBRE_DEL_PERFIL>.yml` y a continuación el nombre del perfil.

A continuación crearemos las configuraciones del perfil por default `application.yml`:

````yml
server:
  port: 8080
  error:
    include-message: always

spring:
  application:
    name: book-network-backend

  profiles:
    active: dev

  servlet:
    multipart:
      max-file-size: 50MB

springdoc:
  default-produces-media-type: application/json
````

De las configuraciones anteriores podemos observar que el perfil que estamos seleccionando será `dev`, es decir,
cuando la aplicación de Spring Boot se ejecute con el `perfil activo dev` (configurado mediante la propiedad
`spring.profiles.active en application.yml`), buscará un archivo llamado `application-dev.yml` y usará sus
configuraciones. Luego, utilizará las configuraciones que estén en el perfil por defecto `(application.yml)`. Esto
significa que las configuraciones que existan en el perfil seleccionado `(application-dev.yml)` sobrescribirán
las configuraciones que se repitan en el perfil por defecto `(application.yml)`.

El siguiente paso es agregar las configuraciones al perfil seleccionado. Para eso necesitamos crear dicho archivo, al
cual le llamaremos `application-dev.yml`:

````yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5435/db_book_social_network
    username: magadiflo
    password: magadiflo

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true

  mail:
    host: localhost
    port: 1025
    username: magadiflo
    password: magadiflo
    default-encoding: UTF-8
    properties:
      mail:
        mime:
          charset: UTF-8
        smtp:
          trust: '*'
          connectiontimeout: 5000
          timeout: 3000
          writetimeout: 5000
          auth: true
          starttls:
            enable: true

logging:
  level:
    org.hibernate.SQL: DEBUG
````

De las configuraciones anteriores podemos hablar del `connectiontimeout`, `timeout` y `writetimeout`. Según la
documentación ([Sending Email](https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-email.html)),
ciertos valores de tiempo de espera (timeout) predeterminados son infinitos y es posible que desee cambiarlos para
evitar que un servidor de correo que no responde bloquee un hilo.

## Spring Security Overview

Cuando estamos desarrollando una aplicación desde cero, lo primero que debemos considerar configurar debe ser la
seguridad, porque por ejemplo, necesitaríamos conseguir al usuario o tomar la información del usuario conectado para
hacer algo al respecto, por ejemplo, auditoría o para saber quién está haciendo tal cosa, cuáles son sus permisos, sus
roles, etc. es por eso que esto es muy importante para evitar cualquier tipo de cambio y que estos se vuelvan costosos
en el futuro. Así que primero debemos asegurar nuestra aplicación y luego podemos continuar implementando las diferentes
características o funcionalidades.

Primero que todo debemos entender cómo es que funciona la seguridad con Spring Security, para eso se muestra la
siguiente imagen:

![03.spring-security-overview.png](assets/03.spring-security-overview.png)

- Cuando se haga una solicitud HTTP: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, el primer elemento que entrará en juego
  será el conjunto de filtros `FilterChain`, quien interceptará la solicitud a través del conjunto de cadena de filtros
  que tengamos en nuestra aplicación.


- Recordemos que en nuestra aplicación de Spring Boot tenemos filtros que ya vienen por defecto y se encuentran en el
  conjunto de filtros `FilterChain`. Ahora, para implementar la seguridad, es necesario crear nuestro propio filtro. El
  filtro que crearemos se llamará `JwtAuthenticationFilter` y extenderá de `OncePerRequestFilter`.


- Nuestro filtro personalizado `JwtAuthenticationFilter` verificará si el token existe. Si el token existe utilizará
  al `JwtService` para validarlo; en caso contrario, el request la pasará al siguiente filtro de la cadena de filtros
  donde se realizarán acciones propias de los filtros a los que se pase la solicitud.


- Si el `JwtService` determina que el `jwt` es un token inválido, entonces lanzará una
  excepción `TokenInvalidException`; en caso contrario, si el token es correcto, se extraerá detalles del usuario a
  partir del token como el `username`. Con el `username` extraído se utilizará el `UserDetailsService` para recuperar al
  usuario de la base de datos de postgres.


- Si el usuario no existe, se lanza la excepción `UserNotFoundException`, caso contrario, actualizamos
  el `SecurityContextHolder` con los detalles del usuario autenticado.


- Una vez que el `SecurityContextHolder` está actualizado con los detalles del usuario autenticado, pasamos
  al `DispatcherServlet`. El `DispatcherServlet` basado en la url que ha sido invocado trata de determinar a
  qué `controller` enviar la solicitud http.


- Finalmente, luego de que el controlador realice lo que tenga que realizar, retorna la respuesta al usuario final.

