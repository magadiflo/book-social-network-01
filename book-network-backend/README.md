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

## Implementa el servicio de envío de email

Crearemos la clase de servicio `EmailService` que nos permitirá trabajar con la librería `JavaMailServer` para el envío
de correos.

````java

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String username, EmailTemplateName emailTemplateName,
                          String confirmationUrl, String activationCode, String subject) throws MessagingException {

        String templateName = emailTemplateName == null ? "confirm-email" : emailTemplateName.getName();

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("confirmationUrl", confirmationUrl);
        variables.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(variables);

        helper.setFrom("contact@magadiflo.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = this.templateEngine.process(templateName, context);

        helper.setText(template, true);

        this.javaMailSender.send(mimeMessage);
    }
}
````

El envío del correo puede ser algo pesado, quizás nos lleve mucho tiempo, así que no queremos bloquear al usuario y
hacerlo esperar hasta que se envíe el correo, por lo que necesitamos agregar la siguiente anotación a nivel del método
para enviar el correo de forma asíncrona `@Async`.

Cuando agregamos la anotación `@Async` sobre un método debemos agregar en una clase de configuración (en mi caso en la
clase principal) la anotación `@EnableAsync`.

````java

@EnableAsync //<--- Habilita la capacidad de ejecución de métodos asíncronos de Spring
@EnableJpaAuditing
@SpringBootApplication
public class BookNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkBackendApplication.class, args);
    }

}
````

`@EnableAsync`, **habilita la capacidad de ejecución de métodos asincrónicos de Spring**, Para usarse junto con las
clases `@Configuration`, lo que permite el procesamiento asíncrono basado en anotaciones para un contexto de aplicación
Spring completo.

Por otro lado, dentro del método `sendEmail()` estamos definiendo el nombre de la plantilla `html` que vamos a
usar, por defecto, si el `EmailTemplateName` es nulo, tomará la plantilla con nombre `confirm-email.html`, caso
contrario tomará el que se le envíe por parámetro.

En la clase de enumeración `EmailTemplateName` definimos el nombre de una de las plantillas html que vamos a usar:

````java

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate-account");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
````

En el código tenemos dos plantillas que pueden ser usadas `activate-account` y `confim-email`, eso significa que
necesitamos crearle a cada uno su correspondiente `documento HTML` que incorpore `Thyemelaf` para poder leer las
variables que se le pasen.

En el path `src/main/resources/templates` crearemos las dos plantillas `activate-account.html` y `confirm-email.html`,
pero por el momento solo implementaremos el `activate-account.html`:

````html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Activación de Cuenta</title>
    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #F4F4F4;
        }

        .container {
            max-width: 600px;
            margin: 10px auto;
            padding: 20px;
            background-color: #FFF;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .activation-code {
            font-size: 36px;
            text-align: center;
            margin-bottom: 20px;
        }

        .activation-link {
            display: block;
            text-align: center;
            margin-top: 20px;
        }

        .activation-link a {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007BFF;
            color: #FFF;
            text-decoration: none;
            border-radius: 5px;
        }

        .activation-link a:hover {
            cursor: pointer;
        }
    </style>
</head>

<body>
<div class="container">
    <h1>Activación de cuenta</h1>
    <p class="greeting" th:text="'Hola ' + ${username} + ','"></p>
    <p>
        ¡Gracias por registrarte! Por favor, utilice el siguiente
        <b>código de activación</b> para activar su cuenta.
    </p>
    <div class="activation-code"><span th:text="${activation_code}"></span></div>
    <div class="activation-link">
        <a th:href="${confirmationUrl}" target="_blank">Activa tu cuenta</a>
    </div>
</div>
</body>

</html>
````

## Actualiza el servicio AuthenticationService para el envío de email

Una vez que hemos implementado el método `sendEmail()` la clase `EmailService`, vamos a actualizar el
método `register()` de la clase de servicio `AuthenticationService` para que cuando un usuario se registre, a
continuación se pueda enviar un correo para validar su cuenta:

````java

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    /* other properties */
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
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

    private void sendValidationEmail(User user) throws MessagingException {
        String activationCode = this.generateAndSaveActivationCode(user);
        this.emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT,
                this.activationUrl, activationCode, "Activación de cuenta");
    }
    /* other methods */
}
````

Debemos crear la configuración `application.mailing.frontend.activation-url` en el `application-dev.yml`. Esta
configuración la estamos usando en el `AuthenticationService` para enviárselo al servicio `EmailService` y éste a
su vez lo adjunte a la plantilla html:

````yml
application:
  mailing:
    frontend:
      activation-url: http://localhost:4200/activate-account
````

## Probando el registro de usuario y envío de email

Antes de ejecutar la aplicación para testear el registro del usuario y el envío del email, necesitamos registrar el rol
`USER` que por defecto deberá tener todo usuario cuando se registre en el sistema.

Para eso, en la clase principal creamos el `@Bean CommandLineRunner` para poder crear el rol `USER` y guardarlo en la
base de datos.

````java

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class BookNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("USER").isEmpty()) {
                Role role = Role.builder().name("USER").build();
                roleRepository.save(role);
            }
        };
    }

}
````

Listo, ahora sí realicemos el registro de un usuario y observemos que se registra en la base de datos y se envía el
correo con el código de activación.

**NOTA**
> Es importante recordar que estamos trabajando con `docker compose`, así que debemos tener levantado en primer lugar
> los contenedores de la base de datos de `postgres` y el del `maildev` para emular el servidor de recepción de correo.

````bash
$ curl -v -X POST -H "Content-Type: application/json" -d "{\"firstName\": \"Milagros\", \"lastName\": \"Diaz\", \"email\": \"milagros@gmail.com\", \"password\": \"12345678\"}" http://localhost:8080/api/v1/auth/register | jq
>
< HTTP/1.1 202
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 0
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Length: 0
< Date: Mon, 10 Jun 2024 04:10:35 GMT
<
````

Observamos que el código retornado es el código `HTTP 202 Accepted` indicándonos que *el servidor ha aceptado la
solicitud de tu navegador, pero aún la está procesando. La solicitud puede, en última instancia, dar lugar o no a una
respuesta completa.*

Revisamos la base de datos ingresando al contenedor de docker `c-postgres-bsn`:

````bash
$ docker container exec -it c-postgres-bsn /bin/sh
/ # psql -U magadiflo -d db_book_social_network
psql (15.2)
Type "help" for help.

db_book_social_network=# \dt
            List of relations
 Schema |    Name     | Type  |   Owner
--------+-------------+-------+-----------
 public | roles       | table | magadiflo
 public | tokens      | table | magadiflo
 public | users       | table | magadiflo
 public | users_roles | table | magadiflo
(4 rows)

db_book_social_network=# SELECT * FROM roles;
 id |        created_date        | last_modified_date | name
----+----------------------------+--------------------+------
  1 | 2024-06-09 22:55:02.196783 |                    | USER
(1 row)

db_book_social_network=# SELECT * FROM users;
 id | account_locked |        created_date        | date_of_birth |       email        | enabled | first_name | last_modified_date | last_name |                           password
----+----------------+----------------------------+---------------+--------------------+---------+------------+--------------------+-----------+--------------------------------------------------------------
  1 | f              | 2024-06-09 23:10:35.555767 |               | milagros@gmail.com | f       | Milagros   |                    | Diaz      | $2a$10$NlmuwPQdn1wy0V.zss9JLOu3sv5Y2xAEf/8NCphd5aY.OMYIWcb12
(1 row)

db_book_social_network=# SELECT * FROM users_roles;
 user_id | role_id
---------+---------
       1 |       1
(1 row)

