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

