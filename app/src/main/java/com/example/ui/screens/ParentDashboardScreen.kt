package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
    onChangePin: (currentPin: String, newPin: String) -> Boolean = { _, _ -> false },
    onLogout: () -> Unit = {}
) {
    BackHandler { onBack() }

    var showAddChildDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "👨‍👩‍👧‍👦 Área dos Pais",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (familyName.isNotBlank()) {
                            Text(
                                text = familyName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
                    IconButton(
                        onClick = { showChangePinDialog = true },
                        modifier = Modifier.testTag("parent_change_pin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pin,
                            contentDescription = "Alterar PIN de acesso",
                            tint = BotaoPrimarioAzul
                        )
                    }
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
        AdaptiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { adaptive ->
            if (adaptive.isExpanded) {
                // Canonical NavigationRail layout for Tablets / ChromeOS / Large Foldables
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        NavigationRailItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.People, contentDescription = "Filhos") },
                            label = { Text("Filhos (${children.size})") }
                        )
                        NavigationRailItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (pendingCompletions.isNotEmpty()) {
                                            Badge { Text("${pendingCompletions.size}") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Tarefas")
                                }
                            },
                            label = { Text("Tarefas") }
                        )
                        NavigationRailItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (pendingRedemptions.isNotEmpty()) {
                                            Badge { Text("${pendingRedemptions.size}") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = "Trocas")
                                }
                            },
                            label = { Text("Trocas") }
                        )
                        NavigationRailItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendário") },
                            label = { Text("Calendário") }
                        )
                    }

                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        ChoreCatalogQuickBanner(
                            onNavigateToTasks = { onNavigate(AppScreen.PARENT_TASKS_MANAGE) },
                            adaptive = adaptive
                        )
                        ParentTabBody(
                            selectedTab = selectedTab,
                            adaptive = adaptive,
                            children = children,
                            balances = balances,
                            pendingCompletions = pendingCompletions,
                            pendingRedemptions = pendingRedemptions,
                            allCompletions = allCompletions,
                            allTasks = allTasks,
                            onDeleteChild = onDeleteChild,
                            onOpenChildScreen = onOpenChildScreen,
                            onAddChildClick = { showAddChildDialog = true },
                            onReviewCompletion = onReviewCompletion,
                            onReviewRedemption = onReviewRedemption,
                            onNavigateToTasks = { onNavigate(AppScreen.PARENT_TASKS_MANAGE) }
                        )
                    }
                }
            } else {
                // Compact & Medium TabRow layout
                Column(modifier = Modifier.fillMaxSize()) {
                    if (adaptive.maxWidth < 380.dp) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.background,
                            edgePadding = 8.dp
                        ) {
                            ParentTabsContent(
                                selectedTab = selectedTab,
                                childrenCount = children.size,
                                pendingCompletionsCount = pendingCompletions.size,
                                pendingRedemptionsCount = pendingRedemptions.size,
                                onSelectTab = { selectedTab = it }
                            )
                        }
                    } else {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.background
                        ) {
                            ParentTabsContent(
                                selectedTab = selectedTab,
                                childrenCount = children.size,
                                pendingCompletionsCount = pendingCompletions.size,
                                pendingRedemptionsCount = pendingRedemptions.size,
                                onSelectTab = { selectedTab = it }
                            )
                        }
                    }

                    ChoreCatalogQuickBanner(
                        onNavigateToTasks = { onNavigate(AppScreen.PARENT_TASKS_MANAGE) },
                        adaptive = adaptive
                    )

                    ParentTabBody(
                        selectedTab = selectedTab,
                        adaptive = adaptive,
                        children = children,
                        balances = balances,
                        pendingCompletions = pendingCompletions,
                        pendingRedemptions = pendingRedemptions,
                        allCompletions = allCompletions,
                        allTasks = allTasks,
                        onDeleteChild = onDeleteChild,
                        onOpenChildScreen = onOpenChildScreen,
                        onAddChildClick = { showAddChildDialog = true },
                        onReviewCompletion = onReviewCompletion,
                        onReviewRedemption = onReviewRedemption,
                        onNavigateToTasks = { onNavigate(AppScreen.PARENT_TASKS_MANAGE) }
                    )
                }
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

    if (showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { showChangePinDialog = false },
            onConfirm = { currentPin, newPin ->
                if (onChangePin(currentPin, newPin)) {
                    showChangePinDialog = false
                }
            }
        )
    }
}