db_book_social_network=# SELECT * FROM tokens;
 id |         created_at         |         expires_at         | token  | validated_at | user_id
----+----------------------------+----------------------------+--------+--------------+---------
  1 | 2024-06-09 23:10:35.594157 | 2024-06-09 23:20:35.594157 | 118161 |              |       1
(1 row)

db_book_social_network=#
````

Recordemos que estamos usando un servidor de correo `maildev` levantado en el contenedor de docker, así que
accedemos a él mediante el navegador para ver el correo que se envió junto al código de activación.

![05.send-email-test.png](assets/05.send-email-test.png)

Observemos que el código de activación que recibimos en el correo es el mismo que tenemos almacenado en la
tabla `tokens` de la base de datos.

## Implementa método de autenticación (login)

Antes de implementar el método de autenticación necesitamos crear clases cuya única finalidad será recibir y enviar
datos a través de solicitudes http:

````java
public record AuthenticationRequest(@NotBlank(message = "El correo es obligatorio")
                                    @Email(message = "El correo no tiene un formato válido")
                                    String email,

                                    @NotBlank(message = "La contraseña es obligatoria")
                                    @Size(min = 8, message = "La contraseña debería tener como mínimo 8 caracteres")
                                    String password) {
}
````

````java
public record AuthenticationResponse(String token) {
}
````

Para implementar la autenticación del usuario debemos crear un nuevo método en el servicio `AuthenticationService`:

````java

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    /* other properties */
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var authentication = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authenticate = this.authenticationManager.authenticate(authentication);
        /**
         * Si el flujo llega hasta esta línea significa que la autenticación ha sido exitosa retornando un objeto
         * del tipo Authentication. En caso contrario, el método this.authenticationManager.authenticate(...)
         * lanzará una excepción
         */
        User user = (User) authenticate.getPrincipal();
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.fullName());

        String jwt = this.jwtService.generateToken(claims, user);

        return new AuthenticationResponse(jwt);
    }

    /* other methods */
}
````

Observar que en la clase anterior estamos inyectando la interface `AuthenticationManager` quien nos permitirá realizar
la autenticación del usuario. Sin embargo, necesitamos crear un `@Bean` que retorne el objeto concreto
del `AuthenticationManager` y se inyecte en la propiedad `authenticationManager`.

Para eso, en la clase `BeansConfig` agregamos el `@Bean AuthenticationManager`:

````java

@Configuration
public class BeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION));
        configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
````

Aprovechamos la clase de configuración anterior para también agregarle el `@Bean CorsFilter`, este bean es muy
importante, dado que nuestra aplicación de Angular estará levantado en el dominio `http://localhost:4200`, mientras
que nuestra aplicación de Spring Boot correrá en `http://localhost:8080`. Cuando se hagan peticiones desde
Angular, nuestro navegador bloqueará la petición debido a las políticas de seguridad de CORS (Cross-Origin Resource
Sharing), porque detecta que la petición se está realizando a otro dominio que no es el mismo en el que está corriendo
el frontend. Para evitar ese problema, necesitamos agregar el bean de `CorsFilter` en el backend, para permitir el
dominio del frontend y así habilitar las peticiones entre ambos dominios.

Nuestra configuración del `CorsFilter` permite solicitudes desde http://localhost:4200 a cualquier endpoint en el
backend, permitiendo los métodos HTTP y encabezados definidos dentro de la configuración. Esta configuración asegura
que las peticiones entre la aplicación Angular y el backend Spring Boot no sean bloqueadas por el navegador.

Finalmente, implementamos el endpoint de `/authenticate` en el controlador:

````java

@Tag(name = "Authentication", description = "API de autenticación de usuario")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
        this.authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(this.authenticationService.authenticate(request));
    }

}
````

## Implementa método de activación de cuenta

Implementamos primero el método `activateAccount()` en la clase de servicio `AuthenticationService`:

````java

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    /* other properties */

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    /* other methods */

    public void activateAccount(String token) throws MessagingException {
        Token tokenDB = this.tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (LocalDateTime.now().isAfter(tokenDB.getExpiresAt())) {
            this.sendValidationEmail(tokenDB.getUser());
            throw new RuntimeException("El token de activación ha caducado. Se ha enviado un nuevo token al mismo correo.");
        }

        // Podemos obtener el usuario desde el token recuperado o nuevamente ir a la bd
        User user = this.userRepository.findById(tokenDB.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en la activación"));
        user.setEnabled(true);
        this.userRepository.save(user);

        tokenDB.setValidatedAt(LocalDateTime.now());
        this.tokenRepository.save(tokenDB);
    }

    /* other methods */
}
````

Finalmente, en el controlador `AuthenticationController` definimos el endpoint para activar la cuenta:

````java

@Tag(name = "Authentication", description = "API de autenticación de usuario")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /* other methods */

    @GetMapping(path = "/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        this.authenticationService.activateAccount(token);
    }

}
````

## Prueba flujo completo de autenticación

### Registro de usuario

````bash
$ curl -v -X POST -H "Content-Type: application/json" -d "{\"firstName\": \"Martin\", \"lastName\": \"Diaz\", \"email\": \"martin@gmail.com\", \"password\": \"12345678\"}" http://localhost:8080/api/v1/auth/register | jq
>
< HTTP/1.1 202
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 0
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Length: 0
< Date: Mon, 10 Jun 2024 05:41:36 GMT
<
````

- Verificamos el registro generado en las tablas de la base de datos

````bash
$ docker container exec -it c-postgres-bsn /bin/sh
/ # psql -U magadiflo -d db_book_social_network
psql (15.2)
Type "help" for help.

db_book_social_network=# SELECT * FROM roles;
 id |        created_date        | last_modified_date | name
----+----------------------------+--------------------+------
  1 | 2024-06-09 22:55:02.196783 |                    | USER
(1 row)

db_book_social_network=# SELECT * FROM users;
 id | account_locked |        created_date        | date_of_birth |       email        | enabled | first_name | last_modified_date | last_name |                           password
----+----------------+----------------------------+---------------+--------------------+---------+------------+--------------------+-----------+--------------------------------------------------------------
  1 | f              | 2024-06-09 23:10:35.555767 |               | milagros@gmail.com | f       | Milagros   |                    | Diaz      | $2a$10$NlmuwPQdn1wy0V.zss9JLOu3sv5Y2xAEf/8NCphd5aY.OMYIWcb12
  2 | f              | 2024-06-10 00:41:35.931245 |               | martin@gmail.com   | f       | Martin     |                    | Diaz      | $2a$10$mWuK1LNjNgVEkTglRKQj.OiorPil3APzkMBhLRYfQeolmPx0XPvH6
(2 rows)

db_book_social_network=# SELECT * FROM tokens;
 id |         created_at         |         expires_at         | token  | validated_at | user_id
----+----------------------------+----------------------------+--------+--------------+---------
  1 | 2024-06-09 23:10:35.594157 | 2024-06-09 23:20:35.594157 | 118161 |              |       1
  2 | 2024-06-10 00:41:36.045093 | 2024-06-10 00:51:36.045093 | 564332 |              |       2
(2 rows)

db_book_social_network=# SELECT * FROM users_roles;
 user_id | role_id
---------+---------
       1 |       1
       2 |       1
(2 rows)

db_book_social_network=#
````

- Verificamos que haya llegado el correo de verificación

![06.register-user-email.png](assets/06.register-user-email.png)

### Activando cuenta

- Activamos la cuenta con el código de verificación recibido

````bash
$ curl -v -G --data "token=564332" http://localhost:8080/api/v1/auth/activate-account | jq
>
< HTTP/1.1 200
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 0
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Length: 0
< Date: Mon, 10 Jun 2024 05:47:16 GMT
<
````

