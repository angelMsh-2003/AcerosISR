package com.example.acerosisr.Model
import com.google.gson.annotations.SerializedName // Importante
import java.util.Date

data class Empleados(
    @SerializedName("id") // Mapea el json "id" a esta variable
    val IdEmpleado: Long? = null,
    val NumEmpleado: Long,
    val NombreEmpleado: String,
    val Cargo: String,
    val Correo: String? = null,
    val Password_hash: String? = null,
    val Salt: String? = null,
    val Estado: Long = 1,
    val PasswordPlaintext: String? = null
)

data class UsuarioTareaEspecifica(
    val tareaId: Long,
    val empleadoId: Long,
    val estado: String, // "Pendiente", "En proceso", "Completada"
    val fechaInicio: Date?,
    val fechaFin: Date?,
    val comentarios: String?,
    val proyectoId: Long,
    val proyectoTitulo: String,
    val proyectoDescripcion: String
)