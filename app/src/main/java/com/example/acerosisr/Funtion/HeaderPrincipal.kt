package com.example.acerosisr.Funtion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Changed import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite
import com.example.acerosisr.R // Added import for Android resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Encabezado() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(PrimaryColor) // Changed from Theme.primaryColor
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo
        Image(
            painterResource(id = R.drawable.isr_logo), // Changed call
            contentDescription = "Logo_ISR",
            modifier = Modifier.size(40.dp)
        )
        // Title
        Text(
            text = "Aceros ISR",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextColorWhite // Changed from Theme.textColorWhite
        )
        // Placeholder for an optional icon or menu button
        Box(modifier = Modifier.size(40.dp)) { /* Optional menu icon */ }
    }
}

@Preview(showBackground = true)
@Composable
fun EncabezadoPreview() {
    Encabezado()
}
