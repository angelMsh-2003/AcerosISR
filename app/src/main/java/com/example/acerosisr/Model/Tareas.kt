package com.example.acerosisr.Model

import java.util.Date

data class Tareas(
    val tareaId: Long = 0L,
    val proyectoId: Long = 0L,
    val empleadoId: Long? = null,
    val estado: String = "",
    val fechaInicio: Date? = null,
    val fechaFin: Date? = null,
    val comentarios: String? = null
)
