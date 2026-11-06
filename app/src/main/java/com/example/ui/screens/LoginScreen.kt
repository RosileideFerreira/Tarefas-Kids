package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BotaoPrimarioAzul
import com.example.ui.theme.LinkTextoClicavel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginWithPassword: (email: String, pass: String) -> Boolean,
    onLoginWithPin: (pin: String) -> Boolean,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var usePinLogin by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Login do Responsável", fontWeight = FontWeight.Bold, fontSize = 19.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("login_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BotaoPrimarioAzul.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockPerson,
                    contentDescription = null,
                    tint = BotaoPrimarioAzul,
                    modifier = Modifier.size(38.dp)
                )
            }

            Text(
                text = if (usePinLogin) "Entrar com PIN" else "Entrar com E-mail e Senha",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Área protegida para os responsáveis gerenciarem tarefas e saldos.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (usePinLogin) {
                        OutlinedTextField(
                            value = pin,
                            onValueChange = {
                                if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it
                            },
                            label = { Text("PIN de 4 dígitos") },
                            placeholder = { Text("Ex: 1234") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_pin_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) }
                        )

                        Button(
                            onClick = {
                                if (pin.length != 4) {
                                    errorMessage = "Digite os 4 dígitos do PIN."
                                    return@Button
                                }
                                val ok = onLoginWithPin(pin)
                                if (!ok) {
                                    errorMessage = "PIN incorreto!"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_pin_submit"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
                        ) {
                            Text("Entrar com PIN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    } else {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("E-mail do responsável") },
                            placeholder = { Text("exemplo@email.com") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Senha") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Alternar visibilidade"
                                    )
                                }
                            }
                        )

                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Preencha e-mail e senha."
                                    return@Button
                                }
                                val ok = onLoginWithPassword(email, password)
                                if (!ok) {
                                    errorMessage = "E-mail ou senha incorretos."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_password_submit"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
                        ) {
                            Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider()

                    TextButton(
                        onClick = {
                            usePinLogin = !usePinLogin
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (usePinLogin) "Prefiro entrar com E-mail e Senha" else "Entrar rapidamente com PIN de 4 dígitos",
                            color = LinkTextoClicavel,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.testTag("nav_to_register_button")
            ) {
                Text(
                    text = "Ainda não tem conta? Cadastrar Família ✨",
                    color = BotaoPrimarioAzul,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
