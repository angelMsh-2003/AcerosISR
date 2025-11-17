package com.example.acerosisr.Funtion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.acerosisr.ui.theme.*
import com.example.acerosisr.Model.InfoViewAlert // Assuming Model will be refactored too or removed
// import com.example.acerosisr.ViewModel.HerramientaViewModel // This import might be removed or updated based on other refactors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlertDialog(showDialog: Boolean, onDismiss: () -> Unit, messages: String, color: Color, icon: ImageVector) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            ),
            content = {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, color, RoundedCornerShape(16.dp))
                        .background(BackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "IconAlert",
                            tint = color,
                            modifier = Modifier.size(50.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = messages,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextColorBlack,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { onDismiss() },
                            colors = ButtonColors(
                                containerColor = color,
                                contentColor = TextColorWhite,
                                disabledContentColor = color,
                                disabledContainerColor = BackgroundColor
                            )) {
                            Text("Aceptar",  style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomViewDialog(
    showDialog: Boolean,
    tittle: String,
    infoViewAlert: Array<String>,
    fraction: Float,
    color: Color,
    icon: ImageVector,
    bandButton : Boolean,
    onDismiss: () -> Unit,
    onDismissCancel: () -> Unit,
    onDismissOk: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth(0.98f).fillMaxHeight(fraction),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            ),
            content = {
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, color, RoundedCornerShape(16.dp))
                        .background(BackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tittle,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextColorBlack,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { onDismiss() },
                                modifier = Modifier.size(35.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "icon",
                                    modifier = Modifier.fillMaxSize(),
                                    tint = color
                                )
                            }
                        }
                        for (item in infoViewAlert){
                            Text(text = item,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextColorBlack,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                            HorizontalDivider(thickness = 1.dp, color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        if (bandButton){
                            Row (modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onDismissOk()
                                    },
                                    colors = ButtonColors(
                                        containerColor = SecondaryColor,
                                        contentColor = TextColorWhite,
                                        disabledContentColor = ErrorColor,
                                        disabledContainerColor = BackgroundColor
                                    )
                                ) {
                                    Text("En proceso", style = MaterialTheme.typography.bodyLarge)
                                }
                                OutlinedButton(
                                    onClick = {onDismissCancel()},
                                    colors = ButtonColors(
                                        containerColor = PrimaryColor, // fondo
                                        contentColor = TextColorWhite, // texto
                                        disabledContentColor = BackgroundColor, // fondo
                                        disabledContainerColor = TextColorBlack // texto
                                    )
                                ) {
                                    Text("Logrado", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlertTwoBottons (
    showDialog: Boolean,
    onDismissCancel: () -> Unit,
    onDismissOk: () -> Unit,
    messages: String) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissCancel,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            ), content = {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, ErrorColor, RoundedCornerShape(16.dp))
                        .background(BackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "IconAlert",
                            tint = ErrorColor,
                            modifier = Modifier.size(50.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = messages,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextColorBlack,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row (modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onDismissOk()
                                          },
                                colors = ButtonColors(
                                    containerColor = ErrorColor,
                                    contentColor = TextColorWhite,
                                    disabledContentColor = ErrorColor,
                                    disabledContainerColor = BackgroundColor
                                )
                            ) {
                                Text("Aceptar", style = MaterialTheme.typography.bodyLarge)
                            }
                            OutlinedButton(
                                onClick = { onDismissCancel() },
                                colors = ButtonColors(
                                    containerColor = TextColorWhite, // fondo
                                    contentColor = TextColorBlack, // text
                                    disabledContentColor = BackgroundColor, // fondo
                                    disabledContainerColor = TextColorBlack // texto
                                )
                            ) {
                                Text("Cancelar", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        )
    }
}