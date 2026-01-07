****# Aceros ISR

# Es un aplicacion movil (solo disponible para android) que busca administrar las tareas, entradas y salidas del inventario, gestion de los proyectos.

## Instrucciones:
Este readme es un contexto para la IA, considera lo siguiente:
-- Lee completo el readme
-- Siempre manten actualizado este documento agregando las peticiones nuevas requeridas
-- El proyecto frontend estan en kotlin adaptado solo para android

## Instancia de la base sql clound 
-- aceros-isr
-- CPeZDHIM7>RJN*=>

## Base de datos (debe ir como google clound sql, se genera una instancia):
-- ======================
CREATE TABLE Empleados (
    empleado_id SERIAL PRIMARY KEY,
    num_empleado INTEGER UNIQUE CHECK (num_empleado BETWEEN 0 AND 9999999),
    nombre VARCHAR(50) NOT NULL,
    cargo VARCHAR(20) NOT NULL CHECK (cargo IN ('dueño','admin','trabajador')),
    correo VARCHAR(50),
    password_hash TEXT,
    salt TEXT,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla CurrentUser ya NO es necesaria.
-- 👉 Se maneja la sesión en la capa de aplicación, no en la BD.

-- ======================
-- 2. MATERIALES (STOCK UNIFICADO)
-- ======================
CREATE TABLE Materiales (
    material_id SERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('tubo','soldadura','lamina','herramienta','otro')),
    nombre VARCHAR(50) NOT NULL,
    unidad_medida VARCHAR(10) NOT NULL CHECK (unidad_medida IN ('pieza','kg','ton','m','lt')),
    descripcion TEXT,
    stock_actual NUMERIC(12,2) DEFAULT 0 CHECK (stock_actual >= 0)
);

-- ======================
-- 3. MOVIMIENTOS DE MATERIAL (para FIFO)
-- ======================
CREATE TABLE MovimientosMaterial (
    movimiento_id SERIAL PRIMARY KEY,
    material_id INT NOT NULL REFERENCES Materiales(material_id) ON DELETE CASCADE,
    tipo_movimiento VARCHAR(10) NOT NULL CHECK (tipo_movimiento IN ('entrada','salida')),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cantidad NUMERIC(12,2) NOT NULL CHECK (cantidad > 0),
    costo_unitario NUMERIC(12,2) CHECK (costo_unitario >= 0),
    empleado_id INT REFERENCES Empleados(empleado_id),
    observaciones TEXT
);

-- Para mantener FIFO, cada “salida” se asocia a una o más entradas,
-- lo manejará la lógica de aplicación (o trigger si se quiere automatizar más adelante).

-- ======================
-- 4. PROYECTOS (TRABAJOS)
-- ======================
CREATE TABLE Proyectos (
    proyecto_id SERIAL PRIMARY KEY,
    descripcion TEXT NOT NULL,
    cliente VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    registrado_por INT REFERENCES Empleados(empleado_id),
    estado VARCHAR(20) DEFAULT 'En progreso' CHECK (estado IN ('En progreso','Finalizado','Cancelado'))
);

-- ======================
-- 5. ACTIVIDADES Y TAREAS
-- ======================

CREATE TABLE Tareas (
    tarea_id SERIAL PRIMARY KEY,
    proyecto_id INT NOT NULL REFERENCES Proyectos(proyecto_id) ON DELETE CASCADE,
    empleado_id INT REFERENCES Empleados(empleado_id),
    estado VARCHAR(15) DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente','En proceso','Completada')),
    fecha_inicio DATE,
    fecha_fin DATE,
    comentarios TEXT
);

-- ======================
-- 6. CONSUMO DE MATERIALES EN TAREAS
-- ======================
CREATE TABLE ConsumoMaterial (
    consumo_id SERIAL PRIMARY KEY,
    tarea_id INT NOT NULL REFERENCES Tareas(tarea_id) ON DELETE CASCADE,
    material_id INT NOT NULL REFERENCES Materiales(material_id),
    cantidad_usada NUMERIC(12,2) NOT NULL CHECK (cantidad_usada > 0),
    fecha_uso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT
);

