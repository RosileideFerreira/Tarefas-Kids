package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

enum class KidTaskStatusFilter(val label: String) {
    ALL("Todas"),
    TO_DO("A Fazer 📋"),
    PENDING("Pendentes 🕐"),
    DONE("Concluídas ✅"),
    REJECTED("Recusadas ❌")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsDashboardScreen(
    child: Child,
    balance: BalanceSummary?,
    tasks: List<TaskItem>,
    completions: List<TaskCompletion>,
    redemptions: List<RewardRedemption>,
    onCompleteTask: (TaskItem) -> Unit,
    onUndoCompletion: (Long) -> Unit,
    onRequestRedemption: (title: String, costCents: Long) -> Boolean,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedRoom by remember { mutableStateOf<RoomCategory?>(null) }
    var selectedStatusFilter by remember { mutableStateOf(KidTaskStatusFilter.ALL) }
    var showRedeemDialog by remember { mutableStateOf(false) }

    // Obter o registro de conclusão mais recente para cada tarefa desta criança
    val latestCompletionByTaskId = remember(completions) {
        completions.groupBy { it.taskId }
            .mapValues { (_, comps) -> comps.maxByOrNull { it.completedAt } }
    }

    // Filtrar apenas tarefas atribuídas a esta criança ou a todas
    val childAssignedTasks = remember(tasks, child.id) {
        tasks.filter { it.isAssignedTo(child.id) }
    }

    // Contadores para os filtros de status
    val todoCount = remember(childAssignedTasks, latestCompletionByTaskId) {
        childAssignedTasks.count { latestCompletionByTaskId[it.id] == null }
    }
    val pendingCount = remember(childAssignedTasks, latestCompletionByTaskId) {
        childAssignedTasks.count { latestCompletionByTaskId[it.id]?.status == CompletionStatus.PENDING }
    }
    val doneCount = remember(childAssignedTasks, latestCompletionByTaskId) {
        childAssignedTasks.count { latestCompletionByTaskId[it.id]?.status == CompletionStatus.APPROVED }
    }
    val rejectedCount = remember(childAssignedTasks, latestCompletionByTaskId) {
        childAssignedTasks.count { latestCompletionByTaskId[it.id]?.status == CompletionStatus.REJECTED }
    }

    // Tarefas filtradas por cômodo e por status selecionado
    val filteredTasks = remember(childAssignedTasks, selectedRoom, selectedStatusFilter, latestCompletionByTaskId) {
        childAssignedTasks.filter { task ->
            val matchesRoom = selectedRoom == null || task.room == selectedRoom?.displayName
            if (!matchesRoom) return@filter false

            val latestComp = latestCompletionByTaskId[task.id]
            when (selectedStatusFilter) {
                KidTaskStatusFilter.ALL -> true
                KidTaskStatusFilter.TO_DO -> latestComp == null
                KidTaskStatusFilter.PENDING -> latestComp?.status == CompletionStatus.PENDING
                KidTaskStatusFilter.DONE -> latestComp?.status == CompletionStatus.APPROVED
                KidTaskStatusFilter.REJECTED -> latestComp?.status == CompletionStatus.REJECTED
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = child.avatarEmoji, fontSize = 24.sp)
                        Text(
                            text = "Tarefas de ${child.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("kids_dash_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Trocar Criança / Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Piggy Bank / Balance Card
            item {
                KidPiggyCard(
                    balance = balance,
                    onRedeemClick = { showRedeemDialog = true }
                )
            }

            // Filtro por Status (Feitas, Pendentes, Recusadas, A Fazer)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Filtrar por Status:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedStatusFilter == KidTaskStatusFilter.ALL,
                                onClick = { selectedStatusFilter = KidTaskStatusFilter.ALL },
                                label = { Text("Todas (${childAssignedTasks.size})") },
                                modifier = Modifier.testTag("status_filter_all")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedStatusFilter == KidTaskStatusFilter.TO_DO,
                                onClick = { selectedStatusFilter = KidTaskStatusFilter.TO_DO },
                                label = { Text("A Fazer 📋 ($todoCount)") },
                                modifier = Modifier.testTag("status_filter_todo")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedStatusFilter == KidTaskStatusFilter.PENDING,
                                onClick = { selectedStatusFilter = KidTaskStatusFilter.PENDING },
                                label = { Text("Pendentes 🕐 ($pendingCount)") },
                                modifier = Modifier.testTag("status_filter_pending")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedStatusFilter == KidTaskStatusFilter.DONE,
                                onClick = { selectedStatusFilter = KidTaskStatusFilter.DONE },
                                label = { Text("Concluídas ✅ ($doneCount)") },
                                modifier = Modifier.testTag("status_filter_done")
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedStatusFilter == KidTaskStatusFilter.REJECTED,
                                onClick = { selectedStatusFilter = KidTaskStatusFilter.REJECTED },
                                label = { Text("Recusadas ❌ ($rejectedCount)") },
                                modifier = Modifier.testTag("status_filter_rejected")
                            )
                        }
                    }
                }
            }

            // Filtro por Cômodo
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Filtrar por Cômodo:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedRoom == null,
                                onClick = { selectedRoom = null },
                                label = { Text("Todos os cômodos") },
                                modifier = Modifier.testTag("room_filter_all")
                            )
                        }
                        items(RoomCategory.values()) { room ->
                            FilterChip(
                                selected = selectedRoom == room,
                                onClick = {
                                    selectedRoom = if (selectedRoom == room) null else room
                                },
                                label = { Text(room.labelWithEmoji) }
                            )
                        }
                    }
                }
            }

            // Task list
            if (filteredTasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = when (selectedStatusFilter) {
                                        KidTaskStatusFilter.DONE -> "🎉"
                                        KidTaskStatusFilter.PENDING -> "🕐"
                                        KidTaskStatusFilter.REJECTED -> "✨"
                                        else -> "🧹"
                                    },
                                    fontSize = 40.sp
                                )
                                Text(
                                    text = when (selectedStatusFilter) {
                                        KidTaskStatusFilter.DONE -> "Nenhuma tarefa concluída neste filtro."
                                        KidTaskStatusFilter.PENDING -> "Nenhuma tarefa pendente de aprovação."
                                        KidTaskStatusFilter.REJECTED -> "Nenhuma tarefa recusada pelos pais. Muito bem!"
                                        KidTaskStatusFilter.TO_DO -> "Tudo pronto! Não há tarefas pendentes para fazer."
                                        else -> "Nenhuma tarefa disponível com os filtros atuais."
                                    },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredTasks, key = { "task_${it.id}" }) { task ->
                    val latestComp = latestCompletionByTaskId[task.id]
                    KidTaskCard(
                        task = task,
                        childId = child.id,
                        latestCompletion = latestComp,
                        onComplete = { onCompleteTask(task) },
                        onUndo = { compId -> onUndoCompletion(compId) }
                    )
                }
            }

            // Recent Redemptions / Requests Section
            if (redemptions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🎁 Minhas solicitações de recompensa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                items(redemptions.take(5), key = { "redemption_${it.id}" }) { red ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = red.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Valor: ${red.formattedCost} • ${red.formattedDate}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (red.status) {
                                    CompletionStatus.APPROVED -> SucessoVerdeContainer
                                    CompletionStatus.REJECTED -> Color(0xFFFEE2E2)
                                    else -> DestaqueAlertaContainer
                                }
                            ) {
                                Text(
                                    text = red.status.fullLabel,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when (red.status) {
                                        CompletionStatus.APPROVED -> Color(0xFF065F46)
                                        CompletionStatus.REJECTED -> Color(0xFF991B1B)
                                        else -> Color(0xFF92400E)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showRedeemDialog) {
        RedemptionRequestDialog(
            availableBalanceCents = balance?.availableBalanceCents ?: 0L,
            formattedAvailable = balance?.formattedAvailable ?: "R$ 0,00",
            onDismiss = { showRedeemDialog = false },
            onConfirm = { title, cents ->
                val success = onRequestRedemption(title, cents)
                if (success) {
                    showRedeemDialog = false
                }
            }
        )
    }
}

@Composable
private fun KidPiggyCard(
    balance: BalanceSummary?,
    onRedeemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kid_piggy_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(SucessoVerdeAgua, Color(0xFF0D9488))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🪙 Meu Cofrinho",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = balance?.formattedAvailable ?: "R$ 0,00",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Disponível para gastar",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onRedeemClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GamificacaoOuro,
                            contentColor = Color(0xFF78350F)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("redeem_reward_button"),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trocar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.25f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Ganho na Semana",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = balance?.formattedWeekly ?: "R$ 0,00",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Ganho no Mês",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = balance?.formattedMonthly ?: "R$ 0,00",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KidTaskCard(
    task: TaskItem,
    childId: Long,
    latestCompletion: TaskCompletion?,
    onComplete: () -> Unit,
    onUndo: (Long) -> Unit
) {
    val currentStatus = latestCompletion?.status

    // Estilo de borda de acordo com o status
    val borderColor = when (currentStatus) {
        CompletionStatus.APPROVED -> SucessoVerdeAgua.copy(alpha = 0.5f)
        CompletionStatus.PENDING -> DestaqueAlertaAmarelo.copy(alpha = 0.6f)
        CompletionStatus.REJECTED -> Color(0xFFEF4444).copy(alpha = 0.5f)
        null -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(18.dp))
                } else Modifier
            )
            .testTag("kid_task_card_${task.id}"),
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
            // Linha Superior: Ícone, Título, Cômodo e Recompensa
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BotaoPrimarioAzul.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = task.roomCategory.emoji, fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = task.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = task.room,
                                fontSize = 12.sp,
                                color = LinkTextoClicavel,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SucessoVerdeContainer
                ) {
                    Text(
                        text = "+ ${task.formattedReward}",
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (!task.description.isNullOrBlank()) {
                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Exibição explícita dos status: Concluído, Pendente, Recusado ou A Fazer
            when (currentStatus) {
                CompletionStatus.APPROVED -> {
                    // STATUS: CONCLUÍDO (Aprovado)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SucessoVerdeContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF065F46),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Status: Concluído ✅",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF065F46)
                                )
                            }
                            Text(
                                text = "Parabéns! Tarefa aprovada pelos pais e recompensa creditada no cofrinho.",
                                fontSize = 11.sp,
                                color = Color(0xFF065F46).copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                CompletionStatus.PENDING -> {
                    // STATUS: PENDENTE (Aguardando aprovação)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DestaqueAlertaContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color(0xFF92400E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Status: Pendente 🕐",
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                TextButton(
                                    onClick = { onUndo(latestCompletion.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("undo_task_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Undo,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Desfazer",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Você marcou que fez! Aguardando o responsável analisar e aprovar.",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E).copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                CompletionStatus.REJECTED -> {
                    // STATUS: RECUSADO (Recusado pelos pais)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEE2E2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = Color(0xFF991B1B),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Status: Recusado ❌",
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "Os pais não aprovaram esta tarefa. Corrija o que faltou organizar e envie novamente!",
                                fontSize = 11.sp,
                                color = Color(0xFF991B1B).copy(alpha = 0.9f)
                            )

                            Button(
                                onClick = onComplete,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("retry_task_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Fazer novamente e reenviar 🔄",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                null -> {
                    // STATUS: A FAZER (Ainda não realizada)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BotaoPrimarioAzul.copy(alpha = 0.1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = BotaoPrimarioAzul,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Status: A Fazer 📋",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BotaoPrimarioAzul
                                )
                            }
                        }

                        Button(
                            onClick = onComplete,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("complete_task_${task.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BotaoPrimarioAzul
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Fiz essa! 🎉",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RedemptionRequestDialog(
    availableBalanceCents: Long,
    formattedAvailable: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, costCents: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🎁", fontSize = 24.sp)
                Text(text = "Trocar por Recompensa", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SucessoVerdeContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Saldo disponível no cofrinho: $formattedAvailable",
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("O que você gostaria de pedir?") },
                    placeholder = { Text("Ex: Sorvete, gibi, tempo extra...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("redeem_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                    label = { Text("Quanto do seu saldo usar? (R$)") },
                    placeholder = { Text("Ex: 5,00") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("redeem_cost_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
                    if (title.isBlank()) {
                        errorMessage = "Escreva o que você gostaria de pedir."
                        return@Button
                    }
                    val normalized = costText.replace(",", ".")
                    val costDouble = normalized.toDoubleOrNull() ?: 0.0
                    val costCents = (costDouble * 100).toLong()

                    if (costCents <= 0) {
                        errorMessage = "Digite um valor maior que zero."
                        return@Button
                    }
                    if (costCents > availableBalanceCents) {
                        errorMessage = "Você não tem saldo suficiente. Seu saldo é $formattedAvailable."
                        return@Button
                    }

                    errorMessage = null
                    onConfirm(title.trim(), costCents)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                modifier = Modifier.testTag("redeem_dialog_confirm")
            ) {
                Text("Pedir aos Pais")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
