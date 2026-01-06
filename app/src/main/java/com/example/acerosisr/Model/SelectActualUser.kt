package com.example.acerosisr.Model

data class SelectActualUser(
    val ID: Long,
    val UserId: Long,
    val NombreEmpleado: String,
    val Cargo: String,
    val deviceId: String? = null,
    val deviceModel: String? = null
)
