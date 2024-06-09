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

## Crea entidad User

A continuación se crea el usuario con el que se va a trabajar en la aplicación. Esta clase de usuario va a implementar
dos interfaces propios de `Spring Security (UserDetails y Principal)`, además vamos a hacer uso de la auditoría que nos
proporciona JPA:

````java

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)  // Permitirá la realización de la auditoria por jpa
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String email; // Deben ser un valor único que identifique al usuario

    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @Override
    public String getName() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public String fullName() { // Creamos este método para evitar realizar concatenaciones. Aquí ya tenemos el método preparado.
        return "%s %s".formatted(this.firstName, this.lastName);
    }
}
````

Nuestra clase `User` implementa la interfaz `UserDetails` y la interfaz `Principal`. Para `Spring Security`, una
definición de Usuario debe respetar el contrato `UserDetails`, es decir, para Spring Security un usuario que va a
interactuar en su arquitectura de seguridad es un usuario que debe tener implementada la interfaz `UserDetails`.

La interfaz `UserDetails` representa al usuario tal como lo entiende `Spring Security`. La clase de tu aplicación que
describe al usuario tiene que implementar esta interfaz, de esta forma el framework lo entenderá.

A continuación, se muestra la definición de la interfaz `UserDetails` que viene en Spring Security:

````java
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
````

También nuestra clase `User` implementa la interfaz `Principal`. El usuario que solicita acceso a la aplicación se
denomina `Principal`. Esta interfaz representa la noción abstracta de Principal, que se puede utilizar para representar
cualquier entidad, como un individuo, una corporación y una identificación de inicio de sesión.

A continuación se muestra la definición de la intefaz `Principal` que viene en con Spring Security:

````java
public interface Principal {
    String getName();
}
````

## Auditoría de la entidad User

Nuestra clase de entidad User tiene anotaciones que le permiten a JPA hacer auditoría, es decir, le permite dar
seguimiento cuando un usuario ha sido creado o actualizado. A continuación se muestran las anotaciones de auditoría a
las que me refiero:

````java
/* other annotations */
@EntityListeners(AuditingEntityListener.class)  // Permitirá la realización de la auditoria por jpa
public class User implements UserDetails, Principal {

    /* other properties */

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    /* other methods */
}
````

La anotación `@EntityListeners` en Spring Data JPA se utiliza para especificar clases que deben ser notificadas cuando
se realizan cambios en las entidades gestionadas por JPA. En nuestro caso, tenemos la
clase `AuditingEntityListener.class`, lo que sugiere que estamos utilizando esta anotación para habilitar la
funcionalidad de auditoría en tu aplicación.

Cuando se usa junto con la funcionalidad de auditoría, `@EntityListeners` **permite registrar cambios en las entidades,
como cuándo fueron creadas o modificadas por última vez, quién las creó o modificó**, etc. Esto es útil para mantener un
registro de la actividad en la base de datos y para auditar los cambios realizados en las entidades.

