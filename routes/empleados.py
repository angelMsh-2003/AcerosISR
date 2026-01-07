# routes_empleados.py
from flask import Blueprint, request, jsonify
from db import db
from models import Empleado

empleados_bp = Blueprint("empleados_bp", __name__)

# POST /api/empleados  -> registrar nuevo usuario
@empleados_bp.route("/empleados", methods=["POST"])
def registrar_empleado():
    data = request.get_json()

    num = data["NumEmpleado"]
    nombre = data["NombreEmpleado"]
    cargo = data.get("Cargo", "trabajador")
    correo = data.get("Correo")
    estado = data.get("Estado", 1)
    password_plain = data.get("PasswordPlaintext")

    # ¿Ya existe?
    existente = Empleado.query.filter_by(num_empleado=num).first()
    if existente:
        # El frontend busca "Duplicate entry" en el mensaje de error
        return "Duplicate entry", 400

    nuevo = Empleado(
        num_empleado=num,
        nombre=nombre,
        cargo=cargo,
        correo=correo,
        password_hash=password_plain,  # guardamos tal cual (modo escolar)
        salt=None,
        activo=(estado == 1),
    )

    db.session.add(nuevo)
    db.session.commit()

    # El repo no usa el body, solo que sea 2xx
    return "Registro exitoso", 201


# POST /api/empleados/login  -> iniciar sesión
@empleados_bp.route("/empleados/login", methods=["POST"])
def login_empleado():
    data = request.get_json()
    num = data["NumEmpleado"]
    password_plain = data["PasswordPlaintext"]

    emp = Empleado.query.filter_by(num_empleado=num).first()

    # 1. Verificar que exista el usuario y la contraseña coincida
    if not emp or emp.password_hash != password_plain:
        return "Credenciales inválidas", 401

    # 2. Verificar que el usuario esté ACTIVO
    if not emp.activo:   # Estado = 0
        return "Credenciales inválidas", 401

    # 3. Si todo es correcto, devolver la información del empleado
    return jsonify({
        "id": emp.empleado_id,
        "NumEmpleado": emp.num_empleado,
        "NombreEmpleado": emp.nombre,
        "Cargo": emp.cargo,
        "Correo": emp.correo,
        "Password_hash": emp.password_hash,
        "Salt": emp.salt,
        "Estado": 1 if emp.activo else 0
    }), 200



# GET /api/empleados/<numEmpleado>  -> detalles para guardar usuario actual
@empleados_bp.route("/empleados/<int:empleado_id>", methods=["GET"])
def obtener_empleado(empleado_id):
    emp = Empleado.query.get(empleado_id)
    if not emp:
        return "No encontrado", 404

    return jsonify({
        "id": emp.empleado_id,
        "NumEmpleado": emp.num_empleado,
        "NombreEmpleado": emp.nombre,
        "Cargo": emp.cargo,
        "Correo": emp.correo,
        "Password_hash": emp.password_hash,
        "Salt": emp.salt,
        "Estado": 1 if emp.activo else 0
    }), 200


# GET /api/empleados  -> obtener todos los empleados
@empleados_bp.route("/empleados", methods=["GET"])
def obtener_empleados():
    empleados = Empleado.query.all()

    resultado = []
    for emp in empleados:
        resultado.append({
            "id" : emp.empleado_id,
            "NumEmpleado": emp.num_empleado,
            "NombreEmpleado": emp.nombre,
            "Cargo": emp.cargo,
            "Correo": emp.correo,
            "Password_hash": emp.password_hash,
            "Salt": emp.salt,
            "Estado": 1 if emp.activo else 0
        })

    return jsonify(resultado), 200


# POST /api/empleados/recover  -> verificar usuario por num_empleado + correo
@empleados_bp.route("/empleados/recover", methods=["POST"])
def recuperar_empleado_por_correo():
    data = request.get_json()

    num = data.get("NumEmpleado")
    correo = data.get("Correo")

    if num is None or correo is None:
        return jsonify({"EmpleadoId": 0}), 400

    emp = Empleado.query.filter_by(num_empleado=num, correo=correo).first()

    if not emp:
        # No hay coincidencia entre número de empleado y correo
        return jsonify({"EmpleadoId": 0}), 200

    # Cualquier número diferente de 0 indica que el usuario existe
    return jsonify({"EmpleadoId": emp.empleado_id}), 200

