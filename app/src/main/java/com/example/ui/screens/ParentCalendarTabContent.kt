package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Child
import com.example.model.CompletionStatus
import com.example.model.TaskCompletion
import com.example.model.TaskItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

data class WeekCalendarData(
    val weekNumber: Int,
    val label: String,
    val rangeText: String,
    val startMillis: Long,
    val endMillis: Long,
    val approvedTasks: List<TaskCompletion>,
    val pendingTasks: List<TaskCompletion>,
    val rejectedTasks: List<TaskCompletion>,
    val uncompletedTasks: List<TaskItem>,
    val weekEarningsCents: Long,
    val isCurrentWeek: Boolean
)

data class MonthCalendarData(
    val monthName: String,
    val year: Int,
    val totalEarningsCents: Long,
    val totalCompletedTasks: Int,
    val totalUncompletedTasks: Int,
    val weeks: List<WeekCalendarData>
)

@Composable
fun ParentCalendarTabContent(
    children: List<Child>,
    allCompletions: List<TaskCompletion>,
    allTasks: List<TaskItem>,
    onNavigateToManageTasks: () -> Unit = {}
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
                Text(text = "📅", fontSize = 48.sp)
                Text(
                    text = "Nenhuma criança cadastrada",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Cadastre as crianças na aba Filhos para acompanhar o calendário semanal e mensal de atividades e saldos.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    var selectedChildId by remember(children) {
        mutableStateOf(children.first().id)
    }

    val selectedChild = remember(children, selectedChildId) {
        children.find { it.id == selectedChildId } ?: children.first()
    }

    // Mês e Ano selecionados no calendário
    var currentCalendarMonth by remember {
        mutableStateOf(Calendar.getInstance())
    }

    // Cálculo das semanas e totais do mês selecionado
    val monthData = remember(selectedChild.id, currentCalendarMonth.timeInMillis, allCompletions, allTasks) {
        calculateMonthData(
            calendar = currentCalendarMonth,
            childId = selectedChild.id,
            allCompletions = allCompletions,
            allTasks = allTasks
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("parent_calendar_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. SELETOR DE CRIANÇAS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Selecione o filho para ver o calendário:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(children, key = { it.id }) { child ->
                        val isSelected = child.id == selectedChild.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedChildId = child.id },
                            leadingIcon = {
                                Text(text = child.avatarEmoji, fontSize = 16.sp)
                            },
                            label = {
                                Text(
                                    text = child.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("calendar_child_chip_${child.id}")
                        )
                    }
                }
            }
        }

        // 2. NAVEGAÇÃO ENTRE MESES
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val newCal = currentCalendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            currentCalendarMonth = newCal
                        },
                        modifier = Modifier.testTag("calendar_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Mês Anterior"
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${monthData.monthName} de ${monthData.year}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Acompanhamento semanal e fechamento do mês",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            val newCal = currentCalendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            currentCalendarMonth = newCal
                        },
                        modifier = Modifier.testTag("calendar_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Próximo Mês"
                        )
                    }
                }
            }
        }

        // 3. CARD RESUMO MENSAL CONSOLIDADO (SOMA DE TODAS AS SEMANAS)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_month_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(BotaoPrimarioAzul, Color(0xFF1E40AF))
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = selectedChild.avatarEmoji, fontSize = 20.sp)
                                    Text(
                                        text = "Saldo Total do Mês (${selectedChild.name})",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = formatCentsToCurrency(monthData.totalEarningsCents),
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Soma de todas as semanas de ${monthData.monthName}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "💰", fontSize = 26.sp)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.25f))

                        // Linhas de soma por semana no resumo
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            monthData.weeks.forEach { week ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${week.label} (${week.rangeText})",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = formatCentsToCurrency(week.weekEarningsCents),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. DETALHAMENTO DE CADA SEMANA
        item {
            Text(
                text = "Detalhamento por Semana:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(monthData.weeks, key = { it.weekNumber }) { week ->
            WeekDetailCard(
                week = week,
                childName = selectedChild.name,
                onNavigateToManageTasks = onNavigateToManageTasks
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WeekDetailCard(
    week: WeekCalendarData,
    childName: String,
    onNavigateToManageTasks: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("week_card_${week.weekNumber}"),
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
            // Cabeçalho da Semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = week.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (week.isCurrentWeek) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BotaoPrimarioAzul.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Semana Atual 📍",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BotaoPrimarioAzul,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = week.rangeText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            // Banner do Saldo Ganho na Semana
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SucessoVerdeContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color(0xFF065F46),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Saldo Ganho na Semana:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color(0xFF065F46)
                        )
                    }
                    Text(
                        text = formatCentsToCurrency(week.weekEarningsCents),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color(0xFF065F46)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // SEÇÃO 1: O QUE A CRIANÇA FEZ NESTA SEMANA
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SucessoVerdeAgua,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Atividades que $childName FEZ (${week.approvedTasks.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (week.approvedTasks.isEmpty() && week.pendingTasks.isEmpty()) {
                    Text(
                        text = "Nenhuma atividade realizada nesta semana.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 22.dp)
                    )
                } else {
                    // Tarefas Aprovadas
                    week.approvedTasks.forEach { comp ->
                        val dateFormatted = SimpleDateFormat("dd/MM 'às' HH:mm", Locale("pt", "BR"))
                            .format(comp.completedAt)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "✅ ${comp.taskTitle}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${comp.taskRoom} • $dateFormatted",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "+ ${formatCentsToCurrency(comp.rewardCents)}",
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Tarefas que ainda estão aguardando aprovação
                    week.pendingTasks.forEach { comp ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DestaqueAlertaContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🕐 ${comp.taskTitle}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "${comp.taskRoom} • Aguardando aprovação dos pais",
                                        fontSize = 11.sp,
                                        color = Color(0xFF92400E).copy(alpha = 0.85f)
                                    )
                                }
                                Text(
                                    text = formatCentsToCurrency(comp.rewardCents),
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // SEÇÃO 2: O QUE A CRIANÇA DEIXOU DE FAZER NESTA SEMANA
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Atividades que DEIXOU DE FAZER (${week.uncompletedTasks.size + week.rejectedTasks.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (week.uncompletedTasks.isEmpty() && week.rejectedTasks.isEmpty()) {
                    Text(
                        text = if (week.approvedTasks.isNotEmpty()) {
                            "🎉 Todas as tarefas previstas para esta semana foram realizadas!"
                        } else {
                            "Nenhuma tarefa pendente ou cadastrada para esta semana."
                        },
                        fontSize = 12.sp,
                        color = if (week.approvedTasks.isNotEmpty()) SucessoVerdeAgua else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 22.dp)
                    )
                } else {
                    // Tarefas Recusadas pelos pais
                    week.rejectedTasks.forEach { comp ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "❌ ${comp.taskTitle}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF991B1B)
                                    )
                                    Text(
                                        text = "${comp.taskRoom} • Recusada pelos pais",
                                        fontSize = 11.sp,
                                        color = Color(0xFF991B1B).copy(alpha = 0.85f)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "Recusada",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF991B1B),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Tarefas da rotina não feitas
                    week.uncompletedTasks.forEach { task ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "⚠️ ${task.title}",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${task.room} • Não realizada nesta semana",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "(${task.formattedReward})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onNavigateToManageTasks,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_task_week_${week.weekNumber}"),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = BotaoPrimarioAzul
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Cadastrar tarefa para a ${week.label}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BotaoPrimarioAzul
                )
            }
        }
    }
}

private fun isTaskForWeek(task: TaskItem, weekNumber: Int): Boolean {
    if (task.targetWeek != null && task.targetWeek == -1) {
        return true
    }
    if (task.targetWeek != null && task.targetWeek in 1..5) {
        return task.targetWeek == weekNumber
    }
    return task.effectiveWeek == weekNumber
}

/**
 * Divide o mês em semanas e calcula tarefas feitas, pendentes, recusadas, não feitas e saldos.
 */
private fun calculateMonthData(
    calendar: Calendar,
    childId: Long,
    allCompletions: List<TaskCompletion>,
    allTasks: List<TaskItem>
): MonthCalendarData {
    val cal = (calendar.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val rawMonthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR")) ?: ""
    val monthName = rawMonthName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }
    val year = cal.get(Calendar.YEAR)
    val now = System.currentTimeMillis()

    // Intervalos das semanas do mês:
    // Semana 1: 1 a 7
    // Semana 2: 8 a 14
    // Semana 3: 15 a 21
    // Semana 4: 22 a 28
    // Semana 5: 29 a maxDays (se houver)
    val weekIntervals = mutableListOf<Pair<Int, Int>>()
    weekIntervals.add(Pair(1, min(7, maxDaysInMonth)))
    if (maxDaysInMonth >= 8) weekIntervals.add(Pair(8, min(14, maxDaysInMonth)))
    if (maxDaysInMonth >= 15) weekIntervals.add(Pair(15, min(21, maxDaysInMonth)))
    if (maxDaysInMonth >= 22) weekIntervals.add(Pair(22, min(28, maxDaysInMonth)))
    if (maxDaysInMonth >= 29) weekIntervals.add(Pair(29, maxDaysInMonth))

    val applicableTasksForChild = allTasks.filter { it.isAssignedTo(childId) }

    val weeksData = weekIntervals.mapIndexed { index, (startDay, endDay) ->
        val startCal = (cal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, startDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = (cal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, endDay)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val startMillis = startCal.timeInMillis
        val endMillis = endCal.timeInMillis
        val isCurrentWeek = now in startMillis..endMillis

        // Completions da criança dentro do período desta semana
        val weekCompletions = allCompletions.filter {
            it.childId == childId && it.completedAt in startMillis..endMillis
        }

        val approved = weekCompletions.filter { it.status == CompletionStatus.APPROVED }
        val pending = weekCompletions.filter { it.status == CompletionStatus.PENDING }
        val rejected = weekCompletions.filter { it.status == CompletionStatus.REJECTED }
        val weekEarningsCents = approved.sumOf { it.rewardCents }

        // Tarefas que pertencem especificamente a ESTA semana
        val weekTasksForChild = applicableTasksForChild.filter { task ->
            isTaskForWeek(task, index + 1)
        }

        // Tarefas que a criança deixou de fazer nesta semana (apenas as que pertenciam a esta semana e não foram feitas)
        val doneTaskIds = (approved.map { it.taskId } + pending.map { it.taskId }).toSet()
        val uncompleted = weekTasksForChild.filter { task ->
            task.id !in doneTaskIds
        }

        val startDayStr = String.format("%02d", startDay)
        val endDayStr = String.format("%02d", endDay)

        WeekCalendarData(
            weekNumber = index + 1,
            label = "Semana ${index + 1}",
            rangeText = "$startDayStr a $endDayStr de $monthName",
            startMillis = startMillis,
            endMillis = endMillis,
            approvedTasks = approved,
            pendingTasks = pending,
            rejectedTasks = rejected,
            uncompletedTasks = uncompleted,
            weekEarningsCents = weekEarningsCents,
            isCurrentWeek = isCurrentWeek
        )
    }

    val totalEarningsCents = weeksData.sumOf { it.weekEarningsCents }
    val totalCompletedTasks = weeksData.sumOf { it.approvedTasks.size }
    val totalUncompletedTasks = weeksData.sumOf { it.uncompletedTasks.size + it.rejectedTasks.size }

    return MonthCalendarData(
        monthName = monthName,
        year = year,
        totalEarningsCents = totalEarningsCents,
        totalCompletedTasks = totalCompletedTasks,
        totalUncompletedTasks = totalUncompletedTasks,
        weeks = weeksData
    )
}

private fun formatCentsToCurrency(cents: Long): String {
    val reais = cents / 100.0
    return String.format(Locale("pt", "BR"), "R$ %.2f", reais)
}
