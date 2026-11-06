package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ParentAccountEntity
import com.example.data.local.TarefasDatabase
import com.example.data.repository.TarefasRepository
import com.example.model.BalanceSummary
import com.example.model.Child
import com.example.model.RewardRedemption
import com.example.model.RoomCategory
import com.example.model.TaskCompletion
import com.example.model.TaskItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    REGISTER,
    PARENT_LOGIN,
    PARENT_PIN,
    PARENT_DASHBOARD,
    PARENT_TASKS_MANAGE,
    KIDS_SELECT,
    KIDS_DASHBOARD,
    HISTORY
}

class TarefasViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("tarefas_kids_prefs", Context.MODE_PRIVATE)
    private val repository: TarefasRepository

    val children: StateFlow<List<Child>>
    val allTasks: StateFlow<List<TaskItem>>
    val activeTasks: StateFlow<List<TaskItem>>
    val pendingCompletions: StateFlow<List<TaskCompletion>>
    val pendingRedemptions: StateFlow<List<RewardRedemption>>
    val balances: StateFlow<Map<Long, BalanceSummary>>
    val allCompletions: StateFlow<List<TaskCompletion>>
    val allRedemptions: StateFlow<List<RewardRedemption>>
    val parentAccount: StateFlow<ParentAccountEntity?>

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedChild = MutableStateFlow<Child?>(null)
    val selectedChild: StateFlow<Child?> = _selectedChild.asStateFlow()

    private val _navigatedFromParent = MutableStateFlow(false)
    val navigatedFromParent: StateFlow<Boolean> = _navigatedFromParent.asStateFlow()

    private val _selectedRoomFilter = MutableStateFlow<RoomCategory?>(null)
    val selectedRoomFilter: StateFlow<RoomCategory?> = _selectedRoomFilter.asStateFlow()

    private val _selectedHistoryChildId = MutableStateFlow<Long?>(null)
    val selectedHistoryChildId: StateFlow<Long?> = _selectedHistoryChildId.asStateFlow()

    private val _parentPin = MutableStateFlow(prefs.getString("pref_parent_pin", "1234") ?: "1234")
    val parentPin: StateFlow<String> = _parentPin.asStateFlow()

    private val _isParentLoggedIn = MutableStateFlow(prefs.getBoolean("pref_is_logged_in", false))
    val isParentLoggedIn: StateFlow<Boolean> = _isParentLoggedIn.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("pref_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        val database = TarefasDatabase.getInstance(application)
        repository = TarefasRepository(database.tarefasDao())

        parentAccount = repository.parentAccount.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )
        children = repository.children.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allTasks = repository.allTasks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        activeTasks = repository.activeTasks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        pendingCompletions = repository.pendingCompletions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        pendingRedemptions = repository.pendingRedemptions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        balances = repository.getBalancesFlow().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap()
        )
        allCompletions = repository.allCompletions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allRedemptions = repository.allRedemptions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        // Limpeza dos dados residuais de exemplo (Ana e Léo) se for a primeira vez após a alteração
        viewModelScope.launch {
            val account = repository.getParentAccount()
            val hasLegacyDemoCleared = prefs.getBoolean("pref_legacy_demo_cleared_v2", false)
            if (!hasLegacyDemoCleared) {
                // Remover Ana e Léo e seus registros
                repository.cleanupLegacyDemoData()
                prefs.edit().putBoolean("pref_legacy_demo_cleared_v2", true).apply()
            }
        }
    }

    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        prefs.edit().putBoolean("pref_dark_mode", newMode).apply()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectChild(child: Child?, fromParent: Boolean = false) {
        _selectedChild.value = child
        _navigatedFromParent.value = fromParent
        if (child != null) {
            _selectedHistoryChildId.value = child.id
        }
    }

    fun setRoomFilter(room: RoomCategory?) {
        _selectedRoomFilter.value = room
    }

    fun setHistoryChildId(childId: Long?) {
        _selectedHistoryChildId.value = childId
    }

    fun emitToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    // --- Cadastro do Responsável e Crianças ---
    fun registerFamily(
        name: String,
        email: String,
        password: String,
        pin: String,
        childrenList: List<Pair<String, String>>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                emitToast("Informe o nome do responsável ou da família.")
                return@launch
            }
            if (email.isBlank() || !email.contains("@")) {
                emitToast("Informe um e-mail válido.")
                return@launch
            }
            if (password.length < 6) {
                emitToast("A senha precisa ter pelo menos 6 caracteres.")
                return@launch
            }
            val cleanPin = if (pin.length == 4) pin else "1234"

            prefs.edit()
                .putString("pref_parent_name", name.trim())
                .putString("pref_parent_email", email.trim().lowercase())
                .putString("pref_parent_pass", password)
                .putString("pref_parent_pin", cleanPin)
                .putBoolean("pref_is_logged_in", true)
                .apply()

            _parentPin.value = cleanPin
            _isParentLoggedIn.value = true

            repository.registerFamily(
                email = email.trim().lowercase(),
                passwordHash = password,
                familyName = name.trim(),
                childrenList = childrenList
            )

            emitToast("Família cadastrada com sucesso! Bem-vindo(a) 🎉")
            onSuccess()
        }
    }

    fun loginWithPassword(email: String, pass: String): Boolean {
        val savedEmail = prefs.getString("pref_parent_email", "") ?: ""
        val savedPass = prefs.getString("pref_parent_pass", "") ?: ""

        if (savedEmail.isBlank()) {
            emitToast("Nenhum cadastro encontrado. Crie sua conta primeiro.")
            return false
        }

        if (email.trim().equals(savedEmail, ignoreCase = true) && pass == savedPass) {
            _isParentLoggedIn.value = true
            prefs.edit().putBoolean("pref_is_logged_in", true).apply()
            viewModelScope.launch { repository.updateLoginState(true) }
            emitToast("Login realizado com sucesso! 👨‍👩‍👧‍👦")
            return true
        } else {
            emitToast("E-mail ou senha incorretos.")
            return false
        }
    }

    fun loginWithPin(pin: String): Boolean {
        if (pin == _parentPin.value) {
            _isParentLoggedIn.value = true
            prefs.edit().putBoolean("pref_is_logged_in", true).apply()
            return true
        }
        return false
    }

    fun logoutParent() {
        _isParentLoggedIn.value = false
        prefs.edit().putBoolean("pref_is_logged_in", false).apply()
        viewModelScope.launch { repository.updateLoginState(false) }
        emitToast("Sessão do responsável encerrada.")
        _currentScreen.value = AppScreen.HOME
    }

    fun isRegistered(): Boolean {
        val email = prefs.getString("pref_parent_email", "") ?: ""
        return email.isNotBlank()
    }

    fun getFamilyName(): String {
        return prefs.getString("pref_parent_name", "Família") ?: "Família"
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.ensureInitialOrDemoData(forceReload = true)
            emitToast("Dados limpos com sucesso.")
        }
    }

    fun addChild(name: String, avatarEmoji: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                emitToast("Informe o nome da criança.")
                return@launch
            }
            repository.addChild(name.trim(), avatarEmoji)
            emitToast("Criança $name cadastrada com sucesso! 🎉")
        }
    }

    fun deleteChild(childId: Long) {
        viewModelScope.launch {
            repository.deleteChild(childId)
            if (_selectedChild.value?.id == childId) {
                _selectedChild.value = null
            }
            emitToast("Criança removida.")
        }
    }

    fun addTask(
        title: String,
        room: String,
        rewardCents: Long,
        description: String,
        assignedChildId: Long? = null,
        assignedChildName: String? = null,
        targetWeek: Int? = null
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                emitToast("Informe o título da tarefa.")
                return@launch
            }
            if (rewardCents <= 0) {
                emitToast("O valor da recompensa deve ser maior que zero.")
                return@launch
            }
            repository.addTask(
                title.trim(),
                room,
                rewardCents,
                description.trim(),
                assignedChildId = assignedChildId,
                assignedChildName = assignedChildName,
                targetWeek = targetWeek
            )
            emitToast("Tarefa criada com sucesso! 👏")
        }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            repository.updateTask(task)
            emitToast("Tarefa atualizada!")
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            emitToast("Tarefa excluída.")
        }
    }

    fun completeTask(child: Child, task: TaskItem) {
        viewModelScope.launch {
            repository.submitCompletion(child, task)
            emitToast("Parabéns, ${child.name}! Tarefa enviada para aprovação dos pais 🎉")
        }
    }

    fun cancelCompletion(completionId: Long) {
        viewModelScope.launch {
            repository.cancelCompletion(completionId)
            emitToast("Marcação da tarefa desfeita.")
        }
    }

    fun reviewCompletion(completionId: Long, approve: Boolean) {
        viewModelScope.launch {
            repository.reviewCompletion(completionId, approve)
            if (approve) {
                emitToast("Tarefa aprovada! Saldo atualizado ✅")
            } else {
                emitToast("Tarefa recusada ❌")
            }
        }
    }

    fun requestRedemption(child: Child, rewardTitle: String, costCents: Long): Boolean {
        if (rewardTitle.isBlank()) {
            emitToast("Informe o que deseja como recompensa.")
            return false
        }
        val summary = balances.value[child.id]
        val availableCents = summary?.availableBalanceCents ?: 0L
        if (costCents > availableCents) {
            val formattedAvailable = summary?.formattedAvailable ?: "R$ 0,00"
            emitToast("Saldo insuficiente! Você possui $formattedAvailable disponível.")
            return false
        }
        viewModelScope.launch {
            repository.requestRedemption(child, rewardTitle.trim(), costCents)
            emitToast("Pedido de recompensa enviado aos pais! 🎁")
        }
        return true
    }

    fun reviewRedemption(redemptionId: Long, approve: Boolean) {
        viewModelScope.launch {
            repository.reviewRedemption(redemptionId, approve)
            if (approve) {
                emitToast("Recompensa aprovada! Aproveite 🎁✅")
            } else {
                emitToast("Recompensa recusada ❌")
            }
        }
    }
}
