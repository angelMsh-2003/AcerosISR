****# Aceros ISR

# Es un aplicacion movil (solo disponible para android) que busca administrar las tareas, entradas y salidas del inventario, gestion de los proyectos.

## Instrucciones:
Este readme es un contexto para la IA, considera lo siguiente:
-- Lee completo el readme
-- Siempre manten actualizado este documento agregando las peticiones nuevas requeridas
-- ahora el proyecto es full android

## Base de datos:
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

CREATE TABLE Materiales (
material_id SERIAL PRIMARY KEY,
tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('tubo','soldadura','lamina','herramienta','otro')),
nombre VARCHAR(50) NOT NULL,
unidad_medida VARCHAR(10) NOT NULL CHECK (unidad_medida IN ('pieza','kg','ton','m','lt')),
descripcion TEXT,
stock_actual NUMERIC(12,2) DEFAULT 0 CHECK (stock_actual >= 0)
);

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

CREATE TABLE Proyectos (
proyecto_id SERIAL PRIMARY KEY,
descripcion TEXT NOT NULL,
cliente VARCHAR(100) NOT NULL,
fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
registrado_por INT REFERENCES Empleados(empleado_id),
estado VARCHAR(20) DEFAULT 'En progreso' CHECK (estado IN ('En progreso','Finalizado','Cancelado'))
);

CREATE TABLE Tareas (
tarea_id SERIAL PRIMARY KEY,
proyecto_id INT NOT NULL REFERENCES Proyectos(proyecto_id) ON DELETE CASCADE,
empleado_id INT REFERENCES Empleados(empleado_id),
estado VARCHAR(15) DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente','En proceso','Completada')),
fecha_inicio DATE,
fecha_fin DATE,
comentarios TEXT
);

CREATE TABLE ConsumoMaterial (
consumo_id SERIAL PRIMARY KEY,
tarea_id INT NOT NULL REFERENCES Tareas(tarea_id) ON DELETE CASCADE,
material_id INT NOT NULL REFERENCES Materiales(material_id),
cantidad_usada NUMERIC(12,2) NOT NULL CHECK (cantidad_usada > 0),
fecha_uso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
observaciones TEXT
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

## Frontend
-- UI de inicio de sesion
-- UI de registro de usuarios
-- UI de menu de inicio
-- El menu de inicio tiene las siguientes opciones:
--- Para usuario primario/admin: Proyectos, Materiales, Tareas, Inventario
--- Para usuario basico: Tareas, Inventario

## Flujo basico de la aplicacion:
-- Usuario primario/basico/admin inicia sesion
-- Usuario primario/admin, en el apartado de **Materiales**, puede registar un nuevo material (primeras entradas), se alamcena esa informacion (contamos ahora con stock)
-- En **Proyectos**, usuario primario/admin registrar un nuevo proyecto y puede ligar diferentes tareas a otros usuarios de menor rango (llenando el apartado de **tareas**)
-- Cualquier movimiento de entrada y salida de material se registra en **movimientosmaterial**
-- Usuario basico puede solo ver sus tareas, y modificar en tres estados dentro de **tareas** (TODO, INPROGRES, DONE)   
-- Cuando usuario basico finalizarla su tarea (DONE), llena los campos de **consumomaterial** para registrar salidas de stock
-- Cuando un proyecto en **proyectos** finalia todas sus tareas, primario/admin puede cerrar el proyecto o asignar mas tareas
-- Si un proyecto se cierra, genera un reporte que se optiene de movimientosmaterial

