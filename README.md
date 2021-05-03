# Practica 6.

## Contexto de la práctica

Un portal de Internet, gestiona unas cuentas de usuario y se pide
`desarrollar la funcionalidad de cambio de password`, una vez el 
usuario esta autenticado.

## ¿Que se tiene que desarrollar?

- [ ] Revisar `WebSecurityConfig` para permitir el acceso al `/h2` y la configuracion via `application.properties`
- [ ] Desarrolla la funcionalidad de cambio de password a nivel de: `html`, `javascript`, `restcontroller` y `service` 
  reutilizando el repository `UserDetailRepository` para gestionar la actualizacion del password.
- [ ] Revisar que los tests pasan en la clase de tests: `UserControllerE2ETest` via `mvn clean test`
- [ ] Revisar que la vista `html` que cambia el password verifica la existencia del token. 

## Notas de desarrollo

Se puede recuperar el usuario autenticado de esta manera:

``` java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
String currentPrincipalName = authentication.getName();
```
 
## ¿Que contiene el proyecto?

El proyecto carga una base de datos de usuarios y la seguridad
se gestiona via JWT.

```sql
CREATE TABLE IF NOT EXISTS `user_details` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` tinyint(10) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ;
```

```java 
@Data
@Table("USER_DETAILS")
public class UserDetail {

    @Id
    private Long user_id;
    private String username;
    private String first_name;
    private String last_name;
    private String gender;
    private String password;
    private Integer status;

}
```

## Como usar el proyecto

```
mvn clean spring-boot:run
```

```
http://localhost:8080/index.html
```

- User: rogers63
- Password: password
