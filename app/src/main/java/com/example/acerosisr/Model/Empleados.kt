package com.example.acerosisr.Model

data class Empleados(
    val NumEmpleado: Long,
    val NombreEmpleado: String,
    val Cargo: String,
    val Correo: String? = null,
    val Password_hash: String? = null, // Still keep for reading existing users, but not for new registration
    val Salt: String? = null, // Still keep for reading existing users, but not for new registration
    val Estado: Long = 1, // 1 for active, 0 for inactive
    val PasswordPlaintext: String? = null // Temporary field for sending password to backend
)
