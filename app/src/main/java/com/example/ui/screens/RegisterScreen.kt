package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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

val REGISTRATION_AVATARS = listOf(
    "🧒", "👧", "👦", "🧑", "👶", "🦊", "🐼", "🐸", "🦁", "🐰", "🦄", "🐯", "🚀", "⭐", "🎨", "⚽"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (
        name: String,
        email: String,
        pass: String,
        pin: String,
        children: List<Pair<String, String>>
    ) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var parentName by remember { mutableStateOf("") }
    var parentEmail by remember { mutableStateOf("") }
    var parentPassword by remember { mutableStateOf("") }
    var parentPin by remember { mutableStateOf("1234") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Lista de crianças adicionadas
    val addedChildren = remember { mutableStateListOf<Pair<String, String>>() }
    var currentChildName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("🧒") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cadastro da Família",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("register_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(BotaoPrimarioAzul.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👨‍👩‍👧‍👦", fontSize = 28.sp)
                    }
                    Column {
                        Text(
                            text = "Configuração Inicial",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cadastre a conta do responsável e as crianças para começar",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // SEÇÃO 1: DADOS DO RESPONSÁVEL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = BotaoPrimarioAzul
                        )
                        Text(
                            text = "1. Conta do Responsável",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Nome do responsável ou família") },
                        placeholder = { Text("Ex: Juliana Santos / Família Santos") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = parentEmail,
                        onValueChange = { parentEmail = it },
                        label = { Text("E-mail de login") },
                        placeholder = { Text("seuemail@exemplo.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = parentPassword,
                        onValueChange = { parentPassword = it },
                        label = { Text("Senha (mínimo 6 caracteres)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Alternar visibilidade da senha"
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = parentPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) parentPin = it },
                        label = { Text("PIN de acesso rápido dos pais (4 dígitos)") },
                        placeholder = { Text("1234") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_pin_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                        supportingText = {
                            Text(
                                "Usado para entrar rapidamente na Área dos Pais e aprovar tarefas",
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            // SEÇÃO 2: CADASTRO DAS CRIANÇAS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChildCare,
                            contentDescription = null,
                            tint = SucessoVerdeAgua
                        )
                        Text(
                            text = "2. Crianças da Família",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Adicione as crianças que realizarão as tarefas. Cada uma terá sua própria tela!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Seletor de Avatar
                    Text(
                        text = "Escolha um avatar:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(REGISTRATION_AVATARS) { avatar ->
                            val isSelected = selectedAvatar == avatar
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) BotaoPrimarioAzul.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) BotaoPrimarioAzul else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = avatar }
                                    .testTag("avatar_option_$avatar"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatar, fontSize = 24.sp)
                            }
                        }
                    }

                    // Input do nome da criança com botão adicionar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currentChildName,
                            onValueChange = { currentChildName = it },
                            label = { Text("Nome da criança") },
                            placeholder = { Text("Ex: Arthur, Sofia...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("child_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (currentChildName.isNotBlank()) {
                                    addedChildren.add(Pair(currentChildName.trim(), selectedAvatar))
                                    currentChildName = ""
                                    focusManager.clearFocus()
                                }
                            })
                        )

                        Button(
                            onClick = {
                                if (currentChildName.isNotBlank()) {
                                    addedChildren.add(Pair(currentChildName.trim(), selectedAvatar))
                                    currentChildName = ""
                                    focusManager.clearFocus()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("add_child_item_button"),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adicionar")
                        }
                    }

                    // Exibição das crianças adicionadas
                    if (addedChildren.isNotEmpty()) {
                        Text(
                            text = "Crianças cadastradas (${addedChildren.size}):",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            addedChildren.forEachIndexed { index, (childName, avatar) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(text = avatar, fontSize = 26.sp)
                                            Text(
                                                text = childName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                        IconButton(onClick = { addedChildren.removeAt(index) }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "💡 Dica: Adicione pelo menos uma criança para facilitar o início.",
                            fontSize = 12.sp,
                            color = DestaqueAlertaAmarelo
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
            }

            // BOTÃO DE CONCLUIR CADASTRO
            Button(
                onClick = {
                    if (parentName.isBlank()) {
                        errorMessage = "Por favor, informe o nome do responsável ou da família."
                        return@Button
                    }
                    if (parentEmail.isBlank() || !parentEmail.contains("@")) {
                        errorMessage = "Por favor, informe um e-mail válido para login."
                        return@Button
                    }
                    if (parentPassword.length < 6) {
                        errorMessage = "A senha precisa ter pelo menos 6 caracteres."
                        return@Button
                    }
                    if (parentPin.length != 4) {
                        errorMessage = "O PIN precisa ter 4 dígitos numéricos."
                        return@Button
                    }
                    errorMessage = null
                    onRegisterSuccess(
                        parentName.trim(),
                        parentEmail.trim().lowercase(),
                        parentPassword,
                        parentPin,
                        addedChildren.toList()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_registration_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
            ) {
                Text(
                    text = "Concluir Cadastro da Família 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // OPÇÃO PARA QUEM JÁ TEM CONTA
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("already_registered_button")
            ) {
                Text(
                    text = "Já possui uma conta cadastrada? Fazer Login",
                    color = LinkTextoClicavel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
