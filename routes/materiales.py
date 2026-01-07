# routes/materiales.py
from flask import Blueprint, request, jsonify
from models import Material, MovimientoMaterial, ConsumoMaterial
from db import db
from .utils import require_admin
from decimal import Decimal

materiales_bp = Blueprint("materiales", __name__)

@materiales_bp.route("/materiales", methods=["GET"])
def listar_materiales():
    materiales = Material.query.all()
    result = []
    for m in materiales:
        result.append({
            "id": m.material_id,
            "tipo": m.tipo,
            "nombre": m.nombre,
            "unidad_medida": m.unidad_medida,
            "descripcion": m.descripcion,
            "stock_actual": m.stock_actual
        })
    return jsonify(result), 200

@materiales_bp.route("/materiales", methods=["POST"])
def crear_material():
    # Solo admin
    # error = require_admin()
    # if error:
    #     return error

    data = request.get_json()

    # Validar que vengan todos los campos obligatorios
    campos_requeridos = ["tipo", "nombre", "unidad_medida", "descripcion", "stock_actual"]
    for campo in campos_requeridos:
        if campo not in data:
            return jsonify({"error": f"Falta el campo '{campo}'"}), 400

    mat = Material(
        tipo=data["tipo"],
        nombre=data["nombre"],
        unidad_medida=data["unidad_medida"],
        descripcion=data.get("descripcion"),
        stock_actual=data.get("stock_actual", 0)
    )

    db.session.add(mat)
    db.session.commit()

    return jsonify({
        "message": "Material creado",
        "material_id": mat.material_id
    }), 201


@materiales_bp.route("/materiales/<int:material_id>", methods=["PUT"])
def actualizar_material(material_id):
    # Solo admin
    # error = require_admin()
    # if error:
    #     return error

    material = Material.query.get(material_id)
    if not material:
        return jsonify({"error": "Material no encontrado"}), 404
    data = request.get_json()

    # Actualizar solo lo que venga en el body
    if "nombre" in data:
        material.nombre = data["nombre"]

    if "tipo" in data:
        material.tipo = data["tipo"]  # debe ser: tubo, soldadura, lamina, herramienta, otro

    if "unidad_medida" in data:
        material.unidad_medida = data["unidad_medida"]  # pieza, kg, ton, m, lt

    if "descripcion" in data:
        material.descripcion = data["descripcion"]

    if "stock_actual" in data:
        material.stock_actual = data["stock_actual"]

    db.session.commit()
    return jsonify({"message": "Material actualizado"}), 200

@materiales_bp.route("/materiales/<int:material_id>", methods=["DELETE"])
def eliminar_material(material_id):
    # # Verificar permisos
    # error = require_admin()
    # if error:
    #     return error

    material = Material.query.get(material_id)
    if not material:
        return jsonify({"error": "Material no encontrado"}), 404

    movimientos = MovimientoMaterial.query.filter_by(material_id=material.material_id).first()
    if movimientos:
        return jsonify({
            "error": "No se puede eliminar el material porque tiene historial de movimientos"
        }), 400

    # Si no tiene movimientos → eliminar
    db.session.delete(material)
    db.session.commit()

    return jsonify({"message": "Material eliminado"}), 200


@materiales_bp.route("/materiales/<int:material_id>", methods=["GET"])
def obtener_material(material_id):
    material = Material.query.get(material_id)
    if not material:
        return jsonify({"error": "Material no encontrado"}), 404

    return jsonify(material.to_dict()), 200


# --------- Movimientos (Entradas / Salidas) ---------

@materiales_bp.route("/materiales/<int:material_id>/entradas", methods=["POST"])
def registrar_entrada(material_id):
    # Solo admin
    # error = require_admin()
    # if error:
    #     return error

    # Buscar material
    material = Material.query.get(material_id)
    if not material:
        return jsonify({"error": "Material no encontrado"}), 404

    data = request.get_json() or {}

    # Validar que venga 'cantidad'
    if "cantidad" not in data:
        return jsonify({"error": "Falta el campo 'cantidad'"}), 400

    # Asegurar que la cantidad sea numérica
    try:
        cantidad = float(data["cantidad"])
    except (TypeError, ValueError):
        return jsonify({"error": "La cantidad debe ser numérica"}), 400

    # Opcionales
    costo_unitario = data.get("costo_unitario")
    observaciones = data.get("observaciones")

    # Leer empleado del header (escolar)
    empleado_id_header = request.headers.get("X-Empleado-Id")
    try:
        empleado_id = int(empleado_id_header) if empleado_id_header else None
    except ValueError:
        empleado_id = None

    # Crear movimiento (OJO con los nombres de campos)
    mov = MovimientoMaterial(
        material_id=material.material_id,   # <-- pk real del material
        tipo_movimiento="entrada",         # <-- coincide con tu CHECK en SQL
        cantidad=cantidad,
        costo_unitario=costo_unitario,
        empleado_id=empleado_id,
        observaciones=observaciones
    )

    # Actualizar stock (stock_actual es NUMERIC)
    material.stock_actual = (material.stock_actual or Decimal("0")) + Decimal(str(cantidad))

    db.session.add(mov)
    db.session.commit()

    return jsonify({"message": "Entrada registrada"}), 201