La clase `AuditingEntityListener.class` es una implementación proporcionada por Spring Data JPA para manejar la
auditoría de entidades. Por lo general, se combina con otras anotaciones,
como `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, y `@LastModifiedBy`, para registrar información sobre cuándo se
creó o modificó una entidad y quién lo hizo.

`@CreatedDate:` esta anotación se utiliza para marcar un campo en tu entidad que representa la fecha y hora en que se
creó la instancia de la entidad. En tu caso, tienes el atributo createdDate anotado con `@CreatedDate`. Al marcar este
campo con `@CreatedDate`, **Spring Data JPA automáticamente asignará la fecha y hora actual al momento de persistir la
entidad en la base de datos por primera vez.** La anotación `@Column(nullable = false, updatable = false)` junto
con `@CreatedDate` indica que **este campo no puede ser nulo y no debe ser actualizable una vez que se ha creado la
entidad.**

`@LastModifiedDate:` esta anotación se utiliza para marcar un campo que representa la fecha y hora de la última
modificación de la entidad. En nuestro caso, tienes el atributo `lastModifiedDate` anotado con `@LastModifiedDate`.
**Cuando se modifica la entidad y se actualiza en la base de datos, Spring Data JPA actualizará automáticamente este
campo con la fecha y hora actual**. La anotación `@Column(insertable = false)` junto con `@LastModifiedDate` indica que
este campo no se incluirá al insertar una nueva entidad en la base de datos, **solo se actualizará cuando la entidad se
modifique.**

Finalmente, necesitamos agregar una anotación `@EnableJpaAuditing` a una clase de configuración, en mi caso a la clase
principal, **para poder habilitar la auditoría en JPA mediante la configuración de anotaciones.**

````java

@EnableJpaAuditing //<--- Habilitando el uso de anotaciones para auditoría JPA
@SpringBootApplication
public class BookNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkBackendApplication.class, args);
    }

}
````

## Crea la entidad Role

Como vamos a trabajar con `Spring Security` necesitamos que nuestra entidad `User` tenga roles definidos para otorgarle
permisos dentro de la aplicación. En ese sentido, crearemos la entidad `Role` y realizaremos la asociación bidireccional
hacia la entidad `User`:

````java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
}
````

Notar que la entidad Role está utilizando las mismas anotaciones de auditoría que usamos en la entidad User. Además,
estamos estableciendo la relación `@ManyToMany` con la entidad `User`. En esta asociación, adicionalmente agregamos
la anotación `@JsonIgnore`, dado que cuando la entidad `User` llame a la entidad `Role`, el `Role` ya no vuelva a
llamar a la entidad `User`, de esta manera evitamos una llamada cíclica.

## Viendo a detalle la relación de User con Role (bidireccional)

En la entidad `Role` vimos que usamos la anotación `@ManyToMany` para establecer la asociación con la entidad `User`.
En este apartado haremos lo mismo con la entidad `User`:

````java

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    /* other properties */

    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @Override
    public String getName() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    /* other methods */
}
````

Como observamos, estamos definiendo una asociación bidireccional entre User y Role, es por eso que en esta entidad
User también definimos la anotación `@ManyToMany`. Además, he agregado la anotación `@JoinTable` para personalizar
la tabla intermedia que se genera automáticamente de la relación manyToMany.

Observemos que he definido el conjunto de columnas `user_id` y `role_id` como únicos, dado que el usuario no debe
repetir el mismo tipo de rol múltiples veces. Además, es importante observar que hemos implementado correctamente
el método `getAuthorities()` a partir de la lista de roles.

## Implementa User y Role Repository

````java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
````

````java
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
````

## Crea la entidad Token y su repositorio

Vamos a crear una entidad llamada `Token`, dado que cuando un usuario se registre en nuestra aplicación recibirá por
correo un `código de 6 dígitos`. Entonces, necesitamos de alguna manera almacenar el código que se emitió al usuario
que se acaba de registrar junto a algunos atributos adicionales para llevar el control de dicho código, como cuándo se
emitió, cuándo expira, etc.

A continuación se muestra la entidad `Token` estableciendo una `ASOCIACIÓN UNIDIRECCIONAL` con la entidad `User`.

````java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
````

Finalmente, la entidad Token tiene su respectivo repositorio, donde definiremos un método personalizado para consultar
al token a partir de su valor:

````java
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}
````

## Tablas generadas a partir de las entidades: User, Role y Token

Si hasta este punto ejecutamos la aplicación, veremos que en la base de datos se crean las siguientes tablas producto
de las distintas asociaciones que hemos realizado.

![04.tables.png](assets/04.tables.png)

## Implementa clases de configuración de Spring Security

A continuación creamos la clase de configuración para personalizar la seguridad con Spring Security:

````java
/**
 * La propiedad prePostEnabled habilita las anotaciones pre/post de Spring Security (default true).
 * La propiedad securedEnabled determina si la anotación @Secured debería ser habilitada (default false).
 * La propiedad jsr250Enabled nos permite usar la anotación @RolesAllowed (default false).
 */
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
````

**DONDE**

- `@EnableWebSecurity`, **nos permite personalizar la configuración de seguridad de la aplicación.** Agregue esta
  anotación a una clase `@Configuration` para tener la configuración de **Spring Security** definida en cualquier
  `WebSecurityConfigurer` o, más probablemente, exponiendo un bean `SecurityFilterChain`.


- `@EnableMethodSecurity(securedEnabled = true)`, **esta anotación nos permite habilitar el uso de otras anotaciones.**
  Por defecto el atributo `prePostEnabled` está en `true`, quien nos permite habilitar el uso de la
  anotación `@PreAuthorize` a nivel de método. Por otro lado, el atributo `securedEnabled` por defecto está en `false`,
  así que explícitamente lo establecemos en `true`; este atributo nos permite habilitar el uso de la
  anotación `@Secured`.


- En el método `securityFilterChain()` vemos un conjunto de paths al que le estamos danto todos los permisos. Estos
  paths representan (con excepción del primer path `/api/v1/auth/**`) las rutas utilizadas por `Swagger` y `OpenAPI`.
  Se colocan la `v2` por si en algún momento se llegase a usar una versión antigua de swagger, y la `v3` por si se usa
  la versión actual.

  ````bash
  .requestMatchers(
      "/api/v1/auth/**",
      "/v2/api-docs",
      "/v3/api-docs",
      "/v3/api-docs/**",
      "/swagger-resources",
      "/swagger-resources/**",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui/**",
      "/webjars/**",
      "/swagger-ui.html").permitAll()
  ````

  Con respecto al primer path `/api/v1/auth/**`, más adelante crearemos un `RestController` al que le definiremos dicho
  path. Este controlador tendrá endpoints referidos al `login`, `register`, `account validation`, etc.; estos endpoints
  deben estar libres de restricciones para su acceso dado que son endpoints con los que se van a iniciar los procesos de
  autenticación, registro, etc.

- `.anyRequest().authenticated()`, cualquier otro path que no se haya definido antes, tendrá que autenticarse.


- `.addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`, esta línea nos dice que antes de
  que se ejecute el filtro `UsernamePasswordAuthenticationFilter` debe ejecutarse nuestro filtro `JwtAuthFilter` quien
  está siendo inyectado vía constructor (más adelante lo implementaremos).

**NOTA: Sobre el AuthenticationProvider**

> En el tutorial define dentro del método de configuración `securityFilterChain()` un
> `.authenticationProvider(this.authenticationProvider)` quien es inyectado vía constructor.
>
> En mi caso no lo voy a definir, dado que `Spring Security` define un `authenticationProvider` por defecto, me refiero
> a la clase `DaoAuthenticationProvider` que es un subTipo de la interfaz `AuthenticationProvider`.
>
> La configuración predeterminada en `Spring Boot` establece el `DaoAuthenticationProvider` como el **proveedor de
> autenticación principal**. Sin embargo, es posible personalizar esta configuración y utilizar diferentes
> implementaciones del `AuthenticationProvider` según tus necesidades específicas.
>
> En ese sentido, lo único que requerimos es definir un `PasswordEncoder` y un `UserDetailsService` que son elementos
> importantes para que la clase `DaoAuthenticationProvider` funcione correctamente, ya que los utiliza internamente.

Es importante, tener en cuenta que la capa `AuthenticationProvider`, **es la responsable de la lógica de
autenticación.** El `AuthenticationProvider` es donde encuentra las condiciones e instrucciones que deciden si
autenticar una solicitud o no.

El `AuthenticationProvider` en `Spring Security` se encarga de la lógica de autenticación. La implementación
predeterminada de la interfaz `AuthenticationProvider` delega la responsabilidad de encontrar el usuario del sistema a
un `UserDetailsService`. También utiliza `PasswordEncoder` para la gestión de contraseñas en el proceso de
autenticación.

## Define filtro personalizado

Por el momento solo definiremos el filtro personalizado que estamos inyectando vía constructor en la clase de
configuración personalizada de Spring Security `SecurityConfig`:

````java

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

    }
}
````

## Define Bean PasswordEncoder

Crearemos una nueva clase de configuración que contendrá múltiples `@Bean`, uno de ellos será por el momento la
implementación del `PasswordEncoder` que será usado por la clase `DaoAuthenticationProvider` definida por defecto por
Spring Security como implementación del `AuthenticationProvider`.

````java

@Configuration
public class BeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
````

## Implementa el UserDetailsService

Al igual que la implementación del `PasswordEncoder`, requerimos implementar el `UserDetailsService` para que sea usado
por el `DaoAuthenticationProvider` (implementación por defecto de Spring Security), en ese sentido, creamos la clase
de implementación `UserDetailsServiceImpl` que implementa la interfaz `UserDetailsService`:

````java

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
````

## Implementa la clase JwtService

Vamos a crear la clase `JwtService` que contendrá todo lo relacionado a la manipulación del `JWT`. Esta clase será
usado por nuestro filtro personalizado `JwtAuthFilter`. A continuación se muestra la clase `JwtService`:

````java

@Component
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        return this.generateToken(new HashMap<String, Object>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return this.buildToken(claims, userDetails, this.jwtExpiration);
    }

    public String extractUsername(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = this.extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public <R> R extractClaim(String token, Function<Claims, R> claimResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return this.extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        long currentTimeMillisStart = System.currentTimeMillis();
        long currentTimeMillisEnd = currentTimeMillisStart + jwtExpiration;

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTimeMillisStart))
                .setExpiration(new Date(currentTimeMillisEnd))
                .claim("authorities", authorities)
                .signWith(this.getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
````

Nuestra clase `JwtService` está usando configuraciones personalizadas definidas en el `application-dev.yml`. Esto lo
podemos observar al inicio de la clase, cuando hacemos uso de la anotación `@Value`. A continuación se muestra
únicamente dichas configuraciones personalizadas:

````yml
application:
  security:
    jwt:
      secret-key: jNFY9S0YoLZ9xizq2V8FG5yMudcZpBKXyLQjSWPbiX8jNFY9S0Y
      expiration: 3600000
````

## Implementa filtro personalizado JwtAuthFilter

En un apartado superior habíamos definido nuestro filtro personalizado `JwtAuthFilter`. Pues bien, en este apartado
terminaremos de implementarlo.

Recordar que para que nuestra clase `JwtAuthFilter` sea reconocido por `Spring Security` como un filtro personalizado,
debemos de extender la clase abstracta `OncePerRequestFilter` e implementar su método abstracto `doFilterInternal`.

`OncePerRequestFilter`, es una clase base de filtro que tiene como objetivo garantizar una ejecución única por envío
de solicitud, en cualquier contenedor de servlets. Se garantiza que el filtro se ejecutará exactamente una vez por cada
solicitud HTTP, incluso si la solicitud atraviesa varios filtros en la cadena de filtros.

````java

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final boolean contains = request.getServletPath().contains("/api/v1/auth");
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (contains || authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring("Bearer ".length());
        userEmail = this.jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (this.jwtService.isTokenValid(jwt, userDetails)) {
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
````

## Crea el controlador de autenticación

Vamos a crear el controlador que realizará las operaciones de login, registro, validación, etc. Empezaremos definiendo
el endpoint para el `/register`:

````java

@Tag(name = "Authentication", description = "API de autenticación de usuario")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest request) {
        this.authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

}
````

El tag `@Tag(name = "Authentication", description = "API de autenticación de usuario")` es de swagger. Cuando usemos
la `UI de Swagger` veremos agrupados los endpoints de este controlador.

En el controlador `AuthenticationController` estamos usando el record `RegistrationRequest` para recibir los datos que
vienen en la solicitud http.

A continuación se muestra el record `RegistrationRequest` cuyos atributos tienen anotaciones de validación:

````java
public record RegistrationRequest(@NotBlank(message = "El nombre es obligatorio")
                                  String firstName,

                                  @NotBlank(message = "El apellido es obligatorio")
                                  String lastName,

                                  @NotBlank(message = "El correo es obligatorio")
                                  @Email(message = "El correo no tiene un formato válido")
                                  String email,

                                  @NotBlank(message = "La contraseña es obligatoria")
                                  @Size(min = 8, message = "La contraseña debería tener como mínimo 8 caracteres")
                                  String password) {
}
````

Si observamos el controlador `AuthenticationController` estamos inyectando una clase de servicio `AuthenticationService`
que por el momento solo define un método que está siendo usado por el método `register()` del controlador.
Esta clase de servicio la implementaremos en el siguiente apartado.

````java

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    public void register(RegistrationRequest request) {

    }
}
````

## Implementa el método de registro del AuthenticationService

En el apartado anterior definimos la clase de servicio `AuthenticationService` que está siendo inyectada en el
controlador `AuthenticationController`. En este apartado implementaremos su método `register()`.

````java

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegistrationRequest request) {
        Role defaultRoleDB = this.roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("El rol USER no fue encontrado"));
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(this.passwordEncoder.encode(request.password()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(defaultRoleDB))
                .build();
        this.userRepository.save(user);
        this.sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
        String activationCode = this.generateAndSaveActivationCode(user);
        //TODO send email
    }

    private String generateAndSaveActivationCode(User user) {
        String activationCode = this.generateActivationCode(6);
        Token token = Token.builder()
                .token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        this.tokenRepository.save(token);
        return activationCode;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // Obtenemos índices del [0..9]
            codeBuilder.append(characters.charAt(randomIndex)); // Obtenemos el carácter según el índice
        }
        return codeBuilder.toString();
    }
}
````

`SecureRandom`, esta clase proporciona un generador de números aleatorios (RNG) criptográficamente sólido.
Un número aleatorio criptográficamente fuerte cumple mínimamente con las pruebas estadísticas del generador de números
aleatorios especificadas en FIPS 140-2, Requisitos de seguridad para módulos criptográficos, sección 4.9.1. Además,
**SecureRandom debe producir resultados no deterministas.** Por lo tanto, cualquier material inicial pasado a un objeto
SecureRandom debe ser impredecible y todas las secuencias de salida de SecureRandom deben ser criptográficamente
sólidas, como se describe en RFC 4086: Requisitos de aleatoriedad para la seguridad.

La diferencia clave entre `SecureRandom` y `Random` (otra clase en Java utilizada para generar números aleatorios) es
que `SecureRandom` utiliza algoritmos criptográficamente seguros para generar números aleatorios, lo que lo hace
adecuado para aplicaciones que requieren aleatoriedad confiable, como la generación de claves criptográficas,
contraseñas seguras, y otros usos donde la predictibilidad de los números aleatorios podría ser un problema de
seguridad.