- Con la activación de la cuenta realizada anteriormente, debemos verificar en la base de datos que el **usuario esté
  habilitado** y que el campo de `validated_at` de la tabla tokens tenga la fecha del momento en que se hizo la
  actualización

````bash
db_book_social_network=# SELECT * FROM users;
 id | account_locked |        created_date        | date_of_birth |       email        | enabled | first_name |     last_modified_date     | last_name |                           password
----+----------------+----------------------------+---------------+--------------------+---------+------------+----------------------------+-----------+--------------------------------------------------------------
  1 | f              | 2024-06-09 23:10:35.555767 |               | milagros@gmail.com | f       | Milagros   |                            | Diaz      | $2a$10$NlmuwPQdn1wy0V.zss9JLOu3sv5Y2xAEf/8NCphd5aY.OMYIWcb12
  2 | f              | 2024-06-10 00:41:35.931245 |               | martin@gmail.com   | t       | Martin     | 2024-06-10 00:47:16.875359 | Diaz      | $2a$10$mWuK1LNjNgVEkTglRKQj.OiorPil3APzkMBhLRYfQeolmPx0XPvH6
(2 rows)

db_book_social_network=# SELECT * FROM tokens;
 id |         created_at         |         expires_at         | token  |       validated_at        | user_id
----+----------------------------+----------------------------+--------+---------------------------+---------
  1 | 2024-06-09 23:10:35.594157 | 2024-06-09 23:20:35.594157 | 118161 |                           |       1
  2 | 2024-06-10 00:41:36.045093 | 2024-06-10 00:51:36.045093 | 564332 | 2024-06-10 00:47:16.90961 |       2
(2 rows)
````

### Iniciando sesión

Finalmente, vamos a iniciar sesión enviando el email y password utilizados en el registro:

````bash
$ curl -v -X POST -H "Content-Type: application/json" -d "{\"email\": \"martin@gmail.com\", \"password\": \"12345678\"}" http://localhost:8080/api/v1/auth/authenticate | jq
>
< HTTP/1.1 200
<
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJmdWxsTmFtZSI6Ik1hcnRpbiBEaWF6Iiwic3ViIjoibWFydGluQGdtYWlsLmNvbSIsImlhdCI6MTcxNzk5ODY5MCwiZXhwIjoxNzE4MDAyMjkwLCJhdXRob3JpdGllcyI6WyJVU0VSIl19.w91GedxxUu3FfXmPHiRzJhY20BmRMxYqr4JUaZ_tZd8"
}
````

Si decodificamos el jwt veremos el contenido que hay en él:

![07.decode-jwt.png](assets/07.decode-jwt.png)

## Prueba flujo cuando el código de autenticación ha caducado

Inicialmente habíamos registrado al usuario `milagros@gmail.com` cuya cuenta aún no ha sido activada:
`(users) enabled=false` y `(tokens) validated_at=null`. Solo como nota, el código de activación de este registro sí ha
llegado al correo.

Veamos cómo es que inicialmente tenemos los datos del usuario `milagros@gmail.com` que ha sigo registrado ya con un
tiempo bien prolongado.

````bash
$ docker container exec -it c-postgres-bsn /bin/sh
/ # psql -U magadiflo -d db_book_social_network
psql (15.2)
Type "help" for help.

db_book_social_network=# SELECT * FROM users;
 id | account_locked |        created_date        | date_of_birth |       email        | enabled | first_name |     last_modified_date     | last_name |                           password
----+----------------+----------------------------+---------------+--------------------+---------+------------+----------------------------+-----------+--------------------------------------------------------------
  1 | f              | 2024-06-09 23:10:35.555767 |               | milagros@gmail.com | f       | Milagros   |                            | Diaz      | $2a$10$NlmuwPQdn1wy0V.zss9JLOu3sv5Y2xAEf/8NCphd5aY.OMYIWcb12
  2 | f              | 2024-06-10 00:41:35.931245 |               | martin@gmail.com   | t       | Martin     | 2024-06-10 00:47:16.875359 | Diaz      | $2a$10$mWuK1LNjNgVEkTglRKQj.OiorPil3APzkMBhLRYfQeolmPx0XPvH6
(2 rows)

db_book_social_network=# SELECT * FROM tokens;
 id |         created_at         |         expires_at         | token  |       validated_at        | user_id
----+----------------------------+----------------------------+--------+---------------------------+---------
  1 | 2024-06-09 23:10:35.594157 | 2024-06-09 23:20:35.594157 | 118161 |                           |       1
  2 | 2024-06-10 00:41:36.045093 | 2024-06-10 00:51:36.045093 | 564332 | 2024-06-10 00:47:16.90961 |       2
````

Ahora, supongamos que el usuario se ha demorado en activar su cuenta, es decir, el código de activación (token) ya ha
expirado, por lo que ahora trataremos de activar la cuenta con el token vencido:

````bash
$ curl -v -G --data "token=118161" http://localhost:8080/api/v1/auth/activate-account | jq
>
< HTTP/1.1 403
<
````

````bash
java.lang.RuntimeException: El token de activación ha caducado. Se ha enviado un nuevo token al mismo correo.
	at dev.magadiflo.book.network.app.auth.AuthenticationService.activateAccount(AuthenticationService.java:81) ~[classes/:na]
......
````

Como observamos, nos lanza un error el servidor y en el log observamos el mensaje que más adelante capturaremos y
mandaremos al cliente.

Revisando las tablas antes mostradas ahora debemos tener un nuevo código de activación para el usuario
`milagros@gmail.com`:

````bash
db_book_social_network=# SELECT * FROM users;
 id | account_locked |        created_date        | date_of_birth |       email        | enabled | first_name |     last_modified_date     | last_name |                           password
----+----------------+----------------------------+---------------+--------------------+---------+------------+----------------------------+-----------+--------------------------------------------------------------
  1 | f              | 2024-06-09 23:10:35.555767 |               | milagros@gmail.com | f       | Milagros   |                            | Diaz      | $2a$10$NlmuwPQdn1wy0V.zss9JLOu3sv5Y2xAEf/8NCphd5aY.OMYIWcb12
  2 | f              | 2024-06-10 00:41:35.931245 |               | martin@gmail.com   | t       | Martin     | 2024-06-10 00:47:16.875359 | Diaz      | $2a$10$mWuK1LNjNgVEkTglRKQj.OiorPil3APzkMBhLRYfQeolmPx0XPvH6
(2 rows)

db_book_social_network=# SELECT * FROM tokens;
 id |         created_at         |         expires_at         | token  |       validated_at        | user_id
----+----------------------------+----------------------------+--------+---------------------------+---------
  1 | 2024-06-09 23:10:35.594157 | 2024-06-09 23:20:35.594157 | 118161 |                           |       1
  2 | 2024-06-10 00:41:36.045093 | 2024-06-10 00:51:36.045093 | 564332 | 2024-06-10 00:47:16.90961 |       2
  3 | 2024-06-10 00:58:53.935437 | 2024-06-10 01:08:53.935437 | 551495 |                           |       1
````

Y además, un nuevo email debe haberse enviado al correo registrado:

![08.new-activation-code.png](assets/08.new-activation-code.png)

Utilizaremos el nuevo código de activación para activar la cuenta. Vemos que el código de estado es exitoso:

````bash
$ curl -v -G --data "token=551495" http://localhost:8080/api/v1/auth/activate-account | jq
>
< HTTP/1.1 200
<
````

Finalmente, verificamos la base de datos y vemos que ahora sí la cuenta ha sido activada:

