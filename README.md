
# API user-manager

Este proyecto es una API RESTful para la gestión de usuarios. En este documento se explica cómo probar la aplicación, tanto de forma automatizada como manual.

**Nota**: Solo el usuario autenticado podrá realizar las acciones de **GET**, **PUT**, **PATCH** y **DELETE** sobre su propia información. Esto significa que un usuario no podrá modificar o eliminar los datos de otro usuario.

## Diagrama de la API
En la carpeta `static` del proyecto se encuentra el archivo **Diagrama-user-manager.png**, que contiene el **diagrama de la API**, el cual describe la arquitectura general de la solución. 
El diagrama de clases del sistema.

## Base de datos H2
El proyecto utiliza **H2** como base de datos en memoria. Para asegurar que las tablas necesarias estén creadas al iniciar la aplicación, se incluye el archivo **schema.sql** en la carpeta `src/main/resources` de la aplicación.

Este archivo contiene el esquema completo de la base de datos, incluyendo las tablas **user** y **phones**, y sus relaciones. Al iniciar la aplicación, **H2** ejecutará automáticamente este script para crear las tablas y las relaciones definidas en el archivo.


## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **H2 Database** (base de datos en memoria)
- **Maven**
- **Swagger UI** para documentación interactiva

## Ejecución de la Aplicación

### Prerrequisitos

- Tener instalado **Java 17**.
- Tener instalado **Maven**.

### Ejecutar la Aplicación

Desde la raíz del proyecto, ejecuta el siguiente comando:

```
mvn spring-boot:run
```

La aplicación se iniciará en el puerto `8080` por defecto.

## Cómo Probar la Aplicación

### Pruebas Automatizadas

El proyecto incluye pruebas unitarias y de integración (por ejemplo, en los archivos `UsuarioControllerTest.java`, `UsuarioServiceImplTest.java`).

Para ejecutar todas las pruebas, utiliza:

```
mvn test
```

### Pruebas Manuales

#### Swagger UI

La documentación interactiva de la API está disponible a través de Swagger. Abre tu navegador y visita:

```
http://localhost:8080/swagger-ui.html
```

Desde allí podrás explorar y probar todos los endpoints, como:

- **POST /user**: Crear un nuevo usuario.
- **GET /user/{id}**: Obtener información de un usuario por su ID.
- **PUT /user{id}**: Actualización completa de un usuario.
- **PATCH /user/{id}**: Actualización parcial de un usuario.
- **DELETE /user/{id}**: Eliminar un usuario.

Nota: Solo el usuario autenticado podrá realizar las acciones de **GET**,**PUT**, **PATCH** y **DELETE** sobre su propia información. Esto significa que un usuario no podrá modificar o eliminar los datos de otro usuario.

#### Postman

Se incluye una colección de Postman en la carpeta `resources` del proyecto. El archivo se llama `User API Collection.postman_collection.json`. Para utilizarlo:

1. Abre Postman.
2. Importa la colección (Archivo > Importar > Selecciona el archivo `User API Collection.postman_collection.json`).
3. La colección incluye las solicitudes para:
   - Crear Usuario
   - Obtener Usuario por ID
   - Reemplazar Usuario
   - Actualizar parcialmente Usuario
   - Eliminar Usuario

Esta colección te permitirá probar los endpoints de forma rápida.


#### Consola H2

La aplicación utiliza una base de datos en memoria H2. Para acceder a la consola:

1. Abre `http://localhost:8080/h2-console` en tu navegador.
2. Configura la conexión usando la siguiente información:
   - **URL:** `jdbc:h2:mem:usuariosdb`
   - **Usuario:** `sa`
   - **Contraseña:** (vacío)
3. Explora las tablas y verifica los datos almacenados.

### Autenticación JWT

A excepción del endpoint de creación de usuario, el resto de endpoints requieren autenticación. Al crear un usuario se genera un token JWT que debes enviar en el header `Authorization` en el siguiente formato:

```
Bearer {token}
```

La colección de Postman incluida ya tiene configuradas las variables necesarias para enviar el token en cada solicitud.

## Resumen de Comandos

- **Ejecutar la aplicación:**  
  ```
  mvn spring-boot:run
  ```

- **Ejecutar pruebas automatizadas:**  
  ```
  mvn test
  ```

- **Acceder a Swagger UI:**  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

- **Acceder a H2 Console:**  
  [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
