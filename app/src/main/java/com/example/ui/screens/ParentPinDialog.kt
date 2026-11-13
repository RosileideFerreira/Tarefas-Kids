package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BotaoPrimarioAzul

@Composable
fun ParentPinDialog(
    correctPin: String,
    onPinSuccess: () -> Unit,
    onUsePasswordLogin: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPinVisible by remember { mutableStateOf(false) }

    fun validateAndSubmit() {
        val trimmed = enteredPin.trim()
        when {
            trimmed.isEmpty() -> {
                errorMessage = "Informe o PIN de 4 dígitos para entrar."
            }
            trimmed.length < 4 || !trimmed.all { it.isDigit() } -> {
                errorMessage = "O PIN deve ter exatamente 4 dígitos numéricos."
            }
            correctPin.length != 4 -> {
                errorMessage = "PIN não configurado. Entre usando e-mail e senha."
            }
            trimmed != correctPin -> {
                errorMessage = "PIN incorreto. Verifique e tente novamente."
            }
            else -> {
                errorMessage = null
                onPinSuccess()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .widthIn(max = 460.dp)
            .imePadding(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Acesso protegido por PIN",
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Digite o PIN de 4 dígitos do responsável para acessar o gerenciamento.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }.take(4)
                        enteredPin = digitsOnly
                        errorMessage = null
                        if (digitsOnly.length == 4) {
                            if (correctPin.length == 4 && digitsOnly == correctPin) {
                                onPinSuccess()
                            } else {
                                errorMessage = if (correctPin.length != 4) {
                                    "PIN não configurado. Entre usando e-mail e senha."
                                } else {
                                    "PIN incorreto. Verifique e tente novamente."
                                }
                            }
                        }
                    },
                    label = { Text("PIN de 4 dígitos") },
                    placeholder = { Text("Digite os 4 números") },
                    singleLine = true,
                    isError = errorMessage != null,
                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { validateAndSubmit() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { isPinVisible = !isPinVisible }) {
                            Icon(
                                imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPinVisible) "Ocultar PIN" else "Mostrar PIN"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_pin_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.testTag("parent_pin_error")
                    )
                }

                TextButton(
                    onClick = onUsePasswordLogin,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("pin_use_password_button")
                ) {
                    Text(
                        text = "Esqueceu o PIN? Entrar com e-mail e senha",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { validateAndSubmit() },
                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("pin_confirm_button")
            ) {
                Text("Entrar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