````bash
$ docker container exec -it c-postgres-bsn /bin/sh
/ # psql -U magadiflo -d db_book_social_network
psql (15.2)
Type "help" for help.

db_book_social_network=# SELECT * FROM users;
 id | account_locked |        created_date        | date_of_birth |       email        | enabled | first_name |     last_modified_date     | last_name |                           password
----+----------------+----------------------------+---------------+--------------------+---------+------------+----------------------------+-----------+--------------------------------------------------------------
  2 | f              | 2024-06-10 00:41:35.931245 |               | martin@gmail.com   | t       | Martin     | 2024-06-10 00:47:16.875359 | Diaz      | $2a$10$mWuK1LNjNgVEkTglRKQj.OiorPil3APzkMBhLRYfQeolmPx0XPvH6
  1 | f              | 2024-06-09 23:10:35.555767 |               | milagros@gmail.com | t       | Milagros   | 2024-06-10 01:02:50.882691 | Diaz      | $2a$10$NlmuwPQdn1wy0V.zss9JLOu3sv5Y2xAEf/8NCphd5aY.OMYIWcb12
(2 rows)

db_book_social_network=# SELECT * FROM tokens;
 id |         created_at         |         expires_at         | token  |        validated_at        | user_id
----+----------------------------+----------------------------+--------+----------------------------+---------
  1 | 2024-06-09 23:10:35.594157 | 2024-06-09 23:20:35.594157 | 118161 |                            |       1
  2 | 2024-06-10 00:41:36.045093 | 2024-06-10 00:51:36.045093 | 564332 | 2024-06-10 00:47:16.90961  |       2
  3 | 2024-06-10 00:58:53.935437 | 2024-06-10 01:08:53.935437 | 551495 | 2024-06-10 01:02:50.926765 |       1
(3 rows)
````

## Manejo de excepciones

Para el manejo de excepciones crearemos un catálogo de errores que contendrá los **códigos de errores del negocio**,
es decir, códigos, mensajes, etc. que es propio del negocio. No hay que confundirnos con los códigos http que son
propios de los estados http. Cuando creamos nuestros catálogos de errores, tendremos esta clase de enumeración donde
definiremos nuestro código personalizado, que es propio del negocio:

````java

@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "Sin código"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "La nueva contraseña no coincide"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "La cuenta del usuario está bloqueada"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "La cuenta del usuario está deshabilitada"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Usuario y/o contraseña es incorrecta");

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
````

Ahora tenemos que crear `dto` que nos permita definir un formato uniforme para todas las excepciones que se produzcan
en nuestra aplicación.

````java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
    private Integer businessErrorCode;
    private String businessErrorDescription;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
````

Finalmente, definimos la clase controladora que manejará las excepciones producidas en nuestra aplicación. Notar que
en esta clase estamos usando las dos clases anteriormente definidas:

````java

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .businessErrorCode(BusinessErrorCodes.ACCOUNT_LOCKED.getCode())
                .businessErrorDescription(BusinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                .error(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .businessErrorCode(BusinessErrorCodes.ACCOUNT_DISABLED.getCode())
                .businessErrorDescription(BusinessErrorCodes.ACCOUNT_DISABLED.getDescription())
                .error(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                .businessErrorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                .error(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception) {
        Set<String> errors = new HashSet<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .validationErrors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        // Registrar la excepción. En nuestro caso, imprimiremos en consola la pila de errores para
        exception.printStackTrace();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .businessErrorDescription("Error interno, contacte al administrador")
                .error(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }
}
````

## Probando el manejo de excepciones

Levantamos la aplicación y ejecutamos los siguientes request, veamos que la aplicación nos está validando correctamente:

````bash
$ curl -v -X POST -H "Content-Type: application/json" -d "{\"email\": \"martin@gmail.com\", \"password\": \"clave12345\"}" http://localhost:8080/api/v1/auth/authenticate | jq
>
< HTTP/1.1 401
<
{
  "businessErrorCode": 304,
  "businessErrorDescription": "Usuario y/o contraseña es incorrecta",
  "error": "Usuario y/o contraseña es incorrecta"
}
````

````bash
$ curl -v -X POST -H "Content-Type: application/json" -d "{\"firstName\": \"\", \"email\": \"rosita@gmail.com\", \"password\": \"12345678\"}" http://localhost:8080/api/v1/auth/register | jq
>
< HTTP/1.1 400
<
{
  "validationErrors": [
    "El nombre es obligatorio",
    "El apellido es obligatorio"
  ]
}
````

## Probando Swagger y OpenAPI

Sin haber realizado ninguna configuración, más que el haber agregado la anotación
`@Tag(name = "Authentication", description = "API de autenticación de usuario")` en el controlador
`AuthenticationController` (aunque podríamos haberlo no agregado), tan solo agregando la dependencia de `Swagger`,
ejecutamos el proyecto `Spring Boot`, abrimos el navegador e ingresamos la siguiente dirección:

````bash
$ http://localhost:8080/swagger-ui.html
````

Al dar enter, seremos redireccionados a la siguiente dirección (podríamos haber ingresado directamente a la url de
abajo, pero lo hice para ver de dónde es que sale):

````bash
$ http://localhost:8080/swagger-ui/index.html
````

En la imagen siguiente observamos la interfaz gráfica de `Swagger-ui`:

![09.swagger-ui.png](assets/09.swagger-ui.png)

Si ingresamos a la siguiente dirección veremos el documento en formato json:

````bash
  $ http://localhost:8080/v3/api-docs
````

````bash
{
  "openapi": "3.0.1",
  "info": {
    "title": "OpenAPI definition",
    "version": "v0"
  },
  "servers": [
    {
      "url": "http://localhost:8080",
      "description": "Generated server url"
    }
  ],
  "tags": [
    {
      "name": "Authentication",
      "description": "API de autenticación de usuario"
    }
  ],
  "paths": {
    "/api/v1/auth/register": {...},
    "/api/v1/auth/authenticate": {...},
    "/api/v1/auth/activate-account": {...}
  },
  "components": {
    ...
  }
}
````

**NOTA**   
La especificación anterior será usada desde Angular con la dependencia `ng-openapi-gen` para generar la clase
de servicio que implemente automáticamente los métodos para hacer solicitudes a los endpoints de nuestro backend.

---

## Crea la entidad Book

A partir de este punto empezaremos a crear las funcionalidades relacionadas a la lógica de negocio de la aplicación.
Empezaremos creando la entidad `Book`:

````java

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy; // Del tipo Long, porque la clave primaria de la entidad User la definimos como Long

    @LastModifiedBy
    @Column(insertable = false)
    private Long lastModifiedBy; // Del tipo Long, porque la clave primaria de la entidad User la definimos como Long
}
````

Es importante mencionar que hemos agregado dos atributos de auditoria adicionales anotados con: `@CreatedBy` y
`@LastModifiedBy`, ambos están sobre campos del tipo `Long`. Estos atributos hacen referencia a la clave primaria
de la entidad `User`:

- `@CreatedBy`, se utiliza para almacenar la información (en nuestro caso su PK) del usuario que creó la entidad.
- `@LastModifiedBy`, se utiliza para almacenar la información (en nuestro caso su PK) del usuario que modificó la
  entidad por última vez.

## Crea la entidad Feedback

````java

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
@EntityListeners(AuditingEntityListener.class)
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double note;
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    private Long lastModifiedBy;
}
````

## Usa herencia y refactoriza el código

En este apartado vamos a refactorizar las clases de entidad porque hay muchos atributos y anotaciones que se están
repitiendo. Para eso, crearemos una clase base y cada entidad heredará de la clase base.

A continuación se muestra la clase base `BaseEntity` que albergará todos los elementos comunes entre las entidades,
tanto el identificador como las propiedades relacionadas con la auditoría, de esa manera dejaremos las entidades
limpias:

````java

@Getter
@Setter
@SuperBuilder // Lo usamos cuando estamos trabajando con herencia
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // Para que las entidades que hereden de esta clase mapeen los atributos definidos aquí
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy; // Del tipo Long, porque la clave primaria de la entidad User la definimos como Long

    @LastModifiedBy
    @Column(insertable = false)
    private Long lastModifiedBy; // Del tipo Long, porque la clave primaria de la entidad User la definimos como Long
}
````

