package com.example.acerosisr.Data

import com.example.acerosisr.Model.Tareas // Importing the actual Tareas data class
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

// Removed the placeholder interface Tareas, now directly using Model.Tareas

interface TareasRepository {
    suspend fun getAllTasks(): Result<List<Tareas>>
    suspend fun getTaskById(taskId: Long): Result<Tareas>
    suspend fun createTask(tarea: Tareas): Result<String>
    suspend fun updateTask(tareaId: Long, tarea: Tareas): Result<String>
    suspend fun deleteTask(taskId: Long): Result<String>
    suspend fun assignTaskToEmployee(taskId: Long, empleadoId: Long): Result<String>
    suspend fun updateTaskStatus(taskId: Long, newStatus: String): Result<String>
}

class TareasRepositoryImpl(private val apiService: ApiService) : TareasRepository {
    private val TASKS_ENDPOINT = "api/tareas"

    override suspend fun getAllTasks(): Result<List<Tareas>> = withContext(Dispatchers.IO) {
        apiService.get(TASKS_ENDPOINT)
            .mapCatching { responseJson -> // Using mapCatching for safer parsing
                val jsonListContent = responseJson.removePrefix("[").removeSuffix("]")
                val jsonList = JsonConverter.splitJsonEntries(jsonListContent).map { "{\n" + it + "\n}" } // Reconstruct full JSON objects for proper parsing

                jsonList.mapNotNull { itemJson ->
                    try {
                        val data = JsonConverter.fromJson(itemJson)
                        Tareas(
                            tareaId = (data["tareaId"] as? Long) ?: 0L,
                            proyectoId = (data["proyectoId"] as? Long) ?: 0L,
                            empleadoId = (data["empleadoId"] as? Long),
                            estado = (data["estado"] as? String) ?: "",
                            fechaInicio = (data["fechaInicio"] as? Long)?.let { Date(it) },
                            fechaFin = (data["fechaFin"] as? Long)?.let { Date(it) },
                            comentarios = data["comentarios"] as? String
                        )
                    } catch (e: Exception) {
                        println("Error parsing task item: ${e.message}")
                        null
                    }
                }
            }
            // Removed .fold(onSuccess = { it }, onFailure = { throw it })
    }

    override suspend fun getTaskById(taskId: Long): Result<Tareas> = withContext(Dispatchers.IO) {
        apiService.get("$TASKS_ENDPOINT/$taskId")
            .mapCatching { responseJson -> // Using mapCatching for safer parsing
                val data = JsonConverter.fromJson(responseJson)
                Tareas(
                    tareaId = (data["tareaId"] as? Long) ?: 0L,
                    proyectoId = (data["proyectoId"] as? Long) ?: 0L,
                    empleadoId = (data["empleadoId"] as? Long),
                    estado = (data["estado"] as? String) ?: "",
                    fechaInicio = (data["fechaInicio"] as? Long)?.let { Date(it) },
                    fechaFin = (data["fechaFin"] as? Long)?.let { Date(it) },
                    comentarios = data["comentarios"] as? String
                )
            }
            // Removed .fold(onSuccess = { it }, onFailure = { throw it })
    }

    override suspend fun createTask(tarea: Tareas): Result<String> = withContext(Dispatchers.IO) {
        val tareaMap = mapOf(
            "proyectoId" to tarea.proyectoId,
            "empleadoId" to tarea.empleadoId,
            "estado" to tarea.estado,
            "fechaInicio" to tarea.fechaInicio?.time, // Convert Date to millis
            "fechaFin" to tarea.fechaFin?.time,
            "comentarios" to tarea.comentarios
        )
        apiService.post(TASKS_ENDPOINT, JsonConverter.toJson(tareaMap))
            .map { "Tarea creada exitosamente" }
    }

    override suspend fun updateTask(tareaId: Long, tarea: Tareas): Result<String> = withContext(Dispatchers.IO) {
        val tareaMap = mapOf(
            "proyectoId" to tarea.proyectoId,
            "empleadoId" to tarea.empleadoId,
            "estado" to tarea.estado,
            "fechaInicio" to tarea.fechaInicio?.time,
            "fechaFin" to tarea.fechaFin?.time,
            "comentarios" to tarea.comentarios
        )
        apiService.put("$TASKS_ENDPOINT/$tareaId", JsonConverter.toJson(tareaMap))
            .map { "Tarea actualizada exitosamente" }
    }

    override suspend fun deleteTask(taskId: Long): Result<String> = withContext(Dispatchers.IO) {
        apiService.delete("$TASKS_ENDPOINT/$taskId")
            .map { "Tarea eliminada exitosamente" }
    }

    override suspend fun assignTaskToEmployee(taskId: Long, empleadoId: Long): Result<String> = withContext(Dispatchers.IO) {
        val body = mapOf("empleadoId" to empleadoId)
        apiService.put("$TASKS_ENDPOINT/$taskId/assign", JsonConverter.toJson(body)) // Assuming an endpoint for assignment
            .map { "Tarea asignada a empleado exitosamente" }
    }

    override suspend fun updateTaskStatus(taskId: Long, newStatus: String): Result<String> = withContext(Dispatchers.IO) {
        val body = mapOf("estado" to newStatus)
        apiService.put("$TASKS_ENDPOINT/$taskId/status", JsonConverter.toJson(body)) // Assuming an endpoint for status update
            .map { "Estado de tarea actualizado a '$newStatus'." }
    }
}
