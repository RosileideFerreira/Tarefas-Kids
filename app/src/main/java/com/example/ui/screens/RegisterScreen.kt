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
    var parentPin by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPinVisible by remember { mutableStateOf(false) }

    val addedChildren = remember { mutableStateListOf<Pair<String, String>>() }
    var currentChildName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("🧒") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
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
        AdaptiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) { adaptive ->
            val maxContentWidth = when (adaptive.widthClass) {
                ScreenWidthClass.Compact -> adaptive.maxWidth
                ScreenWidthClass.Medium -> 680.dp
                ScreenWidthClass.Expanded -> 1080.dp
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                        .padding(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                            Column(modifier = Modifier.weight(1f)) {
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

                    // Sections: 2 columns on Expanded screens, 1 column on Compact/Medium
                    if (adaptive.isExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            ParentAccountSectionCard(
                                parentName = parentName,
                                onParentNameChange = { parentName = it; errorMessage = null },
                                parentEmail = parentEmail,
                                onParentEmailChange = { parentEmail = it; errorMessage = null },
                                parentPassword = parentPassword,
                                onParentPasswordChange = { parentPassword = it; errorMessage = null },
                                isPasswordVisible = isPasswordVisible,
                                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                                parentPin = parentPin,
                                onParentPinChange = { input ->
                                    parentPin = input.filter { it.isDigit() }.take(4)
                                    errorMessage = null
                                },
                                isPinVisible = isPinVisible,
                                onTogglePinVisibility = { isPinVisible = !isPinVisible },
                                modifier = Modifier.weight(1f)
                            )

                            ChildrenRegistrationSectionCard(
                                currentChildName = currentChildName,
                                onChildNameChange = { currentChildName = it },
                                selectedAvatar = selectedAvatar,
                                onSelectAvatar = { selectedAvatar = it },
                                addedChildren = addedChildren,
                                onAddChild = {
                                    if (currentChildName.isNotBlank()) {
                                        addedChildren.add(Pair(currentChildName.trim(), selectedAvatar))
                                        currentChildName = ""
                                        focusManager.clearFocus()
                                    }
                                },
                                onRemoveChild = { index -> addedChildren.removeAt(index) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        ParentAccountSectionCard(
                            parentName = parentName,
                            onParentNameChange = { parentName = it; errorMessage = null },
                            parentEmail = parentEmail,
                            onParentEmailChange = { parentEmail = it; errorMessage = null },
                            parentPassword = parentPassword,
                            onParentPasswordChange = { parentPassword = it; errorMessage = null },
                            isPasswordVisible = isPasswordVisible,
                            onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                            parentPin = parentPin,
                            onParentPinChange = { input ->
                                parentPin = input.filter { it.isDigit() }.take(4)
                                errorMessage = null
                            },
                            isPinVisible = isPinVisible,
                            onTogglePinVisibility = { isPinVisible = !isPinVisible },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ChildrenRegistrationSectionCard(
                            currentChildName = currentChildName,
                            onChildNameChange = { currentChildName = it },
                            selectedAvatar = selectedAvatar,
                            onSelectAvatar = { selectedAvatar = it },
                            addedChildren = addedChildren,
                            onAddChild = {
                                if (currentChildName.isNotBlank()) {
                                    addedChildren.add(Pair(currentChildName.trim(), selectedAvatar))
                                    currentChildName = ""
                                    focusManager.clearFocus()
                                }
                            },
                            onRemoveChild = { index -> addedChildren.removeAt(index) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 560.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                if (parentPin.length != 4 || !parentPin.all { it.isDigit() }) {
                                    errorMessage = "Crie um PIN de exatamente 4 dígitos numéricos."
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
                                .defaultMinSize(minHeight = 54.dp)
                                .testTag("submit_registration_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
                        ) {
                            Text(
                                text = "Concluir Cadastro da Família 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        TextButton(
                            onClick = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("already_registered_button")
                        ) {
                            Text(
                                text = "Já possui uma conta cadastrada? Fazer Login",
                                color = LinkTextoClicavel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ParentAccountSectionCard(
    parentName: String,
    onParentNameChange: (String) -> Unit,
    parentEmail: String,
    onParentEmailChange: (String) -> Unit,
    parentPassword: String,
    onParentPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    parentPin: String,
    onParentPinChange: (String) -> Unit,
    isPinVisible: Boolean,
    onTogglePinVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                onValueChange = onParentNameChange,
                label = { Text("Nome do responsável ou família") },
                placeholder = { Text("Ex: Juliana Santos / Família Santos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            OutlinedTextField(
                value = parentEmail,
                onValueChange = onParentEmailChange,
                label = { Text("E-mail de login") },
                placeholder = { Text("seuemail@exemplo.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_email_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
            )

            OutlinedTextField(
                value = parentPassword,
                onValueChange = onParentPasswordChange,
                label = { Text("Senha (mínimo 6 caracteres)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_password_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Alternar visibilidade da senha"
                        )
                    }
                }
            )

            OutlinedTextField(
                value = parentPin,
                onValueChange = onParentPinChange,
                label = { Text("PIN de acesso dos pais (4 dígitos)") },
                placeholder = { Text("Ex: 4820") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_pin_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onTogglePinVisibility) {
                        Icon(
                            imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Alternar visibilidade do PIN"
                        )
                    }
                },
                supportingText = {
                    Text(
                        "Obrigatório: usado para proteger e entrar na Área dos Pais",
                        fontSize = 11.sp
                    )
                }
            )
        }
    }
}

@Composable
private fun ChildrenRegistrationSectionCard(
    currentChildName: String,
    onChildNameChange: (String) -> Unit,
    selectedAvatar: String,
    onSelectAvatar: (String) -> Unit,
    addedChildren: List<Pair<String, String>>,
    onAddChild: () -> Unit,
    onRemoveChild: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                            .size(48.dp)
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
                            .clickable { onSelectAvatar(avatar) }
                            .testTag("avatar_option_$avatar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatar, fontSize = 24.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = currentChildName,
                    onValueChange = onChildNameChange,
                    label = { Text("Nome da criança") },
                    placeholder = { Text("Ex: Arthur, Sofia...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("child_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAddChild() })
                )

                Button(
                    onClick = onAddChild,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 54.dp)
                        .testTag("add_child_item_button"),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar", maxLines = 1)
                }
            }

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
                                    modifier = Modifier.weight(1f),
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
                                IconButton(onClick = { onRemoveChild(index) }) {
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
}
