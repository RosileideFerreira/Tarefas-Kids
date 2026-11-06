package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BalanceSummary
import com.example.model.Child
import com.example.model.RewardRedemption
import com.example.model.TaskCompletion
import com.example.model.TaskItem
import com.example.ui.AppScreen
import com.example.ui.theme.*

val AVAILABLE_AVATARS = listOf(
    "🧒", "👧", "👦", "🧑", "👶", "🦊", "🐼", "🐸", "🦁", "🐰", "🦄", "🐯"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    children: List<Child>,
    balances: Map<Long, BalanceSummary>,
    pendingCompletions: List<TaskCompletion>,
    pendingRedemptions: List<RewardRedemption>,
    allCompletions: List<TaskCompletion> = emptyList(),
    allTasks: List<TaskItem> = emptyList(),
    familyName: String = "Área dos Pais",
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    onNavigate: (AppScreen) -> Unit,
    onBack: () -> Unit,
    onAddChild: (name: String, avatarEmoji: String) -> Unit,
    onDeleteChild: (Long) -> Unit,
    onOpenChildScreen: (Child) -> Unit,
    onReviewCompletion: (Long, Boolean) -> Unit,
    onReviewRedemption: (Long, Boolean) -> Unit,
    onLogout: () -> Unit = {}
) {
    BackHandler { onBack() }

    var showAddChildDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "👨‍👩‍👧‍👦 Área dos Pais",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (familyName.isNotBlank()) {
                            Text(
                                text = familyName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("parent_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar ao Início"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode, modifier = Modifier.testTag("parent_theme_toggle")) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Mudar cor da tela",
                            tint = if (isDarkMode) GamificacaoOuro else BotaoPrimarioAzul
                        )
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("parent_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Encerrar Sessão do Responsável"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddChildDialog = true },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                    text = { Text("Adicionar Criança") },
                    containerColor = BotaoPrimarioAzul,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_child_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Filhos (${children.size})") },
                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tarefas")
                            if (pendingCompletions.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge { Text("${pendingCompletions.size}") }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Trocas")
                            if (pendingRedemptions.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge { Text("${pendingRedemptions.size}") }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Calendário") },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                )
            }

            // Quick Banner to manage chore catalog
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Catálogo de Tarefas Domésticas",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Defina tarefas e escolha para qual criança atribuir",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { onNavigate(AppScreen.PARENT_TASKS_MANAGE) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                        modifier = Modifier.testTag("manage_tasks_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gerenciar", fontSize = 12.sp)
                    }
                }
            }

            when (selectedTab) {
                0 -> ChildrenTabContent(
                    children = children,
                    balances = balances,
                    onDeleteChild = onDeleteChild,
                    onOpenChildScreen = onOpenChildScreen,
                    onAddChildClick = { showAddChildDialog = true }
                )
                1 -> PendingCompletionsTabContent(
                    pendingCompletions = pendingCompletions,
                    children = children,
                    onReview = onReviewCompletion
                )
                2 -> PendingRedemptionsTabContent(
                    pendingRedemptions = pendingRedemptions,
                    onReview = onReviewRedemption
                )
                3 -> ParentCalendarTabContent(
                    children = children,
                    allCompletions = allCompletions,
                    allTasks = allTasks,
                    onNavigateToManageTasks = { onNavigate(AppScreen.PARENT_TASKS_MANAGE) }
                )
            }
        }
    }

    if (showAddChildDialog) {
        AddChildDialog(
            onDismiss = { showAddChildDialog = false },
            onConfirm = { name, avatar ->
                onAddChild(name, avatar)
                showAddChildDialog = false
            }
        )
    }
}

