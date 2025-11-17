package com.example.acerosisr.Data

import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.SelectActualUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Data class for login request to backend
data class LoginRequest(val numEmpleado: Long, val passwordPlaintext: String)
// Data class for update request
data class UserUpdateData(
    val Correo: String? = null,
    val Estado: Long? = null,
    val PasswordPlaintext: String? = null // For updating password via backend
)

interface UserRepository {
    suspend fun registerNewUser(empleado: Empleados): Result<String>
    suspend fun updateUser(numEmpleado: Long, updateData: UserUpdateData): Result<String>
    suspend fun checkUserExists(numEmpleado: Long): Result<Boolean>
    suspend fun getEmployeeDetails(numEmpleado: Long): Result<Empleados>
    suspend fun getActualUser(): Result<SelectActualUser>
    suspend fun insertActualUser(user: SelectActualUser): Result<String>
    suspend fun deleteActualUser(): Result<String>
    suspend fun countEmployees(): Result<Long>
    suspend fun loginUser(request: LoginRequest): Result<String>
    suspend fun updatePassword(numEmpleado: Long, newPasswordPlaintext: String): Result<String>
}

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {
    private val USERS_ENDPOINT = "api/empleados"
    private val ACTUAL_USER_ENDPOINT = "api/actual-user" // Endpoint for currently logged-in user

    override suspend fun registerNewUser(empleado: Empleados): Result<String> = withContext(Dispatchers.IO) {
        val empleadoMap = mutableMapOf<String, Any?>(
            "NumEmpleado" to empleado.NumEmpleado,
            "NombreEmpleado" to empleado.NombreEmpleado,
            "Cargo" to empleado.Cargo,
            "Correo" to empleado.Correo,
            "Estado" to empleado.Estado
        )
        empleado.PasswordPlaintext?.let { empleadoMap["PasswordPlaintext"] = it } // Send plaintext password to backend

        apiService.post(USERS_ENDPOINT, JsonConverter.toJson(empleadoMap))
            .map { "Registro exitoso" }
    }

    override suspend fun updateUser(numEmpleado: Long, updateData: UserUpdateData): Result<String> = withContext(Dispatchers.IO) {
        val updateMap = mutableMapOf<String, Any?>()
        updateData.Correo?.let { updateMap["Correo"] = it }
        updateData.Estado?.let { updateMap["Estado"] = it }
        updateData.PasswordPlaintext?.let { updateMap["PasswordPlaintext"] = it } // Send plaintext password for update

        apiService.put("$USERS_ENDPOINT/$numEmpleado", JsonConverter.toJson(updateMap))
            .map { "Actualización exitosa" }
    }

    override suspend fun checkUserExists(numEmpleado: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        apiService.get("$USERS_ENDPOINT/$numEmpleado")
            .map { responseJson ->
                responseJson.isNotBlank() && !responseJson.contains("Error") // Basic check, backend should return 404 for non-existent
            }
            .recover { false } // If API returns error (e.g., 404), user doesn't exist
    }

    override suspend fun getEmployeeDetails(numEmpleado: Long): Result<Empleados> = withContext(Dispatchers.IO) {
        apiService.get("$USERS_ENDPOINT/$numEmpleado")
            .mapCatching { responseJson -> // Using mapCatching for safer parsing
                val data = JsonConverter.fromJson(responseJson)
                Empleados(
                    NumEmpleado = (data["NumEmpleado"] as? Long) ?: 0L,
                    NombreEmpleado = (data["NombreEmpleado"] as? String) ?: "",
                    Cargo = (data["Cargo"] as? String) ?: "",
                    Correo = data["Correo"] as? String,
                    Password_hash = data["Password_hash"] as? String,
                    Salt = data["Salt"] as? String,
                    Estado = (data["Estado"] as? Long) ?: 1L
                )
            }
    }

    override suspend fun getActualUser(): Result<SelectActualUser> = withContext(Dispatchers.IO) {
        apiService.get(ACTUAL_USER_ENDPOINT) 
            .mapCatching { responseJson -> // Using mapCatching for safer parsing
                val data = JsonConverter.fromJson(responseJson)
                SelectActualUser(
                    ID = (data["ID"] as? Long) ?: 0L,
                    UserId = (data["UserId"] as? Long) ?: 0L,
                    NombreEmpleado = (data["NombreEmpleado"] as? String) ?: "",
                    Cargo = (data["Cargo"] as? String) ?: ""
                )
            }
            .recover { // If API returns error, return a default/empty one, or propagate failure
                if (it is Exception) {
                    println("Error fetching actual user, returning default: ${it.message}")
                    Result.success(SelectActualUser(0L,0L,"","")).getOrThrow() // This is still a bit awkward, ideally an empty object or null from the API
                } else {
                    throw it
                }
            }
    }

    override suspend fun insertActualUser(user: SelectActualUser): Result<String> = withContext(Dispatchers.IO) {
        val userMap = mapOf(
            "ID" to user.ID,
            "UserId" to user.UserId,
            "NombreEmpleado" to user.NombreEmpleado,
            "Cargo" to user.Cargo
        )
        apiService.post(ACTUAL_USER_ENDPOINT, JsonConverter.toJson(userMap))
            .map { "Usuario actual insertado" }
    }

    override suspend fun deleteActualUser(): Result<String> = withContext(Dispatchers.IO) {
        apiService.delete(ACTUAL_USER_ENDPOINT) 
            .map { "Usuario actual eliminado" }
    }

    override suspend fun countEmployees(): Result<Long> = withContext(Dispatchers.IO) {
        apiService.get("$USERS_ENDPOINT/count") 
            .mapCatching { responseJson ->
                val data = JsonConverter.fromJson(responseJson)
                (data["count"] as? Long) ?: 0L
            }
    }

    override suspend fun loginUser(request: LoginRequest): Result<String> = withContext(Dispatchers.IO) {
        apiService.post("$USERS_ENDPOINT/login", JsonConverter.toJson(mapOf(
            "numEmpleado" to request.numEmpleado,
            "passwordPlaintext" to request.passwordPlaintext
        )))
            .map { "Login exitoso" }
            .recover { e -> Result.failure<String>(RuntimeException("Credenciales inválidas: ${e.message}")) } as Result<String> // Ensured explicit <String>
    }

    override suspend fun updatePassword(numEmpleado: Long, newPasswordPlaintext: String): Result<String> = withContext(Dispatchers.IO) {
        apiService.put("$USERS_ENDPOINT/$numEmpleado/password", JsonConverter.toJson(mapOf(
            "passwordPlaintext" to newPasswordPlaintext
        )))
        .map { "Contraseña actualizada exitosamente" }
    }
}
