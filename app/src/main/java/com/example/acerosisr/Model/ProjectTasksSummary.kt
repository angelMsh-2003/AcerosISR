package com.example.acerosisr.Model

data class ProjectTasksSummary(
    val proyectoId: Int,
    val titulo: String,
    val estado: String,
    val tareasAsignadas: Boolean,
    val tareasCumplidas: Int,
    val tareasEnProceso: Int,
    val tareasPendientes: Int
)
