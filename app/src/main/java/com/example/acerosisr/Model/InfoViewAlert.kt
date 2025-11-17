package com.example.acerosisr.Model

data class InfoViewAlert(
    val title: String,
    val message: String,
    val iconResId: Int? = null, // Optional resource ID for an icon
    val type: AlertType = AlertType.INFO
)

enum class AlertType {
    INFO, WARNING, ERROR, SUCCESS
}
