# routes/tareas.py
from flask import Blueprint, request, jsonify
from models import Tarea, Proyecto
from db import db
from datetime import datetime


tareas_bp = Blueprint("tareas", __name__)

def millis_to_dt(ms):
    """Convierte millis (Long de Android) -> date (solo fecha)."""
    if ms is None:
        return None
    return datetime.fromtimestamp(ms / 1000.0).date()

def dt_to_millis(dt):
    """Convierte date o datetime -> millis para Android."""
    if not dt:
        return None
    if isinstance(dt, datetime):
        base = dt
    else:  # es date
        base = datetime.combine(dt, datetime.min.time())
    return int(base.timestamp() * 1000)

@tareas_bp.route("/tareas", methods=["GET"])
def listar_tareas():
    tareas = Tarea.query.all()
    return jsonify([
        {
            "tareaId": t.tarea_id,
            "proyectoId": t.proyecto_id,
            "empleadoId": t.empleado_id,
            "estado": t.estado,
            "fechaInicio": dt_to_millis(t.fecha_inicio),
            "fechaFin": dt_to_millis(t.fecha_fin),
            "comentarios": t.comentarios
        } for t in tareas
    ]), 200

@tareas_bp.route("/tareas/<int:tarea_id>", methods=["GET"])
def obtener_tarea(tarea_id):
    t = Tarea.query.get(tarea_id)
    if not t:
        return jsonify({"error": "Tarea no encontrada"}), 404

    return jsonify({
        "tareaId": t.tarea_id,
        "proyectoId": t.proyecto_id,
        "empleadoId": t.empleado_id,
        "estado": t.estado,
        "fechaInicio": dt_to_millis(t.fecha_inicio),
        "fechaFin": dt_to_millis(t.fecha_fin),
        "comentarios": t.comentarios
    }), 200

@tareas_bp.route("/tareas", methods=["POST"])
def crear_tarea():
    data = request.get_json() or {}

    # mínimos obligatorios
    if "proyectoId" not in data:
        return jsonify({"error": "Falta 'proyectoId'"}), 400

    t = Tarea(
        proyecto_id=data["proyectoId"],
        empleado_id=data.get("empleadoId"),
        estado=data.get("estado", "Pendiente"),
        fecha_inicio=millis_to_dt(data.get("fechaInicio")),
        fecha_fin=millis_to_dt(data.get("fechaFin")),
        comentarios=data.get("comentarios")
    )
    db.session.add(t)
    db.session.commit()

    return jsonify({"message": "Tarea creada", "tareaId": t.tarea_id}), 201

# ACTUALIZAR TAREA COMPLETA
@tareas_bp.route("/tareas/<int:tarea_id>", methods=["PUT"])
def actualizar_tarea(tarea_id):
    t = Tarea.query.get(tarea_id)
    if not t:
        return jsonify({"error": "Tarea no encontrada"}), 404

    data = request.get_json() or {}

    t.proyecto_id = data.get("proyectoId", t.proyecto_id)
    t.empleado_id = data.get("empleadoId", t.empleado_id)
    t.estado = data.get("estado", t.estado)

    if "fechaInicio" in data:
        t.fecha_inicio = millis_to_dt(data.get("fechaInicio"))
    if "fechaFin" in data:
        t.fecha_fin = millis_to_dt(data.get("fechaFin"))

    t.comentarios = data.get("comentarios", t.comentarios)

    db.session.commit()
    return jsonify({"message": "Tarea actualizada"}), 200

@tareas_bp.route("/tareas/<int:tarea_id>", methods=["DELETE"])
def borrar_tarea(tarea_id):
    t = Tarea.query.get(tarea_id)
    if not t:
        return jsonify({"error": "Tarea no encontrada"}), 404

    db.session.delete(t)
    db.session.commit()
    return jsonify({"message": "Tarea eliminada"}), 200

# REASIGNAR RESPONSABLE O CREAR RESPONSABLE
@tareas_bp.route("/tareas/<int:tarea_id>/assign", methods=["PUT"])
def asignar_tarea(tarea_id):
    t = Tarea.query.get(tarea_id)
    if not t:
        return jsonify({"error": "Tarea no encontrada"}), 404

    data = request.get_json() or {}
    if "empleadoId" not in data:
        return jsonify({"error": "Falta 'empleadoId'"}), 400

    t.empleado_id = data["empleadoId"]
    db.session.commit()
    return jsonify({"message": "Tarea asignada"}), 200

# ACTUALIZAR EL STATUS DE LA TAREA
@tareas_bp.route("/tareas/<int:tarea_id>/status", methods=["PUT"])
def actualizar_estado_tarea(tarea_id):
    t = Tarea.query.get(tarea_id)
    if not t:
        return jsonify({"error": "Tarea no encontrada"}), 404

    data = request.get_json() or {}
    if "estado" not in data:
        return jsonify({"error": "Falta 'estado'"}), 400

    t.estado = data["estado"]
    db.session.commit()
    return jsonify({"message": "Estado actualizado"}), 200


