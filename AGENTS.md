****# Aceros ISR

# Es un aplicacion movil (solo disponible para android) que busca administrar las tareas, entradas y salidas del inventario, gestion de los proyectos.

## Instrucciones:
Este readme es un contexto para la IA, considera lo siguiente:
-- Lee completo el readme
-- Siempre manten actualizado este documento agregando las peticiones nuevas requeridas
-- ahora el proyecto es full android

## Arquitectura y Estructura del Proyecto
El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** con una capa de **Repositorio** para el manejo de datos, desacoplando la lógica de negocio de la fuente de datos.

### Árbol de Directorios (frontend Kotlin para android)
```
app/src/main/java/com/example/acerosisr
├── Data/
│   ├── ApiService.kt
│   ├── TareasRepository.kt
│   └── UserRepository.kt
├── Funtion/
│   ├── Alert.kt
│   ├── CampoAsignacionTareas.kt
│   ├── ColorsSegmentedButton.kt
│   ├── DatePickerModal.kt
│   ├── DesignType.kt
│   ├── HeaderPrincipal.kt
│   └── MenuDrawer.kt
├── Model/
│   ├── Empleados.kt
│   ├── InfoCampo.kt
│   ├── InfoViewAlert.kt
│   ├── Material.kt
│   ├── SelectActualUser.kt
│   └── Tareas.kt
├── Navigation/
│   ├── AppScreen.kt
│   └── Navigation.kt
├── other/
│   └── AppCloser.kt
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── View/
│   ├── Apartados/
│   │   ├── Materiales/
│   │   ├── NuevoEmpleado/
│   │   ├── ProcesoActividades/
│   │   ├── Proyectos/
│   │   └── Tareas/
│   ├── Inicio/
│   │   ├── InicioApp.kt
│   │   ├── Login.kt
│   │   └── Registro.kt
│   └── Menu/
│       └── MenuInicio.kt
├── ViewModel/
│   ├── AppViewModelFactory.kt
│   ├── TareasViewModel.kt
│   └── UserViewModel.kt
└── MainActivity.kt
```

## Consideraciones para el Backend (Google Cloud Run)
La aplicación Android ahora actúa como un cliente para un backend en la nube. Toda la lógica de negocio pesada y el acceso a la base de datos deben residir en el servidor.

### 1. Seguridad de Contraseñas
- **El backend es el único responsable** de la gestión de contraseñas.
- El cliente (la app Android) debe enviar la contraseña en **texto plano vía HTTPS**.
- El backend debe:
    - Generar un `salt` aleatorio por cada usuario.
    - Hashear la contraseña con el `salt` usando un algoritmo robusto (ej. Argon2, scrypt, BCrypt).
    - Almacenar el `password_hash` y el `salt` en la base de datos.
    - Realizar la validación de la contraseña durante el login comparando los hashes.

### 2. Endpoints de la API (Ejemplos)
El backend debe exponer una API RESTful. Los siguientes endpoints son requeridos por el cliente:

**Empleados/Usuarios (`/api/empleados`)**
- `POST /api/empleados`: Registrar un nuevo empleado. El body debe aceptar `NombreEmpleado`, `Cargo`, `Correo`, `PasswordPlaintext`.
- `GET /api/empleados/count`: Contar el número total de empleados.
- `GET /api/empleados/{numEmpleado}`: Obtener detalles de un empleado.
- `PUT /api/empleados/{numEmpleado}`: Actualizar datos de un empleado (ej. `Correo`, `Estado`, `PasswordPlaintext`).
- `POST /api/empleados/login`: Autenticar a un usuario. El body debe aceptar `numEmpleado` y `passwordPlaintext`.

**Tareas (`/api/tareas`)**
- `GET /api/tareas`: Obtener una lista de todas las tareas.
- `POST /api/tareas`: Crear una nueva tarea.
- `GET /api/tareas/{taskId}`: Obtener detalles de una tarea específica.
- `PUT /api/tareas/{taskId}`: Actualizar una tarea.
- `DELETE /api/tareas/{taskId}`: Eliminar una tarea.
- `PUT /api/tareas/{taskId}/assign`: Asignar una tarea a un empleado (ej. body: `{"empleadoId": 123}`).
- `PUT /api/tareas/{taskId}/status`: Actualizar el estado de una tarea (ej. body: `{"estado": "Completada"}`).

**Gestión de Sesión (Ejemplo)**
- `GET /api/actual-user`: Obtener información del usuario actualmente logueado (si se usa sesión en el backend).
- `POST /api/actual-user`: Guardar el estado del usuario logueado.
- `DELETE /api/actual-user`: Cerrar la sesión del usuario.

## Otras Configuraciones y Recomendaciones

### 1. Mock Backend para Desarrollo
- `ApiService.kt` contiene una implementación "mock" que se activa cuando la `BASE_URL` en `MainActivity.kt` es `"mock-api-url"`.
- Esto permite desarrollar y probar el frontend sin una conexión real al backend.
- Para conectar al backend real, simplemente cambia la `BASE_URL` a la URL de tu servicio de Cloud Run.

### 2. Serialización JSON
- El `JsonConverter.kt` actual es muy básico y frágil.
- **Se recomienda encarecidamente** migrar a una librería de serialización robusta como `kotlinx.serialization`. Esto requerirá añadir la dependencia en `build.gradle.kts` y anotar las clases de `Model` con `@Serializable`.

### 3. Inyección de Dependencias (DI)
- Actualmente, la instanciación de `ApiService`, los repositorios y la `AppViewModelFactory` se hace manualmente en `MainActivity.kt`.
- Para mejorar la escalabilidad y el testing, se recomienda implementar una solución de DI como **Hilt** (de Google) o Koin.

## Base de datos (Esquema SQL de Referencia)
(El esquema SQL se mantiene como referencia para el diseño de la base de datos del backend)

CREATE TABLE Empleados ( ... );
CREATE TABLE Materiales ( ... );
CREATE TABLE MovimientosMaterial ( ... );
CREATE TABLE Proyectos ( ... );
CREATE TABLE Tareas ( ... );
CREATE TABLE ConsumoMaterial ( ... );
...