**DONDE**

- `@SuperBuilder`, se utiliza para generar un patrón de diseño `Builder` que también admite `herencia`. Esto es
  especialmente útil cuando tienes clases con jerarquías y deseas que las clases derivadas tengan sus propios
  constructores de tipo `Builder` que incluyan los campos de la clase base.
- Tanto la clase base como las clases derivadas deben tener la anotación `@SuperBuilder` para que la funcionalidad se
  propague correctamente a través de la jerarquía de herencia. Esto permite que cada clase tenga su propio builder que
  incluya los campos de la clase base y los de la clase derivada.
- Si solo anotaras la clase derivada con `@SuperBuilder` y no la clase base, los campos de la clase base no estarían
  disponibles en el builder de la clase derivada. Por eso es necesario que ambas clases tengan la
  anotación `@SuperBuilder`.


- `@MappedSuperclass`, es una anotación de JPA (Java Persistence API) que se utiliza en el contexto de la persistencia
  de datos en bases de datos relacionales. La anotación `@MappedSuperclass` se aplica a una clase para indicar que sus
  propiedades se deben heredar por las clases hijas, pero que no debe ser una entidad en sí misma.
- Cuando se marca una clase como `@MappedSuperclass`, esta clase no se mapea a una tabla en la base de datos. Sin
  embargo, sus atributos se heredan y se mapean a las columnas en las tablas de las entidades hijas.

**Comparación de `@SuperBuilder` con `@Builder`**

- `@Builder` no soporta herencia de forma nativa. Si usas `@Builder` en una clase base, las clases derivadas no
  heredarán el constructor tipo Builder.
- `@SuperBuilder` está diseñado específicamente para escenarios de herencia y proporciona una forma más intuitiva y
  natural de manejar constructores tipo Builder en estas situaciones.

Ahora que ya tenemos nuestra clase base, refactorizamos la entidad `Book` y `Feedback`:

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;
}
````

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
public class Feedback extends BaseEntity {
    private Double note;
    private String comment;
}
````

## Crea la entidad BookTransactionHistory

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_transaction_history")
public class BookTransactionHistory extends BaseEntity {
    private boolean returned;
    private boolean returnApproved;
}
````

## Agrega relaciones entre las entidades

Teniendo en cuenta el diagrama de clases mostrado en el apartado inicial, vamos a empezar a asociar las distintas
entidades usando las anotaciones de jpa/hibernate.

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
public class Feedback extends BaseEntity {
    /* properties */

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;
}
````

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book extends BaseEntity {
    /* properties */

    @JoinColumn(name = "owner_id")
    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;
}
````

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

    /* properties */

    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @OneToMany(mappedBy = "owner")
    private List<Book> books;

    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> histories;

    /* other methods */
}
````

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_transaction_history")
public class BookTransactionHistory extends BaseEntity {
    /* properties */

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;
}
````

Si analizamos bien, tenemos una relación de muchos a muchos entre `Book` y `User`, pero como
menciona el tutor, queremos agregar a la relación algunos atributos adicionales, así que para facilitarnos el trabajo
diremos que la tabla intermedia `book_transaction_history` será nuestra entidad `BookTransactionHistory`, al que le
agregaremos los atributos adicionales y quien será tratado como una entidad para realizar consultas, operaciones, etc.

Luego de relacionar todas las entidades, ejecutaremos la aplicación y veremos cómo es que se han construido las tablas
en la base de datos. A continuación se muestran las tablas generadas y asociadas entre ellas:

![10.relationships_between_entities.png](assets/10.relationships_between_entities.png)

## Agrega el auditor de aplicaciones consciente: Auditing

Para saber quién creó la entidad, quién la modificó por última vez, es decir, saber el `id` del usuario que debe
colocarse en los campos `createdBy`  y `lastModifiedBy` necesitamos implementar algo llamado
`Application Auditor Aware (auditor de aplicaciones consciente)`. El Auditor Application Aware es un mecanismo manejado
por Spring donde todo lo que debemos hacer es proporcionar información sobre el usuario o cómo podemos obtener
información sobre el usuario, en nuestro caso decidimos usar el `id` del usuario para almacenarlo en los campos
`createdBy` y `lastModifiedBy`.

Si observamos la entidad base `BaseEntity` podremos ver que tenemos definido para el tema de auditoría, cuatro
atributos: 2 del tipo `LocaleDateTime` y 2 del tipo `Long`.

Recordemos que para el tema de auditoría agregamos la anotación `@EntityListeners(AuditingEntityListener.class)` en
la clase `BaseEntity` y la anotación `@EnableJpaAuditing` en la clase principal de esta aplicación. Pero eso solo
funciona para las anotaciones `@CreatedDate` y `@LastModifiedDate`, es decir, con esas configuraciones los campos
del tipo `LocalDateTime` estarán insertando valores automáticamente.

Para las anotaciones `@CreatedBy` y `@LastModifiedBy` necesitamos realizar configuraciones adicionales y la pregunta
es **¿por qué?**, porque Spring no sabe como obtener el usuario de forma predeterminada, pero en nuestro caso, desde
que implementamos la seguridad sabemos cómo funciona nuestra aplicación y podemos implementar esto por nuestra propia
cuenta.

A continuación se muestra, como es que tenemos actualmente nuestra clase `BaseEntity`:

````java

@Getter
@Setter
@SuperBuilder // Lo usamos cuando estamos trabajando con herencia
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // Para que las entidades que hereden de esta clase mapeen los atributos definidos aquí
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy; // Del tipo Long, porque la clave primaria de la entidad User la definimos como Long

    @LastModifiedBy
    @Column(insertable = false)
    private Long lastModifiedBy; // Del tipo Long, porque la clave primaria de la entidad User la definimos como Long
}
````

Para agregar la configuración del `AuditorAware` necesitamos crear una clase que la implemente:

```java
/**
 * AuditorAware<Long>, el Long corresponde con el tipo de dato del id del User, esto es porque el id del Usuario
 * será el que se registre en los campos createdBy y lastModifiedBy de la clase BaseEntity.
 */
public class ApplicationAuditAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        User userPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());
    }
}
```

Luego, en la clase de configuración de Beans, debemos crear un `@Bean` que retorne una instancia de la implementación
del AuditorAware:

````java

@Configuration
public class BeansConfig {
    /* other beans */

    @Bean
    public AuditorAware<Long> auditorAware() { //El nombre de este método "auditorAware" será colocado en la anotación @EnableJpaAuditing
        return new ApplicationAuditAware();
    }
}
````

Para finalizar la configuración del AuditorAware, necesitamos ir a la clase principal donde tenemos la anotación
`@EnableJpaAuditing` para agregarle el nombre del método del @Bean que configuramos del AuditorAware.

````java

@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditorAware")//"auditorAware", nombre del método del @Bean auditorAware
@SpringBootApplication
public class BookNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkBackendApplication.class, args);
    }

    /* other bean */
}
````

## Implementa el método save de Book

