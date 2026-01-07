# routes/proyectos.py
from flask import Blueprint, request, jsonify
from models import Proyecto
from db import db
from .utils import require_admin  # mismo helper que usas en materiales

proyectos_bp = Blueprint("proyectos", __name__)

# ========= Listar proyectos =========
@proyectos_bp.route("/proyectos", methods=["GET"])
def listar_proyectos():
    proyectos = Proyecto.query.all()
    return jsonify([p.to_dict() for p in proyectos]), 200


# ========= Crear proyecto =========
@proyectos_bp.route("/proyectos", methods=["POST"])
def crear_proyecto():
    # Solo admin (o primario si luego lo agregas)
    # error = require_admin()
    # if error:
    #     return error

    data = request.get_json() or {}

    # Campos obligatorios
    if "titulo" not in data or "descripcion" not in data or "cliente" not in data:
        return jsonify({"error": "Faltan campos obligatorios: titulo, descripcion y/o cliente"}), 400

    titulo = data["titulo"]
    descripcion = data["descripcion"]
    cliente = data["cliente"]
    estado = data.get("estado", "En progreso")

    # Tomar el empleado que registra desde el header
    empleado_id_header = request.headers.get("X-Empleado-Id")
    try:
        registrado_por = int(empleado_id_header) if empleado_id_header else None
    except ValueError:
        registrado_por = None

    nuevo = Proyecto(
        titulo=titulo,
        descripcion=descripcion,
        cliente=cliente,
        estado=estado,
        registrado_por=registrado_por
    )

    db.session.add(nuevo)
    db.session.commit()

    return jsonify({
        "message": "Proyecto creado",
        "proyecto_id": nuevo.proyecto_id
    }), 201


# ========= Obtener proyecto por ID =========
@proyectos_bp.route("/proyectos/<int:proyecto_id>", methods=["GET"])
def obtener_proyecto(proyecto_id):
    proyecto = Proyecto.query.get(proyecto_id)
    if not proyecto:
        return jsonify({"error": "Proyecto no encontrado"}), 404

    return jsonify(proyecto.to_dict()), 200


# ========= Actualizar proyecto =========
@proyectos_bp.route("/proyectos/<int:proyecto_id>", methods=["PUT"])
def actualizar_proyecto(proyecto_id):
    # error = require_admin()
    # if error:
    #     return error

    proyecto = Proyecto.query.get(proyecto_id)
    if not proyecto:
        return jsonify({"error": "Proyecto no encontrado"}), 404

    data = request.get_json() or {}
    if "titulo" in data:
        proyecto.titulo = data["titulo"]

    if "descripcion" in data:
        proyecto.descripcion = data["descripcion"]
    if "cliente" in data:
        proyecto.cliente = data["cliente"]
    if "estado" in data:
        proyecto.estado = data["estado"]

    db.session.commit()
    return jsonify({"message": "Proyecto actualizado"}), 200


# ========= Eliminar proyecto =========
@proyectos_bp.route("/proyectos/<int:proyecto_id>", methods=["DELETE"])
def eliminar_proyecto(proyecto_id):
    # error = require_admin()
    # if error:
    #     return error

    proyecto = Proyecto.query.get(proyecto_id)
    if not proyecto:
        return jsonify({"error": "Proyecto no encontrado"}), 404

    #🔸 Opcional: bloquear si tiene tareas asociadas (Tareas.proyecto_id)
    from models import Tarea
    if Tarea.query.filter_by(proyecto_id=proyecto.proyecto_id).first():
        return jsonify({"error": "No se puede eliminar el proyecto porque tiene tareas asociadas"}), 400

    db.session.delete(proyecto)
    db.session.commit()
    return jsonify({"message": "Proyecto eliminado"}), 200
