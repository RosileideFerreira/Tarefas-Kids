package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    children: List<Child>,
    balances: Map<Long, BalanceSummary>,
    allCompletions: List<TaskCompletion>,
    allRedemptions: List<RewardRedemption>,
    selectedChildId: Long?,
    onSelectChildId: (Long?) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedSection by remember { mutableIntStateOf(0) } // 0: Tarefas, 1: Recompensas

    val currentChild = remember(children, selectedChildId) {
        if (selectedChildId != null) children.find { it.id == selectedChildId }
        else children.firstOrNull()
    }

    val childCompletions = remember(allCompletions, currentChild) {
        if (currentChild == null) allCompletions
        else allCompletions.filter { it.childId == currentChild.id }
    }

    val childRedemptions = remember(allRedemptions, currentChild) {
        if (currentChild == null) allRedemptions
        else allRedemptions.filter { it.childId == currentChild.id }
    }

    val balanceSummary = currentChild?.let { balances[it.id] }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📜 Histórico e Saldo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("history_back_button")) {
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
                .padding(innerPadding)
        ) { adaptive ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = adaptive.contentMaxWidth)
                ) {
                    if (children.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = adaptive.horizontalPadding, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(children, key = { it.id }) { child ->
                                val isSelected = currentChild?.id == child.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectChildId(child.id) },
                                    label = { Text("${child.avatarEmoji} ${child.name}") }
                                )
                            }
                        }
                    }

                    if (currentChild != null && balanceSummary != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = adaptive.horizontalPadding, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                HistoryMetric(
                                    label = "Ganho na semana",
                                    value = balanceSummary.formattedWeekly,
                                    color = BotaoPrimarioAzul
                                )
                                HistoryMetric(
                                    label = "Ganho no mês",
                                    value = balanceSummary.formattedMonthly,
                                    color = BotaoPrimarioAzul
                                )
                                HistoryMetric(
                                    label = "Disponível",
                                    value = balanceSummary.formattedAvailable,
                                    color = SucessoVerdeAgua
                                )
                            }
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedSection,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = selectedSection == 0,
                            onClick = { selectedSection = 0 },
                            text = { Text("Tarefas Realizadas (${childCompletions.size})") },
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                        )
                        Tab(
                            selected = selectedSection == 1,
                            onClick = { selectedSection = 1 },
                            text = { Text("Recompensas (${childRedemptions.size})") },
                            icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null) }
                        )
                    }

                    val columns = adaptive.twoOrOneColumns

                    if (selectedSection == 0) {
                        if (childCompletions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhuma tarefa registrada para esta criança.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val rows = remember(childCompletions, columns) { childCompletions.chunked(columns) }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = rows,
                                    key = { row -> row.joinToString("_") { "comp_${it.id}" } }
                                ) { rowComps ->
                                    ResponsiveGridRow(
                                        items = rowComps,
                                        columns = columns,
                                        horizontalSpacing = 12.dp
                                    ) { comp ->
                                        HistoryCompletionCard(comp = comp)
                                    }
                                }
                            }
                        }
                    } else {
                        if (childRedemptions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhuma troca por recompensa solicitada.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val rows = remember(childRedemptions, columns) { childRedemptions.chunked(columns) }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = rows,
                                    key = { row -> row.joinToString("_") { "redemption_${it.id}" } }
                                ) { rowReds ->
                                    ResponsiveGridRow(
                                        items = rowReds,
                                        columns = columns,
                                        horizontalSpacing = 12.dp
                                    ) { red ->
                                        HistoryRedemptionCard(red = red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCompletionCard(comp: TaskCompletion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "${comp.taskRoom} • ${comp.taskTitle}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = comp.formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = comp.formattedReward,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (comp.status == CompletionStatus.APPROVED) SucessoVerdeAgua else Color.Gray
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (comp.status) {
                        CompletionStatus.APPROVED -> SucessoVerdeContainer
                        CompletionStatus.REJECTED -> Color(0xFFFEE2E2)
                        else -> DestaqueAlertaContainer
                    }
                ) {
                    Text(
                        text = comp.status.fullLabel,
                        fontSize = 11.sp,
                        color = when (comp.status) {
                            CompletionStatus.APPROVED -> Color(0xFF065F46)
                            CompletionStatus.REJECTED -> Color(0xFF991B1B)
                            else -> Color(0xFF92400E)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRedemptionCard(red: RewardRedemption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "🎁 ${red.title}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = red.formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "- ${red.formattedCost}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DestaqueAlertaAmarelo
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (red.status) {
                        CompletionStatus.APPROVED -> SucessoVerdeContainer
                        CompletionStatus.REJECTED -> Color(0xFFFEE2E2)
                        else -> DestaqueAlertaContainer
                    }
                ) {
                    Text(
                        text = red.status.fullLabel,
                        fontSize = 11.sp,
                        color = when (red.status) {
                            CompletionStatus.APPROVED -> Color(0xFF065F46)
                            CompletionStatus.REJECTED -> Color(0xFF991B1B)
                            else -> Color(0xFF92400E)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