@empleados_bp.route("/empleados/<int:empleado_id>", methods=["PUT"])
def actualizar_empleado(empleado_id):
    data = request.get_json()

    emp = Empleado.query.get(empleado_id)
    if not emp:
        return "No encontrado", 404

    # ───────────────────────────────
    #  Actualización de todos los campos
    # ───────────────────────────────

    if "NumEmpleado" in data:
        emp.num_empleado = data["NumEmpleado"]

    if "NombreEmpleado" in data:
        emp.nombre = data["NombreEmpleado"]

    if "Cargo" in data:
        emp.cargo = data["Cargo"]

    if "Correo" in data:
        emp.correo = data["Correo"]

    # ───────────────────────────────
    #  Manejo robusto del campo Estado / Activo
    # ───────────────────────────────
    if "Estado" in data:
        estado = data["Estado"]
        if isinstance(estado, bool):
            emp.activo = estado
        elif isinstance(estado, (int, float)):
            emp.activo = (estado == 1)
        elif isinstance(estado, str):
            emp.activo = (estado.strip().lower() in ["1", "true", "activo"])
        else:
            return jsonify({"error": "Valor inválido para Estado"}), 400

    # ───────────────────────────────
    #  Contraseña (modo escolar)
    # ───────────────────────────────
    if "PasswordPlaintext" in data:
        emp.password_hash = data["PasswordPlaintext"]

    db.session.commit()

    # Respuesta del usuario actualizado
    return jsonify({
        "id": emp.empleado_id,
        "NumEmpleado": emp.num_empleado,
        "NombreEmpleado": emp.nombre,
        "Cargo": emp.cargo,
        "Correo": emp.correo,
        "Password_hash": emp.password_hash,
        "Salt": emp.salt,
        "Estado": 1 if emp.activo else 0
    }), 200


# GET /api/empleados/num/<numEmpleado>  -> obtener empleado por num_empleado
@empleados_bp.route("/empleados/num/<int:num_empleado>", methods=["GET"])
def obtener_empleado_por_num(num_empleado):
    emp = Empleado.query.filter_by(num_empleado=num_empleado).first()
    if not emp:
        return "No encontrado", 404

    return jsonify({
        "id": emp.empleado_id,
        "NumEmpleado": emp.num_empleado,
        "NombreEmpleado": emp.nombre,
        "Cargo": emp.cargo,
        "Correo": emp.correo,
        "Password_hash": emp.password_hash,
        "Salt": emp.salt,
        "Estado": 1 if emp.activo else 0
    }), 200


# PUT /api/empleados/num/<numEmpleado>  -> actualizar empleado por num_empleado
@empleados_bp.route("/empleados/num/<int:num_empleado>", methods=["PUT"])
def actualizar_empleado_por_num(num_empleado):
    data = request.get_json() or {}

    emp = Empleado.query.filter_by(num_empleado=num_empleado).first()
    if not emp:
        return "No encontrado", 404

    # ───────────────────────────────
    #  Actualización de campos (opcionales)
    # ───────────────────────────────

    if "NumEmpleado" in data:
        emp.num_empleado = data["NumEmpleado"]

    if "NombreEmpleado" in data:
        emp.nombre = data["NombreEmpleado"]

    if "Cargo" in data:
        emp.cargo = data["Cargo"]

    if "Correo" in data:
        emp.correo = data["Correo"]

    # ───────────────────────────────
    #  Manejo del campo Estado / Activo
    # ───────────────────────────────
    if "Estado" in data:
        estado = data["Estado"]
        if isinstance(estado, bool):
            emp.activo = estado
        elif isinstance(estado, (int, float)):
            emp.activo = (estado == 1)
        elif isinstance(estado, str):
            emp.activo = (estado.strip().lower() in ["1", "true", "activo"])
        else:
            return jsonify({"error": "Valor inválido para Estado"}), 400

    # ───────────────────────────────
    #  Contraseña (modo escolar)
    # ───────────────────────────────
    if "PasswordPlaintext" in data:
        emp.password_hash = data["PasswordPlaintext"]

    db.session.commit()

    return jsonify({
        "id": emp.empleado_id,
        "NumEmpleado": emp.num_empleado,
        "NombreEmpleado": emp.nombre,
        "Cargo": emp.cargo,
        "Correo": emp.correo,
        "Password_hash": emp.password_hash,
        "Salt": emp.salt,
        "Estado": 1 if emp.activo else 0
    }), 200
