# routes/utils.py
from flask import request, jsonify

def require_admin():
    rol = request.headers.get("X-Empleado-Rol")
    if rol != "admin":
        return jsonify({"message": "Solo admin puede realizar esta acción"}), 403
    return None