Empezaremos creando el controlador:

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Long> saveBook(@Valid @RequestBody BookRequest request, Authentication authentication) {
        return new ResponseEntity<>(this.bookService.save(request, authentication), HttpStatus.CREATED);
    }
}
````

Es importante observar que por parámetro estamos inyectando la interfaz `Authentication`. Recordemos además, que en
cada petición al backend, la autenticación de nuestro usuario será almacenado en el `SecurityContext`, desde donde
luego obtendremos la inyección que vemos en el método anterior.

Necesitamos el `Authentication`, ya que la entidad `Book` necesita almacenar en una de sus columnas la clave foránea
del usuario. Y precisamente el `User` lo obtendremos de la `Authentication`.

Ahora, vamos a crear el record `BookRequest`. Este record incluirá el `id`, ya que lo usaremos tanto para la
actualización como para el guardado de la entidad.

````java
public record BookRequest(Long id,

                          @NotBlank(message = "100")
                          String title,

                          @NotBlank(message = "101")
                          String authorName,

                          @NotBlank(message = "102")
                          String isbn,

                          @NotBlank(message = "103")
                          String synopsis,

                          boolean shareable) {
}
````

Es importante notar que en las propiedades `message` de las anotaciones hemos definido un número distinto. Esto es
porque en el frontend definiremos una especia de `diccionario de mensajes` para mostrar un mensaje en función del
código.

Ahora crearemos la clase de servicio donde implementaremos la lógica para guardar un book. Hay que tener en cuenta
que como la entidad `Book` se relaciona con la entidad `User`, debemos pasarle a la entidad `Book` la instancia de
la entidad `User` obtenida del `Authentication`.

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Long save(BookRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = this.bookMapper.toBook(request);
        book.setOwner(user);
        return this.bookRepository.save(book).getId();
    }
}
````

La clase de servicio anterior utiliza una clase mapper que hemos creado para convertir el record obtenido en el request
a un entity:

````java

@Component
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .isbn(request.isbn())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }
}
````

Además, la clase de servicio llama al repositorio `BookRepository` para finalizar con el registro del book en la base
de datos.

````java
public interface BookRepository extends JpaRepository<Book, Long> {
}
````

## Implementa el método find book by id

Antes de implementar el método `findBookById` vamos a actualizar la entidad `Book` agregándole un método que nos
permitirá calcular el `rate` de las notas que los usuarios agreguen en la entidad `Feedback`.

````java

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book extends BaseEntity {
    /* other properties */

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @Transient
    public double getRate() {
        if (this.feedbacks == null || this.feedbacks.isEmpty()) {
            return 0D;
        }
        double rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0D);
        return Math.round(rate * 10D) / 10D;
    }
}
````

Necesitamos también, crear una clase que contendrá la información del `Book` que será enviada como respuesta al cliente:

````java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;
    private byte[] cover;
    private double rate;
    private boolean archived;
    private boolean shareable;
}
````

En la clase `BookMapper` agregaremos otro método que nos permitirá hacer la conversión de una entidad `Book` hacia
la clase de respuesta `BookResponse`:

````java

@Component
public class BookMapper {
    /* another method */

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                // TODO: implement this later
                //.cover()
                .build();
    }
}
````

Ahora, en nuestra clase de servicio `BookService` agregaremos el método `findById()` para implementar la lógica de
búsqueda de un book:

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /* another medhod */

    public BookResponse findById(Long bookId) {
        return this.bookRepository.findById(bookId)
                .map(this.bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No se encuentra el libro con el id " + bookId));
    }
}
````

Finalmente, agregamos el endpoint en el controlador `BookController`:

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    @GetMapping(path = "/{bookId}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(this.bookService.findById(bookId));
    }

    /* another endpoint */
}
````

## Implementa la búsqueda de todos los books visualizables

Retornaremos al cliente el conjunto de books visualizables usando paginación, pero para eso necesitamos crear una clase
genérica que envolverá las propiedades de la paginación, es decir no vamos a retornar al cliente directamente el `Page`,
sino más bien, crearemos una clase que desde el backend nos permitirá seleccionar las propiedades de la paginación a
retornar.

````java

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
````

Ahora crearemos el repositorio `BookRepository` donde definiremos un método personalizado para recuperar los books
visualizables:

````java
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            SELECT b
            FROM Book AS b
            WHERE b.archived = false AND
                    b.shareable = true AND
                    b.owner.id != :userId
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Long userId);
}
````

En la clase de servicio definiremos el método findAllBooks, pasándole parámetros como el page, size y authentication
que son enviados desde el controlador:

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> bookPage = this.bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = bookPage.stream()
                .map(this.bookMapper::toBookResponse)
                .toList();

        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .first(bookPage.isFirst())
                .last(bookPage.isLast())
                .build();
    }

    /* other methods */
}
````

Finalmente, definimos el controller junto al endponit para listar los libros que son visibles:

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(@RequestParam(defaultValue = "0", required = false) int page,
                                                                   @RequestParam(defaultValue = "10", required = false) int size,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllBooks(page, size, authentication));
    }
    /* other methods */
}
````

## Implementa la búsqueda de todos los books por owner

Iniciamos creando el controlador con el endpoint para consultar los books por su owner. Notar que no enviamos el id del
owner (del usuario), ya que lo obtendremos de la propia autenticación.

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    @GetMapping(path = "/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(@RequestParam(defaultValue = "0", required = false) int page,
                                                                          @RequestParam(defaultValue = "10", required = false) int size,
                                                                          Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllBooksByOwner(page, size, authentication));
    }

    /* other endpoints */
}
````

Ahora, antes de implementar la clase de servicio con el método `findAllBooksByOwner` necesitamos crear una clase
de especificación, para realizar consultas dinámicas. En esta oportunidad no vamos a crear una consulta personalizada
como se hizo anteriormente, si no más bien utilizaremos la interfaz `Specification<T>` para poder generar una consulta
dinámica, es decir nos apoyaremos del API de Criteria. Para más información sobre este tema visitar mi propio
repositorio [spring-data-jpa-specifications](https://github.com/magadiflo/spring-data-jpa-specifications).

A continuación se muestra el método `withOwnerId` que retorna una instancia de `Speficication` encapsulando la lógica
de predicado basada en criterios. En este caso, estamos igualando el "ownerId" pasado por parámetro con el atributo
"owner.id":

````java
public class BookSpecification {
    public static Specification<Book> withOwnerId(Long ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
````

A continuación implementamos el método `findAllBooksByOwner` en la clase de servicio:

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /* another method */

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> bookPage = this.bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = bookPage.stream()
                .map(this.bookMapper::toBookResponse)
                .toList();

        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .first(bookPage.isFirst())
                .last(bookPage.isLast())
                .build();
    }

    /* other methods */
}
````

Observemos que el método anterior, está usando la clase de especificación que creamos, además, esa especificación la
estamos usando junto con la interfaz `Pageable` dentro del método `findAll` del `BookRepository`. Para que eso funcione
es importante extender al repositorio la interfaz `JpaSpecificationExecutor<>`:

````java
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    /* A method here */
}
````

## Implementa la búsqueda de todos los libros prestados

Empezamos definiendo un nuevo endpoint en el controlador `BookController` que nos permitirá mostrar todos los libros
que han sido prestados para el usuario que está conectado:

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other endpoints */

    @GetMapping(path = "/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(@RequestParam(defaultValue = "0", required = false) int page,
                                                                                   @RequestParam(defaultValue = "10", required = false) int size,
                                                                                   Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllBorrowedBooks(page, size, authentication));
    }


    /* other endpoints */
}
````