@materiales_bp.route("/materiales/<int:material_id>/salidas", methods=["POST"])
def registrar_salida(material_id):
    # Solo admin (por ahora)

    material = Material.query.get(material_id)
    if not material:
        return jsonify({"error": "Material no encontrado"}), 404

    data = request.get_json() or {}

    # Validar cantidad
    if "cantidad" not in data:
        return jsonify({"error": "Falta 'cantidad'"}), 400

    # Validar tarea_id (para ConsumoMaterial)
    if "tarea_id" not in data:
        return jsonify({"error": "Falta 'tarea_id' para registrar consumo"}), 400

    # Parseo numérico de cantidad
    try:
        cantidad = float(data["cantidad"])
    except (TypeError, ValueError):
        return jsonify({"error": "La cantidad debe ser numérica"}), 400

    if cantidad <= 0:
        return jsonify({"error": "La cantidad debe ser mayor a 0"}), 400

    tarea_id = data["tarea_id"]
    observaciones = data.get("observaciones")

    # Leer empleado del header (escolar)
    empleado_id_header = request.headers.get("X-Empleado-Id")
    try:
        empleado_id = int(empleado_id_header) if empleado_id_header else None
    except ValueError:
        empleado_id = None

    # Validar stock disponible
    stock_actual_dec = material.stock_actual or Decimal("0")
    if Decimal(str(cantidad)) > stock_actual_dec:
        return jsonify({"message": "No hay stock suficiente"}), 400

    # 1) Registrar movimiento de salida en MovimientosMaterial
    mov = MovimientoMaterial(
        material_id=material.material_id,
        tipo_movimiento="salida",
        cantidad=cantidad,
        costo_unitario=None,
        empleado_id=empleado_id,
        observaciones=observaciones
    )

    # Actualizar stock
    material.stock_actual = stock_actual_dec - Decimal(str(cantidad))

    db.session.add(mov)

    # 2) Registrar consumo en ConsumoMaterial (FIFO 'salidas' nivel proyecto/tarea)
    consumo = ConsumoMaterial(
        tarea_id=tarea_id,
        material_id=material.material_id,
        cantidad_usada=cantidad,
        observaciones=observaciones
        # fecha_uso se llena solo con default=NOW
    )

    db.session.add(consumo)
    db.session.commit()

    return jsonify({
        "message": "Salida registrada y consumo guardado",
        "movimiento_id": mov.movimiento_id,
        "consumo_id": consumo.consumo_id
    }), 201



@materiales_bp.route("/materiales/<int:material_id>/movimientos", methods=["GET"])
def listar_movimientos(material_id):
    material = Material.query.get(material_id)
    if not material:
        return jsonify({"error": "Material no encontrado"}), 404

    movimientos = (
        MovimientoMaterial
        .query
        .filter_by(material_id=material.material_id)
        .order_by(MovimientoMaterial.fecha.asc())
        .all()
    )

    result = []
    for mov in movimientos:
        result.append({
            "movimiento_id": mov.movimiento_id,
            "material_id": mov.material_id,
            "tipo_movimiento": mov.tipo_movimiento,
            "cantidad": float(mov.cantidad),
            "fecha": mov.fecha.isoformat(),
            "costo_unitario": float(mov.costo_unitario) if mov.costo_unitario is not None else None,
            "empleado_id": mov.empleado_id,
            "observaciones": mov.observaciones
        })

    return jsonify(result), 200


@materiales_bp.route("/materiales/movimientos", methods=["GET"])
def listar_all_movimientos():
    """
    Lista TODOS los movimientos de MovimientosMaterial
    pero ahora devolviendo material_name en lugar de material_id.
    """
    movimientos = (
        MovimientoMaterial
        .query
        .order_by(MovimientoMaterial.fecha.asc())
        .all()
    )

    result = []
    for mov in movimientos:
        result.append({
            "movimiento_id": mov.movimiento_id,
            "tipo_movimiento": mov.tipo_movimiento,
            "cantidad": float(mov.cantidad),
            "fecha": mov.fecha.isoformat(),
            "material_name": mov.material.nombre if mov.material else None,
            "observaciones": mov.observaciones
        })

    return jsonify(result), 200


@materiales_bp.route("/materiales/consumo", methods=["POST"])
def crear_consumo_material():
    """
    Crear un registro en ConsumoMaterial de forma directa (para pruebas).
    NO toca stock_actual ni registra movimiento en MovimientosMaterial.
    """
    data = request.get_json() or {}

    faltan = [campo for campo in ["tarea_id", "material_id", "cantidad_usada"] if campo not in data]
    if faltan:
        return jsonify({"error": f"Faltan campos: {', '.join(faltan)}"}), 400

    try:
        cantidad_usada = float(data["cantidad_usada"])
    except (TypeError, ValueError):
        return jsonify({"error": "cantidad_usada debe ser numérica"}), 400

    if cantidad_usada <= 0:
        return jsonify({"error": "cantidad_usada debe ser mayor a 0"}), 400

    tarea_id = data["tarea_id"]
    material_id = data["material_id"]
    observaciones = data.get("observaciones")

    consumo = ConsumoMaterial(
        tarea_id=tarea_id,
        material_id=material_id,
        cantidad_usada=cantidad_usada,
        observaciones=observaciones
    )

    db.session.add(consumo)
    db.session.commit()

    return jsonify({
        "message": "Consumo registrado (modo prueba)",
        "consumo_id": consumo.consumo_id
    }), 201


@materiales_bp.route("/materiales/consumo", methods=["GET"])
def listar_consumos_material():
    """
    Listar todos los consumos de material registrados,
    incluyendo nombre y unidad de medida del material.
    """
    consumos = ConsumoMaterial.query.order_by(ConsumoMaterial.fecha_uso.asc()).all()

    result = []
    for c in consumos:
        result.append({
            "consumo_id": c.consumo_id,
            "tarea_id": c.tarea_id,
            "material_id": c.material_id,

            # 🔥 NUEVOS CAMPOS
            "material_nombre": c.material.nombre if c.material else None,
            "unidad_medida": c.material.unidad_medida if c.material else None,

            "cantidad_usada": float(c.cantidad_usada),
            "fecha_uso": c.fecha_uso.isoformat(),
            "observaciones": c.observaciones
        })

    return jsonify(result), 200
