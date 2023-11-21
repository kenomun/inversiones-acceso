# Inversiones Acceso

Este proyecto de Spring Boot proporciona un servicio de acceso a usuarios con funcionalidades como:

- Creacion de un usuario: todo usuario por defecto sera creado con role básico "user" role "1", para dar permisos de "admin" se debe modificar directamente la base de datos con role "2")
- Login: Todos los usuarios que se logeen recibiran un token.
- Listar usuarios registrados (solo con token valido).
- Buscar usuario registrado por id (solo con token valido).
- Eliminar usuario registrado (solo con token valido).
- Logout (servicio deja el token inutilizable).
- CRUD Básico Restful
- Consumo de API Externa
- Cifrado de Datos
- Seguridad en los servicios


## Authors

- [@EugenioMuñoz](https://github.com/kenomun/inversiones-acceso)
- 
## Enlaces Adicionales

- **Jira (Proyecto APP INVERSIONES BACKEND):** [https://kenomun.atlassian.net/jira/software/projects/AIB/boards/1](https://kenomun.atlassian.net/jira/software/projects/AIB/boards/1)

## Requisitos

- Java 8
- Maven 3.x
- PostgreSQL

## Dependencias

- Spring Boot 2.7.16
- PostgreSQL Driver
- Springfox Swagger 3.0.0
- Spring Boot Validation
- Spring Boot Data JPA
- Argon2-jvm 2.5
- jjwt 0.9.1
- Lombok 1.18.24
- JUnit 5.9.1
- Mockito 4.9.0

### Base de Datos

- La aplicación utiliza PostgreSQL. Asegúrate de configurar correctamente la conexión en el archivo `application.properties`.
- para la creación de tabla los script se encuntran en Jira.
- La creacion de la base de datos y tabla se debe hacer de forma manual. (en proceso para que se cree de forma automatica al correr el servicio)
- El ingreso de datos como la creacion de usuario se hara solamente por medio del servicio. en proceso para que se cree de forma automatica al correr el servicio un usuario "usuer" y otro "admin"


## Documentación

La documentación de la API está disponible a través de Swagger UI. Después de iniciar la aplicación, visita:

- http://localhost:8080/swagger-ui/index.html


