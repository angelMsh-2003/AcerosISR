package com.example.acerosisr.Data

import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.SelectActualUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class LoginRequest(val numEmpleado: Long, val passwordPlaintext: String)
data class UserUpdateData(
    val NumEmpleado: Long? = null,
    val Nombre: String? = null,
    val Cargo: String? = null,
    val Correo: String? = null,
    val Estado: Long? = null,
    val PasswordPlaintext: String? = null
)

interface UserRepository {
    suspend fun getAllEmployees(): Result<List<Empleados>>
    suspend fun registerNewUser(empleado: Empleados): Result<String>
    suspend fun updateUser(idBase: Long, updateData: UserUpdateData): Result<String>
    suspend fun checkUserExists(numEmpleado: Long): Result<Boolean>
    suspend fun getEmployeeDetails(numEmpleado: Long): Result<Empleados>
    suspend fun getActualUser(): Result<SelectActualUser>
    suspend fun insertActualUser(user: SelectActualUser): Result<String>
    suspend fun deleteActualUser(): Result<String>
    suspend fun countEmployees(): Result<Long>
    suspend fun loginUser(request: LoginRequest): Result<Empleados>
    suspend fun updatePassword(numEmpleado: Long, newPasswordPlaintext: String): Result<String>
    suspend fun validateRecovery(numEmpleado: Long, correo: String): Result<Long>
    suspend fun getEmpleadoIdByNum(numEmpleado: Long): Result<Long>
    suspend fun getEmployeeByNum(numEmpleado: Long): Result<Empleados>
    suspend fun updateEmployeeByNum(numEmpleado: Long, correo: String?, passwordPlaintext: String?): Result<String>

}

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    private val USERS_ENDPOINT = "api/empleados"
    private val RECOVER_ENDPOINT = "api/empleados/recover"
    private val ACTUAL_USER_ENDPOINT = "api/actual-user"
    private val ID_BY_NUM_ENDPOINT = "api/actual-user/empleado-id"

    override suspend fun getAllEmployees(): Result<List<Empleados>> = withContext(Dispatchers.IO) {
        apiService.get(USERS_ENDPOINT).mapCatching { response ->
            val jsonArray = JSONArray(response)
            val list = mutableListOf<Empleados>()
            for (i in 0 until jsonArray.length()) list.add(jsonToEmpleado(jsonArray.getJSONObject(i)))
            list
        }
    }

    override suspend fun getEmpleadoIdByNum(numEmpleado: Long): Result<Long> = withContext(Dispatchers.IO) {
        val body = JSONObject().put("NumEmpleado", numEmpleado)
        apiService.post(ID_BY_NUM_ENDPOINT, body.toString()).mapCatching {
            JSONObject(it).getLong("empleado_id")
        }
    }
    override suspend fun getEmployeeByNum(numEmpleado: Long): Result<Empleados> = withContext(Dispatchers.IO) {
        // endpoint: api/empleados/num/12345
        apiService.get("$USERS_ENDPOINT/num/$numEmpleado")
            .mapCatching { jsonToEmpleado(JSONObject(it)) }
    }

    // === NUEVO: PUT /empleados/num/{num} ===
    override suspend fun updateEmployeeByNum(numEmpleado: Long, correo: String?, passwordPlaintext: String?): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            // Solo agregamos al JSON si el valor no es nulo ni vacío
            if (!correo.isNullOrBlank()) put("Correo", correo)
            if (!passwordPlaintext.isNullOrBlank()) put("PasswordPlaintext", passwordPlaintext)
        }

        // Si el cuerpo está vacío, no tiene sentido llamar a la API
        if (body.length() == 0) {
            return@withContext Result.success("Sin cambios realizados")
        }

        apiService.put("$USERS_ENDPOINT/num/$numEmpleado", body.toString())
            .map { "Información actualizada correctamente" }
    }


    override suspend fun registerNewUser(empleado: Empleados): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("NumEmpleado", empleado.NumEmpleado)
            put("NombreEmpleado", empleado.NombreEmpleado)
            put("Cargo", empleado.Cargo)
            put("Correo", empleado.Correo)
            put("Estado", empleado.Estado)
            empleado.PasswordPlaintext?.let { put("PasswordPlaintext", it) }
        }
        apiService.post(USERS_ENDPOINT, body.toString()).map { "Registro exitoso" }
    }

    override suspend fun updateUser(idBase: Long, updateData: UserUpdateData): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            updateData.NumEmpleado?.let { put("NumEmpleado", it) }
            updateData.Nombre?.let { put("NombreEmpleado", it) }
            updateData.Cargo?.let { put("Cargo", it) }
            updateData.Correo?.let { put("Correo", it) }
            updateData.Estado?.let { put("Estado", it) }
            updateData.PasswordPlaintext?.let { put("PasswordPlaintext", it) }
        }
        apiService.put("$USERS_ENDPOINT/$idBase", body.toString()).map { "Actualización exitosa" }
    }

    override suspend fun checkUserExists(numEmpleado: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        apiService.get("$USERS_ENDPOINT/$numEmpleado").map { it.isNotBlank() && !it.contains("Error") }.recover { false }
    }

    override suspend fun getEmployeeDetails(numEmpleado: Long): Result<Empleados> = withContext(Dispatchers.IO) {
        apiService.get("$USERS_ENDPOINT/$numEmpleado").mapCatching { jsonToEmpleado(JSONObject(it)) }
    }

    override suspend fun getActualUser(): Result<SelectActualUser> = withContext(Dispatchers.IO) {
        apiService.get(ACTUAL_USER_ENDPOINT).mapCatching {
            val json = JSONObject(it)
            SelectActualUser(
                ID = json.optLong("ID"),
                UserId = json.optLong("UserId"),
                NombreEmpleado = json.optString("NombreEmpleado", ""), // Arregla nombre en menú
                Cargo = json.optString("Cargo", ""),
                deviceId = json.optString("deviceId", null),
                deviceModel = json.optString("deviceModel", null)
            )
        }.recover { SelectActualUser(0L, 0L, "", "") }
    }

    override suspend fun insertActualUser(user: SelectActualUser): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("ID", user.ID)
            put("UserId", user.UserId)
            put("NombreEmpleado", user.NombreEmpleado)
            put("Cargo", user.Cargo)
            user.deviceId?.let { put("deviceId", it) }
            user.deviceModel?.let { put("deviceModel", it) }
        }
        apiService.post(ACTUAL_USER_ENDPOINT, body.toString()).map { "Insertado" }
    }

    override suspend fun deleteActualUser(): Result<String> = withContext(Dispatchers.IO) {
        apiService.delete(ACTUAL_USER_ENDPOINT).map { "Eliminado" }
    }

    override suspend fun countEmployees(): Result<Long> = withContext(Dispatchers.IO) {
        apiService.get("$USERS_ENDPOINT/count").mapCatching { JSONObject(it).optLong("count", 0L) }
    }

    override suspend fun loginUser(request: LoginRequest): Result<Empleados> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("NumEmpleado", request.numEmpleado)
            put("PasswordPlaintext", request.passwordPlaintext)
        }
        apiService.post("$USERS_ENDPOINT/login", body.toString()).mapCatching { jsonToEmpleado(JSONObject(it)) }
    }

    override suspend fun updatePassword(numEmpleado: Long, newPasswordPlaintext: String): Result<String> = withContext(Dispatchers.IO) {
        val body = JSONObject().put("passwordPlaintext", newPasswordPlaintext)
        apiService.put("$USERS_ENDPOINT/$numEmpleado/password", body.toString()).map { "OK" }
    }

    override suspend fun validateRecovery(numEmpleado: Long, correo: String): Result<Long> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("NumEmpleado", numEmpleado)
            put("Correo", correo)
        }
        apiService.post(RECOVER_ENDPOINT, body.toString()).mapCatching { JSONObject(it).optLong("EmpleadoId", 0L) }
    }

    private fun jsonToEmpleado(json: JSONObject): Empleados {
        return Empleados(
            IdEmpleado = json.optLong("id"),
            NumEmpleado = json.optLong("NumEmpleado"),
            NombreEmpleado = json.optString("NombreEmpleado", ""),
            Cargo = json.optString("Cargo", ""),
            Correo = json.optString("Correo", ""),
            // Buscamos la clave exacta o en minúsculas por si acaso
            Password_hash = if (json.has("Password_hash")) json.getString("Password_hash") else json.optString("password_hash", ""),
            Salt = null,
            Estado = json.optLong("Estado", 1),
            PasswordPlaintext = null // El backend no suele devolver el plaintext por seguridad, usamos el hash para mostrar algo
        )
    }

}