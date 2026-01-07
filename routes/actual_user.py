from flask import Blueprint, request, jsonify
from db import db
from models import Empleado, AgentUser
from datetime import datetime

actual_user_bp = Blueprint("actual_user_bp", __name__)

# ============================================================
# GET /api/actual-user
# Devuelve el ÚLTIMO usuario que inició sesión (modo simple)
# Totalmente compatible con tu UserRepository.kt
# ============================================================
@actual_user_bp.route("/actual-user", methods=["GET"])
def get_actual_user():
    # Buscamos la última sesión válida
    agent = AgentUser.query.order_by(AgentUser.fecha_login.desc()).first()

    if not agent:
        return jsonify({"error": "No actual user"}), 404
    
    print("GET AGENT /actual-user:", agent)
    return jsonify({
        "ID": agent.id,
        "UserId": agent.num_empleado,
        "NombreEmpleado": agent.nombre,
        "Cargo": agent.cargo
    }), 200


# ============================================================
# POST /api/actual-user
# Registra o actualiza la sesión de un empleado.
# Usado cuando el usuario inicia sesión en la app.
# Body esperado (desde UserRepository.insetUserActual()):
#
# {
#   "ID": 1,
#   "UserId": 7654323,
#   "NombreEmpleado": "Angel Manuel",
#   "Cargo": "trabajador",
#   "deviceId": "android-123123"   (opcional)
# }
# ============================================================
@actual_user_bp.route("/actual-user", methods=["POST"])
def set_actual_user():
    data = request.get_json() or {}

    # Aceptamos varias claves posibles para el número de empleado
    print (data)
    num_empleado = (
        data.get("UserId")
        or data.get("NumEmpleado")
        or data.get("numEmpleado")
    )

    if not num_empleado:
        return jsonify({"error": "Falta NumEmpleado / UserId"}), 400

    # Buscamos al empleado en BD
    empleado = Empleado.query.filter_by(num_empleado=num_empleado).first()
    if not empleado:
        return jsonify({"error": "Empleado no encontrado"}), 404

    # Si no vienen en el body, usamos los datos reales del empleado
    nombre = data.get("NombreEmpleado") or empleado.nombre
    cargo = data.get("Cargo") or empleado.cargo

    device_id = data.get("deviceId")
    device_model = data.get("deviceModel")
    ip = request.remote_addr

    # Estrategia simple:
    # Si ya existe sesión para este num_empleado, actualizamos
    existing = AgentUser.query.filter_by(num_empleado=num_empleado).first()

    if existing:
        existing.nombre = nombre
        existing.cargo = cargo
        existing.ip_address = ip
        existing.device_id = device_id
        existing.device_model = device_model
        existing.fecha_login = datetime.utcnow()
        db.session.commit()
        agent = existing
    else:
        agent = AgentUser(
            num_empleado=num_empleado,
            nombre=nombre,
            cargo=cargo,
            ip_address=ip,
            device_id=device_id,
            device_model=device_model
        )
        db.session.add(agent)
        db.session.commit()
    print("BODY /actual-user:", data)


    return jsonify({
        "status": "OK",
        "ID": agent.id,
        "UserId": agent.num_empleado,
        "NombreEmpleado": agent.nombre,
        "Cargo": agent.cargo
    }), 200
    


# ============================================================
# PUT /api/actual-user
# Edita SOLO los datos permitidos del empleado actual:
#   - Correo
#   - PasswordPlaintext
#
# Tu UserRepository llama esto cuando editas un usuario desde tu Drawer.
# ============================================================
@actual_user_bp.route("/actual-user", methods=["PUT"])
def update_actual_user():
    # Tomamos la última sesión disponible
    agent = AgentUser.query.order_by(AgentUser.fecha_login.desc()).first()
    if not agent:
        return jsonify({"error": "No actual user"}), 404

    empleado = Empleado.query.filter_by(num_empleado=agent.num_empleado).first()
    if not empleado:
        return jsonify({"error": "Empleado no encontrado"}), 404

    data = request.get_json() or {}

    # Editar correo
    if "Correo" in data:
        empleado.correo = data["Correo"]

    # Editar contraseña (modo escolar)
    if "PasswordPlaintext" in data:
        empleado.password_hash = data["PasswordPlaintext"]

    db.session.commit()

    return jsonify({"status": "Usuario modificado"}), 200


# ============================================================
# DELETE /api/actual-user
# "Cerrar sesión": borra todas las sesiones activas.
# Tu app solo maneja 1 usuario activo → válido.
# ============================================================
@actual_user_bp.route("/actual-user", methods=["DELETE"])
def delete_actual_user():
    AgentUser.query.delete()
    db.session.commit()
    return jsonify({"status": "OK"}), 200


# ============================================================
# POST /api/actual-user/empleado-id
# ➜ Dado un NumEmpleado, devolver el empleado_id real en BD.
# ============================================================
@actual_user_bp.route("/actual-user/empleado-id", methods=["POST"])
def obtener_empleado_id_desde_num():
    data = request.get_json() or {}

    # Aceptamos varias claves
    num_empleado = (
        data.get("NumEmpleado")
        or data.get("UserId")
        or data.get("numEmpleado")
    )

    if not num_empleado:
        return jsonify({"empleado_id": 0, "error": "Falta NumEmpleado/UserId"}), 400

    empleado = Empleado.query.filter_by(num_empleado=num_empleado).first()

    if not empleado:
        return jsonify({"empleado_id": 0}), 200

    return jsonify({"empleado_id": empleado.empleado_id}), 200
