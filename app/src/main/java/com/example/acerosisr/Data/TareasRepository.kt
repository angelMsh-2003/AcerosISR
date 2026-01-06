package com.example.acerosisr.Data

import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.ProjectTasksSummary
import com.example.acerosisr.Model.Tareas
import com.example.acerosisr.Model.UsuarioTareaEspecifica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

interface TareasRepository {
    suspend fun getAllTasks(): Result<List<Tareas>>
    suspend fun getTaskById(taskId: Long): Result<Tareas>
    suspend fun createTask(tarea: Tareas): Result<String>
    suspend fun updateTask(tareaId: Long, tarea: Tareas): Result<String>
    suspend fun deleteTask(taskId: Long): Result<String>
    suspend fun assignTaskToEmployee(taskId: Long, empleadoId: Long): Result<String>
    suspend fun updateTaskStatus(taskId: Long, newStatus: String): Result<String>
    suspend fun getAllEmployees(): Result<List<Empleados>>
    suspend fun getProjectsTasksSummary(): Result<List<ProjectTasksSummary>>
    suspend fun getTasksByEmployee(empleadoId: Long): Result<List<UsuarioTareaEspecifica>>
}

class TareasRepositoryImpl(private val apiService: ApiService) : TareasRepository {
    private val TASKS_ENDPOINT = "api/tareas"
    private val EMPLOYEES_ENDPOINT = "api/empleados"
    private val PROJECTS_TASKS_SUMMARY_ENDPOINT = "api/proyectos/resumen-tareas"

    override suspend fun getAllTasks(): Result<List<Tareas>> = withContext(Dispatchers.IO) {
        apiService.get(TASKS_ENDPOINT).mapCatching { response ->
            val jsonArray = JSONArray(response)
            val list = mutableListOf<Tareas>()
            for (i in 0 until jsonArray.length()) list.add(jsonToTarea(jsonArray.getJSONObject(i)))
            list
        }
    }

    override suspend fun getTaskById(taskId: Long): Result<Tareas> = withContext(Dispatchers.IO) {
        apiService.get("$TASKS_ENDPOINT/$taskId").mapCatching { jsonToTarea(JSONObject(it)) }
    }

    override suspend fun createTask(tarea: Tareas): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("proyectoId", tarea.proyectoId)
            put("empleadoId", tarea.empleadoId)
            put("estado", tarea.estado)
            put("fechaInicio", tarea.fechaInicio?.time)
            put("fechaFin", tarea.fechaFin?.time)
            put("comentarios", tarea.comentarios)
        }
        apiService.post(TASKS_ENDPOINT, body.toString()).map { "Creada" }
    }

    override suspend fun updateTask(tareaId: Long, tarea: Tareas): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("proyectoId", tarea.proyectoId)
            put("empleadoId", tarea.empleadoId)
            put("estado", tarea.estado)
            put("fechaInicio", tarea.fechaInicio?.time)
            put("fechaFin", tarea.fechaFin?.time)
            put("comentarios", tarea.comentarios)
        }
        apiService.put("$TASKS_ENDPOINT/$tareaId", body.toString()).map { "Actualizada" }
    }

    override suspend fun deleteTask(taskId: Long): Result<String> = withContext(Dispatchers.IO) {
        apiService.delete("$TASKS_ENDPOINT/$taskId").map { "Eliminada" }
    }

    override suspend fun assignTaskToEmployee(taskId: Long, empleadoId: Long): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().put("empleadoId", empleadoId)
        apiService.put("$TASKS_ENDPOINT/$taskId/assign", body.toString()).map { "Asignada" }
    }

    override suspend fun updateTaskStatus(taskId: Long, newStatus: String): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().put("estado", newStatus)
        apiService.put("$TASKS_ENDPOINT/$taskId/status", body.toString()).map { "Estado actualizado" }
    }

    override suspend fun getAllEmployees(): Result<List<Empleados>> = withContext(Dispatchers.IO) {
        apiService.get(EMPLOYEES_ENDPOINT).mapCatching { response ->
            val jsonArray = JSONArray(response)
            val list = mutableListOf<Empleados>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Empleados(
                        IdEmpleado = obj.optLong("id"),
                        NumEmpleado = obj.optLong("NumEmpleado"),
                        NombreEmpleado = obj.optString("NombreEmpleado"), // Arregla nombres con acentos
                        Cargo = obj.optString("Cargo"),
                        Correo = obj.optString("Correo"),
                        Estado = obj.optLong("Estado", 1),
                        Password_hash = null, Salt = null, PasswordPlaintext = null
                    )
                )
            }
            list
        }
    }

    override suspend fun getProjectsTasksSummary(): Result<List<ProjectTasksSummary>> = withContext(Dispatchers.IO) {
        apiService.get(PROJECTS_TASKS_SUMMARY_ENDPOINT).mapCatching { response ->
            val jsonArray = JSONArray(response)
            val list = mutableListOf<ProjectTasksSummary>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ProjectTasksSummary(
                        proyectoId = obj.getInt("proyecto_id"),
                        titulo = obj.optString("titulo", ""), // Arregla títulos de proyecto
                        estado = obj.optString("estado", ""),
                        tareasAsignadas = obj.optBoolean("tareasAsignadas"),
                        tareasCumplidas = obj.optInt("tareasCumplidas"),
                        tareasEnProceso = obj.optInt("tareasEnProceso"),
                        tareasPendientes = obj.optInt("tareasPendientes")
                    )
                )
            }
            list
        }
    }

    override suspend fun getTasksByEmployee(empleadoId: Long): Result<List<UsuarioTareaEspecifica>> = withContext(Dispatchers.IO) {
        apiService.get("$TASKS_ENDPOINT/empleados/$empleadoId").mapCatching { response ->
            val jsonArray = JSONArray(response)
            val list = mutableListOf<UsuarioTareaEspecifica>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    UsuarioTareaEspecifica(
                        tareaId = obj.getLong("tareaId"),
                        empleadoId = obj.getLong("empleadoId"),
                        estado = obj.optString("estado"),
                        fechaInicio = if (obj.isNull("fechaInicio")) null else Date(obj.getLong("fechaInicio")),
                        fechaFin = if (obj.isNull("fechaFin")) null else Date(obj.getLong("fechaFin")),
                        comentarios = obj.optString("comentarios", "Sin comentarios"), // Arregla comentarios
                        proyectoId = obj.getLong("proyectoId"),
                        proyectoTitulo = obj.optString("proyectoTitulo", "Sin Título"), // Arregla título
                        proyectoDescripcion = obj.optString("proyectoDescripcion", "")
                    )
                )
            }
            list
        }
    }

    private fun jsonToTarea(obj: JSONObject): Tareas {
        return Tareas(
            tareaId = obj.optLong("tareaId"),
            proyectoId = obj.optLong("proyectoId"),
            empleadoId = if (obj.isNull("empleadoId")) null else obj.getLong("empleadoId"),
            estado = obj.optString("estado"),
            fechaInicio = if (obj.isNull("fechaInicio")) null else Date(obj.getLong("fechaInicio")),
            fechaFin = if (obj.isNull("fechaFin")) null else Date(obj.getLong("fechaFin")),
            comentarios = obj.optString("comentarios") // Arregla comentarios
        )
    }
}