@Composable
private fun ChildrenTabContent(
    children: List<Child>,
    balances: Map<Long, BalanceSummary>,
    onDeleteChild: (Long) -> Unit,
    onOpenChildScreen: (Child) -> Unit,
    onAddChildClick: () -> Unit
) {
    if (children.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🧒", fontSize = 48.sp)
                Text(
                    text = "Nenhuma criança cadastrada",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Cadastre os pequenos para começar a definir as tarefas e recompensas!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onAddChildClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
                ) {
                    Text("Cadastrar Criança")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(children, key = { it.id }) { child ->
                val summary = balances[child.id]
                ChildBalanceCard(
                    child = child,
                    summary = summary,
                    onOpenChildScreen = { onOpenChildScreen(child) },
                    onDelete = { onDeleteChild(child.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun ChildBalanceCard(
    child: Child,
    summary: BalanceSummary?,
    onOpenChildScreen: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("child_card_${child.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BotaoPrimarioAzul.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = child.avatarEmoji, fontSize = 28.sp)
                    }
                    Column {
                        Text(
                            text = child.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if ((summary?.pendingTasksCount ?: 0) > 0) {
                            Text(
                                text = "${summary?.pendingTasksCount} tarefa(s) aguardando aprovação 🕐",
                                fontSize = 11.sp,
                                color = DestaqueAlertaAmarelo,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remover Criança",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Balance Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceMetric(
                    label = "Semana",
                    value = summary?.formattedWeekly ?: "R$ 0,00",
                    color = BotaoPrimarioAzul
                )
                BalanceMetric(
                    label = "Mês",
                    value = summary?.formattedMonthly ?: "R$ 0,00",
                    color = BotaoPrimarioAzul
                )
                BalanceMetric(
                    label = "Disponível",
                    value = summary?.formattedAvailable ?: "R$ 0,00",
                    color = SucessoVerdeAgua
                )
            }

            // BOTÃO: Acesso do Responsável à Tela desta Criança
            FilledTonalButton(
                onClick = onOpenChildScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_child_screen_${child.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = BotaoPrimarioAzul.copy(alpha = 0.12f),
                    contentColor = BotaoPrimarioAzul
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver Tela da Criança (${child.name})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Criança") },
            text = { Text("Tem certeza que deseja remover ${child.name}? O histórico desta criança não poderá ser recuperado.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BalanceMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun PendingCompletionsTabContent(
    pendingCompletions: List<TaskCompletion>,
    children: List<Child>,
    onReview: (Long, Boolean) -> Unit
) {
    if (pendingCompletions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🎉", fontSize = 48.sp)
                Text(
                    text = "Tudo em dia!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Não há tarefas realizadas aguardando aprovação no momento.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pendingCompletions, key = { it.id }) { comp ->
                val child = children.find { it.id == comp.childId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_task_${comp.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = child?.avatarEmoji ?: "🧒", fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = child?.name ?: "Criança",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = comp.taskRoom,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = comp.formattedReward,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SucessoVerdeAgua
                            )
                        }

                        Text(
                            text = comp.taskTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Realizada em: ${comp.formattedDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReview(comp.id, false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reject_completion_${comp.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Recusar ❌", fontSize = 13.sp)
                            }

                            Button(
                                onClick = { onReview(comp.id, true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("approve_completion_${comp.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SucessoVerdeAgua
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aprovar ✅", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingRedemptionsTabContent(
    pendingRedemptions: List<RewardRedemption>,
    onReview: (Long, Boolean) -> Unit
) {
    if (pendingRedemptions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🎁", fontSize = 48.sp)
                Text(
                    text = "Sem pedidos pendentes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Quando as crianças pedirem para trocar o saldo por recompensas, os pedidos aparecerão aqui.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pendingRedemptions, key = { it.id }) { red ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pending_redemption_${red.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Pedido por ${red.childName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "- ${red.formattedCost}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DestaqueAlertaAmarelo
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎁", fontSize = 24.sp)
                            Text(
                                text = red.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "Solicitado em: ${red.formattedDate}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReview(red.id, false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reject_redemption_${red.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Recusar ❌", fontSize = 13.sp)
                            }

                            Button(
                                onClick = { onReview(red.id, true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("approve_redemption_${red.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BotaoPrimarioAzul
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Aprovar Troca ✅", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddChildDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, avatarEmoji: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(AVAILABLE_AVATARS[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cadastrar Criança", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da criança") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("child_name_input")
                )

                Text(
                    text = "Escolha o avatar:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(AVAILABLE_AVATARS) { avatar ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedAvatar == avatar) BotaoPrimarioAzul.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedAvatar = avatar },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = avatar, fontSize = 24.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), selectedAvatar)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                modifier = Modifier.testTag("confirm_add_child_button")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
