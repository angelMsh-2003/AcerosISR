# tests/test_materiales.py
def test_crear_material_y_entradas_salidas(client):
    headers_admin = {
        "X-Empleado-Rol": "admin",
        "X-Empleado-Id": "1"
    }

    # Crear material
    resp = client.post("/api/materiales", json={
        "nombre": "Varilla 3/8",
        "stock_actual": 0
    }, headers=headers_admin)
    assert resp.status_code == 201
    material_id = resp.get_json()["id"]

    # Registrar entrada
    resp = client.post(f"/api/materiales/{material_id}/entradas", json={
        "cantidad": 10
    }, headers=headers_admin)
    assert resp.status_code == 201

    # Registrar salida válida
    resp = client.post(f"/api/materiales/{material_id}/salidas", json={
        "cantidad": 4
    }, headers=headers_admin)
    assert resp.status_code == 201

    # Salida que excede stock
    resp = client.post(f"/api/materiales/{material_id}/salidas", json={
        "cantidad": 20
    }, headers=headers_admin)
    assert resp.status_code == 400

    # Verificar stock actual vía listado
    resp = client.get("/api/materiales")
    assert resp.status_code == 200
    data = resp.get_json()
    assert data[0]["stock_actual"] == 6 