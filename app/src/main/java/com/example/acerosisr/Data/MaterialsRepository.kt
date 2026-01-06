package com.example.acerosisr.Data

import com.example.acerosisr.Model.Material
import com.example.acerosisr.Model.MaterialMovement
import com.example.acerosisr.Model.MaterialMovementReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

interface MaterialsRepository {
    // CRUD
    suspend fun getAllMaterials(): Result<List<Material>>
    suspend fun getMaterialById(id: Int?): Result<Material>
    suspend fun createMaterial(material: Material): Result<Material>
    suspend fun updateMaterial(material: Material): Result<Material>
    suspend fun deleteMaterial(id: Int?): Result<Unit>

    // Movimientos
    suspend fun registerMovement(movement: MaterialMovement): Result<Unit>
    suspend fun registerMaterialEntry(materialId: Int?, cantidad: Double, costoUnitario: Double, observaciones: String?): Result<Unit>
    suspend fun getMovementsReport(): Result<List<MaterialMovementReport>>
    suspend fun registerMaterialExitForTask(materialId: Int, cantidad: Double, tareaId: Long, observaciones: String): Result<Unit>
}

class MaterialsRepositoryImpl(private val apiService: ApiService) : MaterialsRepository {
    companion object {
        private const val MATERIALS_ENDPOINT = "api/materiales"
        private const val MOVEMENTS_ENDPOINT = "api/movimientosmaterial"
        private const val REPORT_ENDPOINT = "api/materiales/movimientos"
    }

    // ============ CORRECCIÓN DE ACENTOS AQUÍ ============
    override suspend fun getAllMaterials(): Result<List<Material>> = withContext(Dispatchers.IO) {
        apiService.get(MATERIALS_ENDPOINT)
            .mapCatching { responseJson ->
                val list = mutableListOf<Material>()
                val trimmed = responseJson.trim()

                // JSONArray maneja automáticamente \u00e1 -> á
                if (trimmed.startsWith("[")) {
                    val jsonArray = JSONArray(trimmed)
                    for (i in 0 until jsonArray.length()) {
                        list.add(jsonToMaterial(jsonArray.getJSONObject(i)))
                    }
                } else if (trimmed.startsWith("{")) {
                    list.add(jsonToMaterial(JSONObject(trimmed)))
                }
                list
            }
    }

    override suspend fun getMaterialById(id: Int?): Result<Material> = withContext(Dispatchers.IO) {
        apiService.get("$MATERIALS_ENDPOINT/$id")
            .mapCatching { jsonToMaterial(JSONObject(it)) }
    }

    override suspend fun createMaterial(material: Material): Result<Material> = withContext(Dispatchers.IO) {
        // Usamos JSONObject para crear el cuerpo (escapa caracteres automáticamente)
        val body = JSONObject().apply {
            put("tipo", material.tipo)
            put("nombre", material.nombre)
            put("unidad_medida", material.unidadMedida)
            put("stock_actual", material.stockActual)
            put("descripcion", material.descripcion)
        }
        apiService.post(MATERIALS_ENDPOINT, body.toString())
            .mapCatching { jsonToMaterial(JSONObject(it)) }
    }

    override suspend fun updateMaterial(material: Material): Result<Material> = withContext(Dispatchers.IO) {
        val id = material.id ?: return@withContext Result.failure(Exception("ID nulo"))
        val body = JSONObject().apply {
            put("tipo", material.tipo)
            put("nombre", material.nombre)
            put("unidad_medida", material.unidadMedida)
            put("stock_actual", material.stockActual)
            put("descripcion", material.descripcion)
        }
        apiService.put("$MATERIALS_ENDPOINT/$id", body.toString())
            .mapCatching { jsonToMaterial(JSONObject(it)) }
    }

    override suspend fun deleteMaterial(id: Int?): Result<Unit> = withContext(Dispatchers.IO) {
        apiService.delete("$MATERIALS_ENDPOINT/$id").map { }
    }

    override suspend fun registerMovement(movement: MaterialMovement): Result<Unit> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("material_id", movement.materialId)
            put("tipo_movimiento", movement.tipoMovimiento)
            put("cantidad", movement.cantidad)
            movement.costoUnitario?.let { put("costo_unitario", it) }
            movement.observaciones?.let { put("observaciones", it) }
        }
        apiService.post(MOVEMENTS_ENDPOINT, body.toString()).map { }
    }

    override suspend fun registerMaterialEntry(
        materialId: Int?, cantidad: Double, costoUnitario: Double, observaciones: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("cantidad", cantidad)
            put("costo_unitario", costoUnitario)
            observaciones?.let { put("observaciones", it) }
        }
        apiService.post("$MATERIALS_ENDPOINT/$materialId/entradas", body.toString()).map { }
    }

    override suspend fun getMovementsReport(): Result<List<MaterialMovementReport>> = withContext(Dispatchers.IO) {
        apiService.get(REPORT_ENDPOINT).mapCatching { response ->
            val jsonArray = JSONArray(response)
            val list = mutableListOf<MaterialMovementReport>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    MaterialMovementReport(
                        movimientoId = obj.optInt("movimiento_id"),
                        materialId = if (obj.isNull("material_id")) null else obj.optInt("material_id"),
                        materialName = obj.optString("material_name", "Desconocido"), // Esto también arregla nombres en reportes
                        tipoMovimiento = obj.optString("tipo_movimiento"),
                        cantidad = obj.optDouble("cantidad"),
                        fecha = obj.optString("fecha"),
                        observaciones = obj.optString("observaciones")
                    )
                )
            }
            list
        }
    }

    override suspend fun registerMaterialExitForTask(
        materialId: Int, cantidad: Double, tareaId: Long, observaciones: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("cantidad", cantidad)
            put("tarea_id", tareaId)
            put("observaciones", observaciones)
        }
        apiService.post("$MATERIALS_ENDPOINT/$materialId/salidas", body.toString()).map { }
    }

    // MAPPER CENTRALIZADO QUE DECODIFICA UNICODE
    private fun jsonToMaterial(json: JSONObject): Material {
        val id = if (json.has("id")) json.getInt("id") else json.optInt("material_id", 0)
        return Material(
            id = id,
            tipo = json.optString("tipo", ""),
            nombre = json.optString("nombre", ""), // optString ya devuelve "estándar" correctamente
            unidadMedida = json.optString("unidad_medida", ""),
            descripcion = json.optString("descripcion", ""),
            stockActual = json.optDouble("stock_actual", 0.0)
        )
    }
}