Implementamos el método del servicio que llamará al repositorio del `BookTransactionHistory`:

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = this.transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(this.bookMapper::toBorrowedBookResponse)
                .toList();
        return PageResponse.<BorrowedBookResponse>builder()
                .content(bookResponses)
                .number(allBorrowedBooks.getNumber())
                .size(allBorrowedBooks.getSize())
                .totalElements(allBorrowedBooks.getTotalElements())
                .totalPages(allBorrowedBooks.getTotalPages())
                .first(allBorrowedBooks.isFirst())
                .last(allBorrowedBooks.isLast())
                .build();
    }
    /* other methods */
}
````

Para implementar el método anterior es necesario crear las siguientes clases y repositorios. Empezamos creando la
clase `BorrowedBookResponse` donde agruparemos propiedades que serán retornadas al cliente. Las propiedades que tendrá
esta clase estarán conformadas por propiedades del `Book` y del `BookTransactionHistory`:

````java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedBookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private double rate;
    private boolean returned;
    private boolean returnApproved;
}
````

Definimos un método en la clase `BookMapper` para mapear una entidad `BookTransactionHistory` en una
clase `BorrowedBookResponse`:

````java

@Component
public class BookMapper {
    /* other methods */

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
````

Creamos el repositorio de la entidad `BookTransactionHistory` donde definiremos un método personalizado para consultar
las entidades por el id del usuario:

````java
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

    @Query("""
            SELECT history
            FROM BookTransactionHistory AS history
            WHERE history.user.id = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long userId);
}
````

## Implementa el findAllReturnedBooks

Empezamos implementando el controlador con el nuevo endpoint:

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other methods */

    @GetMapping(path = "/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(@RequestParam(defaultValue = "0", required = false) int page,
                                                                                   @RequestParam(defaultValue = "10", required = false) int size,
                                                                                   Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllReturnedBooks(page, size, authentication));
    }
    /* other methods */
}
````

En la capa de servicio, implementamos un método para la lógica de negocio del endpoint anterior:

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = this.transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(this.bookMapper::toBorrowedBookResponse)
                .toList();
        return PageResponse.<BorrowedBookResponse>builder()
                .content(bookResponses)
                .number(allBorrowedBooks.getNumber())
                .size(allBorrowedBooks.getSize())
                .totalElements(allBorrowedBooks.getTotalElements())
                .totalPages(allBorrowedBooks.getTotalPages())
                .first(allBorrowedBooks.isFirst())
                .last(allBorrowedBooks.isLast())
                .build();
    }

    /* other methods */
}
````

Finalmente, en el repositorio `BookTransactionHistoryRepository` creamos un método personalizado:

````java
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

    /* another method */

    @Query("""
            SELECT history
            FROM BookTransactionHistory AS history
            WHERE history.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long userId);
}
````

## Implementa la actualización del status de los books compartibles

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other methods */

    @PatchMapping(path = "/shareable/{bookId}")
    public ResponseEntity<Long> updateShareableStatus(@PathVariable Long bookId, Authentication authentication) {
        return ResponseEntity.ok(this.bookService.updateShareableStatus(bookId, authentication));
    }
}
````

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */

    public Long updateShareableStatus(Long bookId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + bookId));
        // El status del libro solo puede ser actualizado por el dueño del propio libro
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes actualizar el estado del libro para compartir");
        }
        book.setShareable(!book.isShareable());
        this.bookRepository.save(book);
        return bookId;
    }
}
````

En la clase de servicio anterior estamos lanzando una excepción personalizada cuando un usuario que no es dueño del
libro trata de actualizar el atributo shareable. La excepción personalizada la creamos tal como sigue:

````java
public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String message) {
        super(message);
    }
}
````

Finalmente, la excepción personalizada la tenemos que manejar dentro de la clase de excepciones globales:

````java

@RestControllerAdvice
public class GlobalExceptionHandler {
    /* other methods */
    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}
````

## Implementa la actualización del status de los books archivados

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other methods */
    @PatchMapping(path = "/archived/{bookId}")
    public ResponseEntity<Long> updateArchivedStatus(@PathVariable Long bookId, Authentication authentication) {
        return ResponseEntity.ok(this.bookService.updateArchivedStatus(bookId, authentication));
    }
}
````

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */
    public Long updateArchivedStatus(Long bookId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + bookId));
        // El status del libro solo puede ser actualizado por el dueño del propio libro
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes actualizar el estado del libro para archivar");
        }
        book.setArchived(!book.isArchived());
        this.bookRepository.save(book);
        return bookId;
    }
}
````

## Implementar la característica de préstamo de libro

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;
    /* other methods */

    @PostMapping(path = "/borrow/{bookId}")
    public ResponseEntity<Long> borrowBook(@PathVariable Long bookId, Authentication authentication) {
        return ResponseEntity.ok(this.bookService.borrowBook(bookId, authentication));
    }

    /* other methods */
}
````

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */

    public Long borrowBook(Long bookId, Authentication authentication) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + bookId));
        // Si el book está archivado o no es compartible, entonces lanzamos una excepción de operación no permitida
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("El libro solicitado no se puede tomar prestado porque está archivado o no se puede compartir");
        }

        User user = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes pedir prestado tu propio libro");
        }

        final boolean isAlreadyBorrowed = this.transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("El libro solicitado ya está prestado");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return this.transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
}
````

````java
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

    /* other queries */
    @Query("""
            SELECT (COUNT(*) > 0) AS isBorrowed
            FROM BookTransactionHistory AS history
            WHERE history.user.id = :userId AND
                    history.book.id = :bookId AND
                    history.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(Long bookId, Long userId);
}
````

## Implementa devolución de libro prestado

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other methods */

    @PatchMapping(path = "/borrow/return/{bookId}")
    public ResponseEntity<Long> returnBorrowBook(@PathVariable Long bookId, Authentication authentication) {
        return ResponseEntity.ok(this.bookService.returnBorrowBook(bookId, authentication));
    }
}
````

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */

    public Long returnBorrowBook(Long bookId, Authentication authentication) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("El libro solicitado no se puede tomar prestado porque está archivado o no se puede compartir");
        }

        User user = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes pedir prestado o retornar tu propio libro");
        }

        BookTransactionHistory bookTransactionHistory = this.transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("No tomaste prestado este libro"));
        bookTransactionHistory.setReturned(true);

        return this.transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
}
````

````java
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

    /* other queries */

    @Query("""
            SELECT history
            FROM BookTransactionHistory AS history
            WHERE history.user.id = :userId AND
                    history.book.id = :bookId AND
                    history.returned = false AND
                    history.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long userId);
}
````

## Implementa la aprobación del libro prestado

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other methods */

    @PatchMapping(path = "/borrow/return/approved/{bookId}")
    public ResponseEntity<Long> approvedReturnBorrowBook(@PathVariable Long bookId, Authentication authentication) {
        return ResponseEntity.ok(this.bookService.approvedReturnBorrowBook(bookId, authentication));
    }
}
````

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;

    /* other methods */

    public Long approvedReturnBorrowBook(Long bookId, Authentication authentication) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("El libro solicitado no se puede tomar prestado porque está archivado o no se puede compartir");
        }

        User user = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes pedir prestado o retornar tu propio libro");
        }

        BookTransactionHistory bookTransactionHistory = this.transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("Los libros aún no han sido devueltos. No puedes aprobar su devolución"));
        bookTransactionHistory.setReturnApproved(true);

        return this.transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
}
````

