package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

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
    var isPinVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun submitPin() {
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            errorMessage = "Digite os 4 dígitos numéricos do PIN."
            return
        }
        val ok = onLoginWithPin(pin)
        if (!ok) {
            errorMessage = "PIN incorreto!"
        }
    }

    fun submitPassword() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Preencha e-mail e senha."
            return
        }
        val ok = onLoginWithPassword(email, password)
        if (!ok) {
            errorMessage = "E-mail ou senha incorretos."
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
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
        AdaptiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) { adaptive ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = adaptive.formMaxWidth)
                        .padding(horizontal = adaptive.horizontalPadding, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    if (adaptive.isCompactHeight) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(BotaoPrimarioAzul.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockPerson,
                                    contentDescription = null,
                                    tint = BotaoPrimarioAzul,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (usePinLogin) "Entrar com PIN" else "Entrar com E-mail e Senha",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Área protegida para os responsáveis gerenciarem tarefas e saldos.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
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
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Área protegida para os responsáveis gerenciarem tarefas e saldos.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

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
                                        val digits = it.filter { c -> c.isDigit() }.take(4)
                                        pin = digits
                                        errorMessage = null
                                    },
                                    label = { Text("PIN de 4 dígitos") },
                                    placeholder = { Text("Digite os 4 números") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_pin_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    isError = errorMessage != null,
                                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { submitPin() }),
                                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                    trailingIcon = {
                                        IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                            Icon(
                                                imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Alternar visibilidade do PIN"
                                            )
                                        }
                                    }
                                )

                                Button(
                                    onClick = { submitPin() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 50.dp)
                                        .testTag("login_pin_submit"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
                                ) {
                                    Text("Entrar com PIN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            } else {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        errorMessage = null
                                    },
                                    label = { Text("E-mail do responsável") },
                                    placeholder = { Text("exemplo@email.com") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_email_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                                )

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        errorMessage = null
                                    },
                                    label = { Text("Senha") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { submitPassword() }),
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
                                    onClick = { submitPassword() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 50.dp)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                            ) {
                                Text(
                                    text = if (usePinLogin) "Prefiro entrar com E-mail e Senha" else "Entrar rapidamente com PIN de 4 dígitos",
                                    color = LinkTextoClicavel,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("nav_to_register_button")
                    ) {
                        Text(
                            text = "Ainda não tem conta? Cadastrar Família ✨",
                            color = BotaoPrimarioAzul,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