@tareas_bp.route("/proyectos/<int:proyecto_id>/resumen-tareas", methods=["GET"])
def resumen_tareas_proyecto(proyecto_id):
    # 1) Buscar proyecto
    proyecto = Proyecto.query.get(proyecto_id)
    if not proyecto:
        return jsonify({"error": "Proyecto no encontrado"}), 404

    # 2) Obtener tareas del proyecto
    tareas = Tarea.query.filter_by(proyecto_id=proyecto_id).all()

    # Si NO hay tareas -> sin tareas asignadas
    if not tareas:
        return jsonify({
            "proyecto_id": proyecto.proyecto_id,
            "estado": proyecto.estado,
            "cliente": proyecto.cliente,
            "tareasAsignadas": False,
            "tareasCumplidas": 0,
            "tareasEnProceso": 0,
            "tareasPendientes": 0
        }), 200

    # 3) Contar tareas por estado
    total = len(tareas)
    completadas = 0
    en_proceso = 0
    pendientes = 0

    for t in tareas:
        if t.estado == "Completada":
            completadas += 1
        elif t.estado == "En proceso":
            en_proceso += 1
        elif t.estado == "Pendiente":
            pendientes += 1
        else:
            # Si por alguna razón el estado es raro, lo contamos como pendiente
            pendientes += 1

    # 4) Calcular porcentajes (enteros que sumen 100)
    if total == 0:
        porc_completadas = porc_en_proceso = porc_pendientes = 0
    else:
        # calculamos dos con round y el último como "lo que falte" para sumar 100
        porc_completadas = round(completadas * 100 / total)
        porc_en_proceso = round(en_proceso * 100 / total)
        porc_pendientes = 100 - porc_completadas - porc_en_proceso

    return jsonify({
        "proyecto_id": proyecto.proyecto_id,
        "estado": proyecto.estado,
        "cliente": proyecto.cliente,
        "tareasAsignadas": True,
        "tareasCumplidas": porc_completadas,
        "tareasEnProceso": porc_en_proceso,
        "tareasPendientes": porc_pendientes
    }), 200


@tareas_bp.route("/proyectos/resumen-tareas", methods=["GET"])
def resumen_todas_tareas_proyectos():
    # 1) Obtener todos los proyectos
    proyectos = Proyecto.query.all()

    resultados = []

    for proyecto in proyectos:
        # 2) Obtener tareas de este proyecto
        tareas = Tarea.query.filter_by(proyecto_id=proyecto.proyecto_id).all()

        if not tareas:
            # Sin tareas asignadas
            resultados.append({
                "proyecto_id": proyecto.proyecto_id,
                "estado": proyecto.estado,
                "titulo": proyecto.titulo,
                "tareasAsignadas": False,
                "tareasCumplidas": 0,
                "tareasEnProceso": 0,
                "tareasPendientes": 0
            })
            continue

        # 3) Contar tareas por estado
        total = len(tareas)
        completadas = 0
        en_proceso = 0
        pendientes = 0

        for t in tareas:
            if t.estado == "Completada":
                completadas += 1
            elif t.estado == "En proceso":
                en_proceso += 1
            elif t.estado == "Pendiente":
                pendientes += 1
            else:
                # Cualquier estado raro lo consideramos pendiente
                pendientes += 1

        # 4) Calcular porcentajes que sumen 100
        if total == 0:
            porc_completadas = porc_en_proceso = porc_pendientes = 0
        else:
            porc_completadas = round(completadas * 100 / total)
            porc_en_proceso = round(en_proceso * 100 / total)
            porc_pendientes = 100 - porc_completadas - porc_en_proceso

        resultados.append({
            "proyecto_id": proyecto.proyecto_id,
            "estado": proyecto.estado,
            "titulo": proyecto.titulo,
            "tareasAsignadas": True,
            "tareasCumplidas": porc_completadas,
            "tareasEnProceso": porc_en_proceso,
            "tareasPendientes": porc_pendientes
        })

    return jsonify(resultados), 200

@tareas_bp.route("/tareas/empleados/<int:empleado_id>", methods=["GET"])
def listar_tareas_por_empleado(empleado_id):
    """
    Lista todas las tareas asignadas a un empleado específico,
    incluyendo el título y descripción del proyecto.
    """
    filas = (
        db.session.query(Tarea, Proyecto)
        .join(Proyecto, Tarea.proyecto_id == Proyecto.proyecto_id)
        .filter(Tarea.empleado_id == empleado_id)
        .all()
    )

    return jsonify([
        {
            "tareaId": t.tarea_id,
            "proyectoId": t.proyecto_id,
            "proyectoTitulo": p.titulo,
            "proyectoDescripcion": p.descripcion,
            "empleadoId": t.empleado_id,
            "estado": t.estado,
            "fechaInicio": dt_to_millis(t.fecha_inicio),
            "fechaFin": dt_to_millis(t.fecha_fin),
            "comentarios": t.comentarios
        }
        for t, p in filas
    ]), 200