CREATE TABLE AgentUser (
    id SERIAL PRIMARY KEY,
    num_empleado INT NOT NULL REFERENCES Empleados(num_empleado),
    nombre VARCHAR(50) NOT NULL,
    cargo VARCHAR(20) NOT NULL CHECK (cargo IN ('dueño','admin','trabajador')),
    ip_address VARCHAR(45),
    device_id VARCHAR(200),
    fecha_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


## Tipos de usuarios:
-- ADMIN: Acceso a todo
-- Usuario primario: puede tener acceso a toda la logica de negicio, (registrar inventario, tareas, fin de proyectos)
-- Usuario basico: solo puede ver sus tareas (que otra persona le asigno) e intentario

## Reglas de negocio
-- (Crear nuevos usuarios): Cualquier persona puede crear un usuario solo con rol basico, un usuario primario o admin puede cambiar de rango a otros usuasrios.
-- (Eliminar usuarios): Un usuario con rol 'admin' o 'primario' puede eliminar cuentas de usuarios basicos
-- (Editar usuarios): Cualquier usuario puede modificar sus datos (nombre, cargo, correo, password)
-- (Tareas): Los usuarios admin/primario pueden crear, editar o borrar tareas.
-- (Asignacion de tareas): Un usuario con rol 'admin' o 'primario' puede asignar tareas a otros usuarios, los usuarios basicos solo pueden marcarlas como TODO, INPROGRES, DONE
-- (Visibilidad de Tareas): Un usuario con rol 'basico' solo puede ver las tareas que le registro otro usuario superior. Un 'admin' o 'primario' puede ver las tareas de todos los empleados.-
-- (Finalizar tareas): El usuario admin/primario/basico pueden marcar sus tareas como DONE
-- (Proyecto): El proyecto se utiliza para gestionar las salidas de stock.
-- (Registro de proyecto): Un usuario con rol 'admin' o 'primario' puede registrar un nuevo proyecto, y asignar tareas a otros usuarios.
-- (Visibilidad de proyecto): Un usuario con rol 'admin' o 'primario' puede ver el progreso de las tareas conforme se van cumpliendo las tareas de los otros usuarios
-- (Fin de un proyecto): Un usuario admin/primario puede finalizar un proyecto solo si todas las tareas estan en DONE. Al final se entrega un pequeño registro de lo que paso en el proyecto (salidas en stock)
-- (Materiales "inventario"): El inventario se utiliza para gestionar las entradas y salidas de materiales.
-- (Movimiento de inventario): Todo se rije bajo la idea de primeras entradas primeras salidas, todo se registra en la tabla **MovimientosMaterial**

## Flujo basico de la aplicacion:
-- Usuario primario/basico/admin inicia sesion
-- Usuario primario/admin, en el apartado de **Materiales**, puede registar un nuevo material (primeras entradas), se alamcena esa informacion (contamos ahora con stock)
-- En **Proyectos**, usuario primario/admin registrar un nuevo proyecto y puede ligar diferentes tareas a otros usuarios de menor rango (llenando el apartado de **tareas**)
-- Cualquier movimiento de entrada y salida de material se registra en **movimientosmaterial**
-- Usuario basico puede solo ver sus tareas, y modificar en tres estados dentro de **tareas** (TODO, INPROGRES, DONE)   
-- Cuando usuario basico finalizarla su tarea (DONE), llena los campos de **consumomaterial** para registrar salidas de stock
-- Cuando un proyecto en **proyectos** finalia todas sus tareas, primario/admin puede cerrar el proyecto o asignar mas tareas
-- Si un proyecto se cierra, genera un reporte que se optiene de movimientosmaterial


## Frontend

### Arquitectura y Estructura del Proyecto
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

## Consideraciones para el Backend (Google Cloud Run y Flask)
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