````java
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
    /* other queries */
    @Query("""
            SELECT history
            FROM BookTransactionHistory AS history
            WHERE history.book.owner.id = :userId AND
                    history.book.id = :bookId AND
                    history.returned = true AND
                    history.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long userId);
}
````

## Implementa la carga de imágenes de portada de libros

Empezaremos configurando la ruta en el archivo `application-dev.yml`:

````yml
application:
  file:
    upload:
      photos-output-path: ./uploads
````

Definimos el controlador con el siguiente endpoint:

````java

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    /* other methods */

    @PostMapping(path = "/cover/{bookId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadBookCoverPicture(@PathVariable Long bookId, @Parameter @RequestPart MultipartFile file, Authentication authentication) {
        this.bookService.uploadBookCoverPicture(bookId, file, authentication);
        return ResponseEntity.accepted().build();
    }
}
````

**DONDE**

- `@Parameter`, es una anotación de swagger importado del siguiente paquete `io.swagger.v3.oas.annotations`. Esta
  anotación se utiliza en el contexto de la documentación de APIs con OpenAPI (anteriormente conocida como Swagger).
- El propósito de `@Parameter` es describir detalles de los parámetros que se pasan a los métodos de tu controlador,
  facilitando la generación de la documentación de tu API. En tu caso, proporciona información adicional sobre el
  parámetro `file` de tipo `MultipartFile`.


- La anotación `@RequestPart` se utiliza en Spring Boot para indicar que un parámetro de un método del controlador debe
  ser extraído de una parte específica del cuerpo de una solicitud multipart (por ejemplo, un archivo cargado). Es
  particularmente útil cuando trabajas con solicitudes que contienen datos multipart, como archivos subidos y otros
  datos de formulario al mismo tiempo.
- En el contexto de tu método `uploadBookCoverPicture`, la anotación `@RequestPart` se aplica al parámetro `file`, que
  es de tipo `MultipartFile`. Esto le dice a Spring que debe mapear una parte específica del cuerpo de la solicitud
  multipart a ese parámetro.

Ahora, en la clase de servicio implementamos el método que definirá la lógica para poder almacenar la imagen en el
servidor y su path en la base de datos. Para eso luego nos vamos a apoyar de una clase de servicio al que le llamaremos
`FileStorageService`:

````java

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final BookMapper bookMapper;
    private final FileStorageService fileStorageService;

    /* other methods */

    public void uploadBookCoverPicture(Long bookId, MultipartFile file, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + bookId));
        String bookCover = this.fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);

        this.bookRepository.save(book);
    }
}
````

Finalmente, se muestra a continuación nuestra clase que tendrá la función de subir la imagen a una ruta específica
dentro del servidor:

````java

@Slf4j
@RequiredArgsConstructor
@Service
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile file, @Nonnull Long userId) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return this.uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile file, @Nonnull String fileUploadSubPath) {
        final String finalUploadPath = this.fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();

            if (!folderCreated) {
                log.warn("Falló al crear el folder de destino (target)");
                return null;
            }
        }

        final String fileExtension = this.getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try {
            Files.write(targetPath, file.getBytes());
            log.info("Archivo guardado en {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("El archivo no pudo ser guardado", e);
        }
        return null;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";

        int lastDotIndex = filename.lastIndexOf(".");

        if (lastDotIndex == -1) return "";

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
````

## Lee el file desde una ubicación determinada

Recordemos que en nuestra clase de conversión `BookMapper` tenemos un método que convierte una entidad `Book` a un
dto `BookResponse`. Este dto tiene un atributo llamado `cover` del tipo `byte[]` que nos falta mapear. Para rellenar
este campo con el valor de la imagen en byte[], necesitamos crear una clase donde definiremos un método que nos
permitirá leer la imagen almacenada en el servidor y convertirla a byte[]:

````java

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String fileUrl) {
        if (StringUtils.isNotBlank(fileUrl)) {
            try {
                Path filePath = new File(fileUrl).toPath();
                return Files.readAllBytes(filePath);
            } catch (IOException e) {
                log.warn("No se encontró el archivo en la ruta {}", fileUrl);
            }
        }
        return null;
    }
}
````

Ahora que tenemos la clase de utilidad con el método que nos retorna un byte[] de la imagen, vamos a usarlo para
rellenar el campo cover del dto BookResponse:

````java

@Component
public class BookMapper {
    /* another method */
    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover())) //<-- Rellenando campo
                .build();
    }
    /* another method */
}
````

## Implementando el save de la entidad Feedback

````java

@Tag(name = "Feedback", description = "API Rest de la entidad Feedback")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Long> saveFeedback(@Valid @RequestBody FeedbackRequest request, Authentication authentication) {
        return new ResponseEntity<>(this.feedbackService.save(request, authentication), HttpStatus.CREATED);
    }

}
````

````java

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeebackRepository feebackRepository;
    private final FeedbackMapper feedbackMapper;

    public Long save(FeedbackRequest request, Authentication authentication) {
        Book book = this.bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + request.bookId()));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("No puedes dar comentarios sobre un libro archivado o que no se puede compartir");
        }

        User user = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes dar comentarios sobre tu propio libro");
        }

        Feedback feedback = this.feedbackMapper.toFeedback(request);
        return this.feebackRepository.save(feedback).getId();
    }
}
````

````java

@Component
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder().id(request.bookId()).build())
                .build();
    }
}
````

````java
public record FeedbackRequest(@Positive(message = "200")
                              @Min(value = 0, message = "201")
                              @Max(value = 5, message = "202")
                              Double note,

                              @NotBlank(message = "203")
                              String comment,

                              @NotNull(message = "204")
                              Long bookId) {
}
````

````java
public interface FeebackRepository extends JpaRepository<Feedback, Long> {
}
````

## Implementa en listado de todos los feedbacks

````java

@Tag(name = "Feedback", description = "API Rest de la entidad Feedback")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping(path = "/book/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(@PathVariable Long bookId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                Authentication authentication) {
        return ResponseEntity.ok(this.feedbackService.findAllFeedbackByBook(bookId, page, size, authentication));
    }

    /* another method */
}
````

Si observamos los parámetros del método anterior, veremos que la anotación `@RequestParam` está definida de la
siguiente manera `@RequestParam(defaultValue = "0") int page`, es decir, no estamos usando el atributo `required` tal
como lo hacíamos en los métodos del Book Controller `@RequestParam(defaultValue = "0", required = false) int page`,
esto no es ningún problema, ya que cuando se usa el atributo `defaultValue`, implícitamente se establece el atributo
`required` en `false`.

````java

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeebackRepository feebackRepository;
    private final FeedbackMapper feedbackMapper;

    /* other methods */
    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Long bookId, int page, int size, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        User user = (User) authentication.getPrincipal();
        Page<Feedback> feedbackPage = this.feebackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbackPage.stream()
                .map(feedback -> this.feedbackMapper.toFeedbackResponse(feedback, user.getId()))
                .toList();
        return PageResponse.<FeedbackResponse>builder()
                .content(feedbackResponses)
                .number(feedbackPage.getNumber())
                .size(feedbackPage.getSize())
                .totalElements(feedbackPage.getTotalElements())
                .totalPages(feedbackPage.getTotalPages())
                .first(feedbackPage.isFirst())
                .last(feedbackPage.isLast())
                .build();
    }
}
````

````java
public interface FeebackRepository extends JpaRepository<Feedback, Long> {
    @Query("""
            SELECT f
            FROM Feedback AS f
            WHERE f.book.id = :bookId
            """)
    Page<Feedback> findAllByBookId(Long bookId, Pageable pageable);
}
````

````java

@Component
public class FeedbackMapper {

    /* another method */

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Long userId) {
        boolean itIsOwnFeedback = Objects.equals(feedback.getBook().getOwner().getId(), userId);
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(itIsOwnFeedback)
                .build();
    }
}
````

````java

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private Double note;
    private String comment;
    private boolean ownFeedback;
}
````
