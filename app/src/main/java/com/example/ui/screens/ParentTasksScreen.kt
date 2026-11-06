package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Child
import com.example.model.RoomCategory
import com.example.model.TaskItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentTasksScreen(
    tasks: List<TaskItem>,
    children: List<Child>,
    onBack: () -> Unit,
    onAddTask: (title: String, room: String, rewardCents: Long, description: String, assignedChildId: Long?, assignedChildName: String?, targetWeek: Int?) -> Unit,
    onUpdateTask: (TaskItem) -> Unit,
    onDeleteTask: (Long) -> Unit
) {
    BackHandler { onBack() }

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskItem?>(null) }
    var selectedRoomFilter by remember { mutableStateOf<RoomCategory?>(null) }

    val filteredTasks = remember(tasks, selectedRoomFilter) {
        if (selectedRoomFilter == null) tasks
        else tasks.filter { it.room == selectedRoomFilter?.displayName }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Gerenciar Tarefas", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tasks_back_button")) {
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova Tarefa") },
                containerColor = BotaoPrimarioAzul,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_task_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Room Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedRoomFilter == null,
                        onClick = { selectedRoomFilter = null },
                        label = { Text("Todos (${tasks.size})") }
                    )
                }
                items(RoomCategory.values()) { room ->
                    FilterChip(
                        selected = selectedRoomFilter == room,
                        onClick = {
                            selectedRoomFilter = if (selectedRoomFilter == room) null else room
                        },
                        label = { Text(room.labelWithEmoji) }
                    )
                }
            }

            if (filteredTasks.isEmpty()) {
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
                        Text(text = "🧹", fontSize = 48.sp)
                        Text(
                            text = "Nenhuma tarefa encontrada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Crie uma nova tarefa e escolha se ela é para todos ou para uma criança específica.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemManageCard(
                            task = task,
                            onEdit = { taskToEdit = task },
                            onDelete = { onDeleteTask(task.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TaskFormDialog(
            task = null,
            children = children,
            onDismiss = { showAddDialog = false },
            onSave = { title, room, cents, desc, childId, childName, targetWeek ->
                onAddTask(title, room, cents, desc, childId, childName, targetWeek)
                showAddDialog = false
            }
        )
    }

    taskToEdit?.let { task ->
        TaskFormDialog(
            task = task,
            children = children,
            onDismiss = { taskToEdit = null },
            onSave = { title, room, cents, desc, childId, childName, targetWeek ->
                task.title = title
                task.room = room
                task.rewardCents = cents
                task.description = desc
                task.assignedChildId = childId
                task.assignedChildName = childName
                task.targetWeek = targetWeek
                onUpdateTask(task)
                taskToEdit = null
            }
        )
    }
}

@Composable
private fun TaskItemManageCard(
    task: TaskItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("manage_task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.roomCategory.emoji,
                        fontSize = 18.sp
                    )
                    Text(
                        text = task.room,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LinkTextoClicavel
                    )

                    // Target child pill
                    val isForAll = task.assignedChildId == null || task.assignedChildId == 0L
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isForAll) GamificacaoRoxo.copy(alpha = 0.15f) else BotaoPrimarioAzul.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (isForAll) "🌟 Todas as crianças" else "🧒 ${task.assignedChildName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isForAll) Color(0xFF7E22CE) else BotaoPrimarioAzul,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    // Week badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SucessoVerdeContainer
                    ) {
                        Text(
                            text = task.weekBadgeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF065F46),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.formattedReward,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SucessoVerdeAgua
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Tarefa",
                        tint = BotaoPrimarioAzul
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir Tarefa",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormDialog(
    task: TaskItem?,
    children: List<Child>,
    onDismiss: () -> Unit,
    onSave: (title: String, room: String, cents: Long, description: String, assignedChildId: Long?, assignedChildName: String?, targetWeek: Int?) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var selectedRoom by remember {
        mutableStateOf(task?.roomCategory ?: RoomCategory.QUARTO)
    }
    var rewardText by remember {
        val initial = if (task != null) String.format("%.2f", task.rewardCents / 100.0) else "1.00"
        mutableStateOf(initial)
    }
    var description by remember { mutableStateOf(task?.description ?: "") }

    // Target child selector (null means "Todas as crianças")
    var selectedChildId by remember { mutableStateOf(task?.assignedChildId) }
    var selectedChildName by remember { mutableStateOf(task?.assignedChildName ?: "Todas as crianças") }

    // Target week selector
    var selectedWeek by remember {
        mutableStateOf(task?.targetWeek ?: task?.effectiveWeek ?: 1)
    }
    var expandedWeekDropdown by remember { mutableStateOf(false) }

    var expandedRoomDropdown by remember { mutableStateOf(false) }
    var expandedChildDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (task == null) "Nova Tarefa" else "Editar Tarefa",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Tarefa") },
                    placeholder = { Text("Ex: Arrumar a cama") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                // "Para quem é a tarefa?" Selector
                ExposedDropdownMenuBox(
                    expanded = expandedChildDropdown,
                    onExpandedChange = { expandedChildDropdown = it }
                ) {
                    OutlinedTextField(
                        value = if (selectedChildId == null || selectedChildId == 0L) "🌟 Todas as crianças" else selectedChildName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Para quem é a tarefa?") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedChildDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("task_assigned_child_select")
                    )
                    ExposedDropdownMenu(
                        expanded = expandedChildDropdown,
                        onDismissRequest = { expandedChildDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("🌟 Todas as crianças") },
                            onClick = {
                                selectedChildId = null
                                selectedChildName = "Todas as crianças"
                                expandedChildDropdown = false
                            }
                        )
                        children.forEach { child ->
                            DropdownMenuItem(
                                text = { Text("${child.avatarEmoji} ${child.name}") },
                                onClick = {
                                    selectedChildId = child.id
                                    selectedChildName = "${child.name} ${child.avatarEmoji}"
                                    expandedChildDropdown = false
                                }
                            )
                        }
                    }
                }

                // Room Selector
                ExposedDropdownMenuBox(
                    expanded = expandedRoomDropdown,
                    onExpandedChange = { expandedRoomDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedRoom.labelWithEmoji,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cômodo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoomDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRoomDropdown,
                        onDismissRequest = { expandedRoomDropdown = false }
                    ) {
                        RoomCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.labelWithEmoji) },
                                onClick = {
                                    selectedRoom = cat
                                    expandedRoomDropdown = false
                                }
                            )
                        }
                    }
                }

                // Week of Calendar Selector
                val weekOptions = listOf(
                    1 to "Semana 1 (dias 01 a 07)",
                    2 to "Semana 2 (dias 08 a 14)",
                    3 to "Semana 3 (dias 15 a 21)",
                    4 to "Semana 4 (dias 22 a 28)",
                    5 to "Semana 5 (dias 29 a 31)",
                    -1 to "Todas as semanas (Semanal)"
                )
                val currentWeekText = weekOptions.find { it.first == selectedWeek }?.second ?: "Semana 1 (dias 01 a 07)"

                ExposedDropdownMenuBox(
                    expanded = expandedWeekDropdown,
                    onExpandedChange = { expandedWeekDropdown = it }
                ) {
                    OutlinedTextField(
                        value = "📅 $currentWeekText",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Semana do Calendário") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWeekDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("task_week_select")
                    )
                    ExposedDropdownMenu(
                        expanded = expandedWeekDropdown,
                        onDismissRequest = { expandedWeekDropdown = false }
                    ) {
                        weekOptions.forEach { (weekNum, weekLabel) ->
                            DropdownMenuItem(
                                text = { Text(weekLabel) },
                                onClick = {
                                    selectedWeek = weekNum
                                    expandedWeekDropdown = false
                                }
                            )
                        }
                    }
                }

                // Reward input
                OutlinedTextField(
                    value = rewardText,
                    onValueChange = { rewardText = it },
                    label = { Text("Valor da Recompensa (R$)") },
                    placeholder = { Text("Ex: 1.50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("R$ ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_reward_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição / Instruções") },
                    placeholder = { Text("Ex: Esticar o lençol e alinhar o travesseiro") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sanitized = rewardText.replace(",", ".").trim()
                    val valueDouble = sanitized.toDoubleOrNull() ?: 1.0
                    val cents = Math.round(valueDouble * 100)
                    if (title.isNotBlank()) {
                        onSave(
                            title.trim(),
                            selectedRoom.displayName,
                            cents,
                            description.trim(),
                            selectedChildId,
                            selectedChildName,
                            selectedWeek
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BotaoPrimarioAzul),
                modifier = Modifier.testTag("task_dialog_save_button")
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
