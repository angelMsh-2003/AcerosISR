# 🏗️ Aceros IRS - Gestión Inteligente para Herrerías

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?logo=kotlin&style=for-the-badge)
![Android](https://img.shields.io/badge/Platform-Android-green?logo=android&style=for-the-badge)
![Cloud](https://img.shields.io/badge/Backend-Google_Cloud_Run-blue?logo=googlecloud&style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Active_Development-orange?style=for-the-badge)

> **Aplicación móvil empresarial para la gestión integral de inventarios, ventas y procesos operativos, desarrollada bajo una arquitectura moderna Backend-Driven y desplegada 100% en la nube.**

---

## 📱 Visión General del Proyecto

**Aceros IRS** es una aplicación nativa de Android creada para resolver problemas reales de operación en una herrería: control de inventarios, seguimiento de ventas, gestión de compras y coordinación de tareas internas.

El proyecto evolucionó de una solución **local y monolítica (SQLite)** hacia una **arquitectura distribuida basada en microservicios**, permitiendo:

- Sincronización en tiempo real entre dispositivos  
- Escalabilidad horizontal  
- Separación clara entre frontend, backend y datos  

---

## 📸 Galería de Interfaz (UI Showcase)

| Dashboard Principal | Gestión de Inventario | Registro de Tareas |
|:-------------------:|:---------------------:|:------------------:|
| ![Home](link_a_tu_imagen_1.png) | ![Stock](link_a_tu_imagen_2.png) | ![Ventas](link_a_tu_imagen_3.png) |
| *Vista resumen con KPIs* | *Control de stock en tiempo real* | *Flujo de caja y tickets* |

---

## 🧠 Arquitectura General
- Android App (Kotlin)
- HTTPS / REST
- Backend API (Flask + Gunicorn)
- ORM (SQLAlchemy)
- PostgreSQL (Cloud SQL)


**Principios aplicados**
- API-First Design  
- Separación de responsabilidades  
- Backend stateless  
- Infraestructura cloud-native  

---

## 🚀 Ingeniería Frontend (Android & Kotlin)

### Enfoque técnico
- **Arquitectura MVVM** para desacoplar UI, lógica de negocio y acceso a datos.
- **Jetpack Compose + Material 3** para una UI declarativa, moderna y mantenible.
- **Coroutines + Flow** para manejo asíncrono, estados reactivos y carga incremental.
- **Retrofit + OkHttp** para consumo seguro y tipado de APIs REST.
- **Manejo de estados complejos** (loading, success, error) orientado a UX real.

### Casos reales resueltos
- Formularios con validaciones dinámicas
- Listados con paginación y filtros
- Dashboards con agregaciones de datos
- Manejo de roles (admin / empleado)

---

## ☁️ Backend & Microservicios

El backend fue diseñado con una mentalidad **cloud-ready y escalable**, no como un simple CRUD.

### Tecnologías clave
- Python + Flask  
- Gunicorn como servidor WSGI  
- SQLAlchemy ORM  
- PostgreSQL (Cloud SQL)  
- Docker  
- Google Cloud Run  

### Características técnicas
- API REST versionada
- Serialización y validación de payloads JSON
- Manejo de errores estandarizado
- Variables de entorno para configuración segura
- Servicios stateless (ideal para auto-scaling)

### Diseño de datos
- Modelado relacional con llaves foráneas
- Integridad referencial
- Enumeraciones controladas (CHECK constraints)
- Separación entre entidades operativas:
  - Materiales
  - Movimientos
  - Proyectos
  - Usuarios

---

## 🔐 Seguridad & Buenas Prácticas

- Credenciales gestionadas por variables de entorno
- No secrets hardcodeados
- Separación de ambientes (local / cloud)
- Uso de HTTPS en producción
- Control básico de roles desde backend

---

## 🔄 Migración y Retos Técnicos

### Problemas reales abordados
- ❌ Desincronización entre dispositivos  
- ❌ Datos duplicados  
- ❌ Escalabilidad nula  

### Soluciones implementadas
- Migración a PostgreSQL centralizado
- Normalización de esquemas
- API REST como fuente única de verdad
- Contenerización completa del backend
- Despliegue automatizado en Cloud Run  

**Resultado:**  
Un sistema estable, replicable y alineado a estándares profesionales de backend moderno.

---

## 🧩 Stack Tecnológico

### Frontend
- Kotlin
- Jetpack Compose
- Retrofit
- Coroutines / Flow
- Material Design 3

### Backend
- Python
- Flask
- SQLAlchemy
- Gunicorn

### Infraestructura
- Docker
- Google Cloud Run
- Cloud SQL (PostgreSQL)
- Google Artifact Registry

## 👨‍💻 Autor

**Angel Manuel Sánchez Hipólito**

- 💼 LinkedIn: _(www.linkedin.com/in/angelmanuelsh)_  
- 📧 Email: _(angelmanuel203sh@gmail.com)_  

---

> *Proyecto en desarrollo activo, enfocado en calidad de ingeniería más que en prototipo.*
