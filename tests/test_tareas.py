# tests/test_tareas.py
def test_crear_y_listar_tareas(client):
    # Crear tarea
    resp = client.post("/api/tareas", json={
        "titulo": "Cortar acero",
        "descripcion": "Tarea de prueba"
    })
    assert resp.status_code == 201

    # Listar tareas
    resp = client.get("/api/tareas")
    assert resp.status_code == 200
    data = resp.get_json()
    assert len(data) == 1
    assert data[0]["titulo"] == "Cortar acero"
