# tests/test_auth.py
def test_crear_empleado_y_login(client):
    # Crear empleado
    resp = client.post("/api/empleados", json={
        "num_empleado": 1,
        "nombre": "Juan",
        "cargo": "admin",
        "password": "1234"
    })
    assert resp.status_code == 201

    # Login correcto
    resp = client.post("/api/empleados/login", json={
        "num_empleado": 1,
        "password": "1234"
    })
    assert resp.status_code == 200
    data = resp.get_json()
    assert data["nombre"] == "Juan"

    # Login incorrecto
    resp = client.post("/api/empleados/login", json={
        "num_empleado": 1,
        "password": "malo"
    })
    assert resp.status_code == 401
