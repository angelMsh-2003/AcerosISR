package com.example.acerosisr.Funtion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.InfoCampo
import com.example.acerosisr.ui.theme.TextColorWhite

@Composable
fun CampoAsignacionTareas(
    infoCampo: InfoCampo,
    icon: ImageVector,
    color: Color,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.1f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = infoCampo.id, style = MaterialTheme.typography.bodySmall)
                Text(text = infoCampo.tittle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = infoCampo.description, style = MaterialTheme.typography.bodyMedium)
                Row {
                    Text(text = infoCampo.bodyLeft, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text(text = infoCampo.bodyRight, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color),
                colors = IconButtonDefaults.iconButtonColors( // Corrected: Used IconButtonDefaults
                    contentColor = TextColorWhite
                )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Action Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
