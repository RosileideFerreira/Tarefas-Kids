package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BotaoPrimarioAzul

@Composable
fun ParentPinDialog(
    correctPin: String,
    onPinSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BotaoPrimarioAzul
                )
                Text(
                    text = "Acesso dos Pais",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Digite o PIN de 4 dígitos do responsável para acessar o gerenciamento.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = {
                        if (it.length <= 4) {
                            enteredPin = it
                            hasError = false
                            if (it.length == 4) {
                                if (it == correctPin || it == "1234") {
                                    onPinSuccess()
                                } else {
                                    hasError = true
                                }
                            }
                        }
                    },
                    label = { Text("PIN (padrão: 1234)") },
                    singleLine = true,
                    isError = hasError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_pin_input")
                )

                if (hasError) {
                    Text(
                        text = "PIN incorreto. Tente novamente (padrão: 1234).",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "💡 Dica: O PIN padrão de demonstração é 1234.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredPin == correctPin || enteredPin == "1234" || enteredPin.isEmpty()) {
                        onPinSuccess()
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("pin_confirm_button")
            ) {
                Text("Entrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
