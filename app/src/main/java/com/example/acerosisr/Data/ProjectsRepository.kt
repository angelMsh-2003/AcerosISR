package com.example.acerosisr.Data

import com.example.acerosisr.Model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

interface ProjectsRepository {
    suspend fun getAllProjects(): Result<List<Project>>
    suspend fun getProjectById(id: Int): Result<Project>
    suspend fun createProject(project: Project): Result<Project>
    suspend fun updateProject(project: Project): Result<Project>
    suspend fun deleteProject(id: Int): Result<Unit>
}

class ProjectsRepositoryImpl(private val apiService: ApiService) : ProjectsRepository {
    companion object {
        private const val PROJECTS_ENDPOINT = "api/proyectos"
    }

    override suspend fun getAllProjects(): Result<List<Project>> = withContext(Dispatchers.IO) {
        apiService.get(PROJECTS_ENDPOINT).mapCatching { response ->
            val list = mutableListOf<Project>()
            val trimmed = response.trim()
            if (trimmed.startsWith("[")) {
                val jsonArray = JSONArray(trimmed)
                for (i in 0 until jsonArray.length()) {
                    list.add(jsonToProject(jsonArray.getJSONObject(i)))
                }
            } else if (trimmed.startsWith("{")) {
                list.add(jsonToProject(JSONObject(trimmed)))
            }
            list
        }
    }

    override suspend fun getProjectById(id: Int): Result<Project> = withContext(Dispatchers.IO) {
        apiService.get("$PROJECTS_ENDPOINT/$id").mapCatching { jsonToProject(JSONObject(it)) }
    }

    override suspend fun createProject(project: Project): Result<Project> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("titulo", project.titulo)
            put("descripcion", project.descripcion)
            put("cliente", project.cliente)
            put("estado", project.estado)
            project.registradoPor?.let { put("registrado_por", it) }
        }
        apiService.post(PROJECTS_ENDPOINT, body.toString()).mapCatching { jsonToProject(JSONObject(it)) }
    }

    override suspend fun updateProject(project: Project): Result<Project> = withContext(Dispatchers.IO) {
        val id = project.id ?: throw Exception("ID requerido")
        val body = JSONObject().apply {
            put("titulo", project.titulo)
            put("descripcion", project.descripcion)
            put("cliente", project.cliente)
            put("estado", project.estado)
            project.registradoPor?.let { put("registrado_por", it) }
        }
        apiService.put("$PROJECTS_ENDPOINT/$id", body.toString()).map { project }
    }

    override suspend fun deleteProject(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        apiService.delete("$PROJECTS_ENDPOINT/$id").map { }
    }

    private fun jsonToProject(json: JSONObject): Project {
        val id = if (json.has("proyecto_id")) json.getInt("proyecto_id") else json.optInt("id")
        return Project(
            id = id,
            titulo = json.optString("titulo", ""), // Arregla acentos en títulos
            descripcion = json.optString("descripcion", ""), // Arregla acentos en descripción
            cliente = json.optString("cliente", ""),
            estado = json.optString("estado", ""),
            fechaRegistro = json.optString("fecha_registro", ""),
            registradoPor = if (json.isNull("registrado_por")) null else json.getInt("registrado_por")
        )
    }
}