@Composable
private fun ParentTabsContent(
    selectedTab: Int,
    childrenCount: Int,
    pendingCompletionsCount: Int,
    pendingRedemptionsCount: Int,
    onSelectTab: (Int) -> Unit
) {
    Tab(
        selected = selectedTab == 0,
        onClick = { onSelectTab(0) },
        text = { Text("Filhos ($childrenCount)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = { Icon(Icons.Default.People, contentDescription = null) }
    )
    Tab(
        selected = selectedTab == 1,
        onClick = { onSelectTab(1) },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tarefas", maxLines = 1)
                if (pendingCompletionsCount > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Badge { Text("$pendingCompletionsCount") }
                }
            }
        },
        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
    )
    Tab(
        selected = selectedTab == 2,
        onClick = { onSelectTab(2) },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Trocas", maxLines = 1)
                if (pendingRedemptionsCount > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Badge { Text("$pendingRedemptionsCount") }
                }
            }
        },
        icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null) }
    )
    Tab(
        selected = selectedTab == 3,
        onClick = { onSelectTab(3) },
        text = { Text("Calendário", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
    )
}

@Composable
private fun ChoreCatalogQuickBanner(
    onNavigateToTasks: () -> Unit,
    adaptive: AdaptiveLayoutInfo
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = adaptive.contentMaxWidth)
                    .padding(horizontal = adaptive.horizontalPadding, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
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
                    onClick = onNavigateToTasks,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("manage_tasks_button"),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gerenciar", fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun ParentTabBody(
    selectedTab: Int,
    adaptive: AdaptiveLayoutInfo,
    children: List<Child>,
    balances: Map<Long, BalanceSummary>,
    pendingCompletions: List<TaskCompletion>,
    pendingRedemptions: List<RewardRedemption>,
    allCompletions: List<TaskCompletion>,
    allTasks: List<TaskItem>,
    onDeleteChild: (Long) -> Unit,
    onOpenChildScreen: (Child) -> Unit,
    onAddChildClick: () -> Unit,
    onReviewCompletion: (Long, Boolean) -> Unit,
    onReviewRedemption: (Long, Boolean) -> Unit,
    onNavigateToTasks: () -> Unit
) {
    when (selectedTab) {
        0 -> ChildrenTabContent(
            children = children,
            balances = balances,
            adaptive = adaptive,
            onDeleteChild = onDeleteChild,
            onOpenChildScreen = onOpenChildScreen,
            onAddChildClick = onAddChildClick
        )
        1 -> PendingCompletionsTabContent(
            pendingCompletions = pendingCompletions,
            children = children,
            adaptive = adaptive,
            onReview = onReviewCompletion
        )
        2 -> PendingRedemptionsTabContent(
            pendingRedemptions = pendingRedemptions,
            adaptive = adaptive,
            onReview = onReviewRedemption
        )
        3 -> ParentCalendarTabContent(
            children = children,
            allCompletions = allCompletions,
            allTasks = allTasks,
            onNavigateToManageTasks = onNavigateToTasks
        )
    }
}

@Composable
private fun ChildrenTabContent(
    children: List<Child>,
    balances: Map<Long, BalanceSummary>,
    adaptive: AdaptiveLayoutInfo,
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
                modifier = Modifier.widthIn(max = 420.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🧒", fontSize = 48.sp)
                Text(
                    text = "Nenhuma criança cadastrada",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Cadastre os pequenos para começar a definir as tarefas e recompensas!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onAddChildClick,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul)
                ) {
                    Text("Cadastrar Criança")
                }
            }
        }
    } else {
        val columns = adaptive.twoOrOneColumns
        val rows = remember(children, columns) { children.chunked(columns) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = adaptive.contentMaxWidth),
                contentPadding = PaddingValues(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = rows,
                    key = { row -> row.joinToString("_") { it.id.toString() } }
                ) { rowChildren ->
                    ResponsiveGridRow(
                        items = rowChildren,
                        columns = columns,
                        horizontalSpacing = 16.dp
                    ) { child ->
                        ChildBalanceCard(
                            child = child,
                            summary = balances[child.id],
                            onOpenChildScreen = { onOpenChildScreen(child) },
                            onDelete = { onDeleteChild(child.id) }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
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
                    modifier = Modifier.weight(1f),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = child.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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

            FilledTonalButton(
                onClick = onOpenChildScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
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
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
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
    adaptive: AdaptiveLayoutInfo,
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
                modifier = Modifier.widthIn(max = 420.dp),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        val columns = adaptive.twoOrOneColumns
        val rows = remember(pendingCompletions, columns) { pendingCompletions.chunked(columns) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = adaptive.contentMaxWidth),
                contentPadding = PaddingValues(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = rows,
                    key = { row -> row.joinToString("_") { it.id.toString() } }
                ) { rowComps ->
                    ResponsiveGridRow(
                        items = rowComps,
                        columns = columns,
                        horizontalSpacing = 12.dp
                    ) { comp ->
                        val child = children.find { it.id == comp.childId }
                        PendingCompletionCard(
                            comp = comp,
                            child = child,
                            onReview = onReview
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingCompletionCard(
    comp: TaskCompletion,
    child: Child?,
    onReview: (Long, Boolean) -> Unit
) {
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = child?.avatarEmoji ?: "🧒", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
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
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("reject_completion_${comp.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Recusar ❌", fontSize = 13.sp, maxLines = 1)
                }

                Button(
                    onClick = { onReview(comp.id, true) },
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("approve_completion_${comp.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SucessoVerdeAgua
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aprovar ✅", fontSize = 13.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun PendingRedemptionsTabContent(
    pendingRedemptions: List<RewardRedemption>,
    adaptive: AdaptiveLayoutInfo,
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
                modifier = Modifier.widthIn(max = 420.dp),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        val columns = adaptive.twoOrOneColumns
        val rows = remember(pendingRedemptions, columns) { pendingRedemptions.chunked(columns) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = adaptive.contentMaxWidth),
                contentPadding = PaddingValues(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = rows,
                    key = { row -> row.joinToString("_") { it.id.toString() } }
                ) { rowReds ->
                    ResponsiveGridRow(
                        items = rowReds,
                        columns = columns,
                        horizontalSpacing = 12.dp
                    ) { red ->
                        PendingRedemptionCard(red = red, onReview = onReview)
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingRedemptionCard(
    red: RewardRedemption,
    onReview: (Long, Boolean) -> Unit
) {
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
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
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
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("reject_redemption_${red.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Recusar ❌", fontSize = 13.sp, maxLines = 1)
                }

                Button(
                    onClick = { onReview(red.id, true) },
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("approve_redemption_${red.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BotaoPrimarioAzul
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aprovar Troca ✅", fontSize = 13.sp, maxLines = 1)
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
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .widthIn(max = 460.dp)
            .imePadding(),
        title = {
            Text("Cadastrar Criança", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
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
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("confirm_add_child_button")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ChangePinDialog(
    onDismiss: () -> Unit,
    onConfirm: (currentPin: String, newPin: String) -> Unit
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    imageVector = Icons.Default.Pin,
                    contentDescription = null,
                    tint = BotaoPrimarioAzul
                )
                Text("Alterar PIN dos Pais", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = {
                        currentPin = it.filter { c -> c.isDigit() }.take(4)
                        errorMessage = null
                    },
                    label = { Text("PIN atual (4 dígitos)") },
                    singleLine = true,
                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_pin_current_input")
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = {
                        newPin = it.filter { c -> c.isDigit() }.take(4)
                        errorMessage = null
                    },
                    label = { Text("Novo PIN (4 dígitos)") },
                    singleLine = true,
                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { isPinVisible = !isPinVisible }) {
                            Icon(
                                imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Alternar visibilidade"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_pin_new_input")
                )

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        confirmPin = it.filter { c -> c.isDigit() }.take(4)
                        errorMessage = null
                    },
                    label = { Text("Confirmar novo PIN") },
                    singleLine = true,
                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_pin_confirm_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPin.length != 4 -> errorMessage = "Informe o PIN atual de 4 dígitos."
                        newPin.length != 4 -> errorMessage = "O novo PIN deve ter 4 dígitos."
                        newPin != confirmPin -> errorMessage = "A confirmação do novo PIN não confere."
                        else -> onConfirm(currentPin, newPin)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("change_pin_save_button")
            ) {
                Text("Salvar PIN")
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
