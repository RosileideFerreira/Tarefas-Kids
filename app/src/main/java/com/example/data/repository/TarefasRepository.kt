package com.example.data.repository

import com.example.data.local.ChildEntity
import com.example.data.local.CompletionEntity
import com.example.data.local.ParentAccountEntity
import com.example.data.local.RedemptionEntity
import com.example.data.local.TarefasDao
import com.example.data.local.TaskEntity
import com.example.model.BalanceSummary
import com.example.model.Child
import com.example.model.CompletionStatus
import com.example.model.RewardRedemption
import com.example.model.RoomCategory
import com.example.model.TaskCompletion
import com.example.model.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class TarefasRepository(private val dao: TarefasDao) {

    val children: Flow<List<Child>> = dao.getAllChildren().map { list ->
        list.map { it.toJavaModel() }
    }

    val allTasks: Flow<List<TaskItem>> = dao.getAllTasks().map { list ->
        list.map { it.toJavaModel() }
    }

    val activeTasks: Flow<List<TaskItem>> = dao.getActiveTasks().map { list ->
        list.map { it.toJavaModel() }
    }

    val pendingCompletions: Flow<List<TaskCompletion>> = dao.getPendingCompletions().map { list ->
        list.map { it.toJavaModel() }
    }

    val pendingRedemptions: Flow<List<RewardRedemption>> = dao.getPendingRedemptions().map { list ->
        list.map { it.toJavaModel() }
    }

    val allCompletions: Flow<List<TaskCompletion>> = dao.getAllCompletions().map { list ->
        list.map { it.toJavaModel() }
    }

    val allRedemptions: Flow<List<RewardRedemption>> = dao.getAllRedemptions().map { list ->
        list.map { it.toJavaModel() }
    }

    fun getCompletionsForChild(childId: Long): Flow<List<TaskCompletion>> {
        return dao.getCompletionsForChild(childId).map { list ->
            list.map { it.toJavaModel() }
        }
    }

    fun getRedemptionsForChild(childId: Long): Flow<List<RewardRedemption>> {
        return dao.getRedemptionsForChild(childId).map { list ->
            list.map { it.toJavaModel() }
        }
    }

    /**
     * Calcula o resumo de saldos para todas as crianças.
     */
    fun getBalancesFlow(): Flow<Map<Long, BalanceSummary>> {
        return combine(
            dao.getAllChildren(),
            dao.getAllCompletions(),
            dao.getAllRedemptions()
        ) { childrenList, completionsList, redemptionsList ->
            val now = System.currentTimeMillis()
            val weekStart = getWeekStartMillis(now)
            val monthStart = getMonthStartMillis(now)

            val summaryMap = mutableMapOf<Long, BalanceSummary>()

            for (child in childrenList) {
                summaryMap[child.id] = BalanceSummary(child.id, child.name, child.avatarEmoji)
            }

            for (comp in completionsList) {
                val summary = summaryMap[comp.childId] ?: continue
                if (comp.status == CompletionStatus.APPROVED.code) {
                    summary.totalApprovedEarningsCents += comp.rewardCents
                    if (comp.completedAt >= weekStart) {
                        summary.weeklyEarningsCents += comp.rewardCents
                    }
                    if (comp.completedAt >= monthStart) {
                        summary.monthlyEarningsCents += comp.rewardCents
                    }
                } else if (comp.status == CompletionStatus.PENDING.code) {
                    summary.pendingTasksCount++
                }
            }

            for (red in redemptionsList) {
                val summary = summaryMap[red.childId] ?: continue
                if (red.status == CompletionStatus.APPROVED.code) {
                    summary.approvedRedemptionsCents += red.costCents
                } else if (red.status == CompletionStatus.PENDING.code) {
                    summary.pendingRedemptionsCents += red.costCents
                    summary.pendingRedemptionsCount++
                }
            }

            summaryMap
        }
    }

    // --- Actions ---

    suspend fun addChild(name: String, avatarEmoji: String, parentId: String = "default_parent"): Long {
        return dao.insertChild(ChildEntity(name = name, avatarEmoji = avatarEmoji, parentId = parentId))
    }

    suspend fun deleteChild(childId: Long) {
        dao.deleteChild(childId)
    }

    suspend fun addTask(
        title: String,
        room: String,
        rewardCents: Long,
        description: String = "",
        assignedChildId: Long? = null,
        assignedChildName: String? = null,
        targetWeek: Int? = null,
        parentId: String = "default_parent"
    ): Long {
        return dao.insertTask(
            TaskEntity(
                title = title,
                room = room,
                rewardCents = rewardCents,
                description = description,
                assignedChildId = assignedChildId,
                assignedChildName = assignedChildName ?: "Todas as crianças",
                targetWeek = targetWeek,
                parentId = parentId
            )
        )
    }

    suspend fun updateTask(task: TaskItem) {
        dao.updateTask(TaskEntity.fromJavaModel(task))
    }

    suspend fun deleteTask(taskId: Long) {
        dao.deleteTask(taskId)
    }

    suspend fun submitCompletion(child: Child, task: TaskItem): Long {
        val entity = CompletionEntity(
            taskId = task.id,
            childId = child.id,
            taskTitle = task.title,
            taskRoom = task.room,
            rewardCents = task.rewardCents,
            status = CompletionStatus.PENDING.code,
            completedAt = System.currentTimeMillis()
        )
        return dao.insertCompletion(entity)
    }

    suspend fun cancelCompletion(completionId: Long) {
        dao.deleteCompletion(completionId)
    }

    suspend fun reviewCompletion(completionId: Long, approve: Boolean) {
        val status = if (approve) CompletionStatus.APPROVED.code else CompletionStatus.REJECTED.code
        dao.updateCompletionStatus(completionId, status, System.currentTimeMillis())
    }

    suspend fun requestRedemption(child: Child, rewardTitle: String, costCents: Long): Long {
        val entity = RedemptionEntity(
            childId = child.id,
            childName = child.name,
            title = rewardTitle,
            costCents = costCents,
            status = CompletionStatus.PENDING.code,
            createdAt = System.currentTimeMillis()
        )
        return dao.insertRedemption(entity)
    }

    suspend fun reviewRedemption(redemptionId: Long, approve: Boolean) {
        val status = if (approve) CompletionStatus.APPROVED.code else CompletionStatus.REJECTED.code
        dao.updateRedemptionStatus(redemptionId, status, System.currentTimeMillis())
    }

    val parentAccount: Flow<ParentAccountEntity?> = dao.getParentAccountFlow()

    suspend fun getParentAccount(): ParentAccountEntity? {
        return dao.getParentAccount()
    }

    suspend fun registerFamily(
        email: String,
        passwordHash: String,
        familyName: String,
        childrenList: List<Pair<String, String>>
    ) {
        val account = ParentAccountEntity(
            id = 1,
            email = email,
            passwordHash = passwordHash,
            familyName = familyName,
            isLoggedIn = true,
            createdAt = System.currentTimeMillis()
        )
        dao.saveParentAccount(account)

        // Inserir as crianças da família cadastradas pelo responsável
        for ((name, avatar) in childrenList) {
            if (name.isNotBlank()) {
                dao.insertChild(ChildEntity(name = name.trim(), avatarEmoji = avatar))
            }
        }
    }

    suspend fun updateLoginState(isLoggedIn: Boolean) {
        dao.updateLoginState(isLoggedIn)
    }

    suspend fun cleanupLegacyDemoData() {
        // Remove dados residuais de Ana e Léo caso existam de execuções anteriores
        val allComps = dao.getAllCompletions()
        // Limpar completions e redemptions associadas a dados de demonstração antigos
        dao.clearCompletions()
        dao.clearRedemptions()
        dao.clearTasks()
        dao.clearChildren()
    }

    suspend fun ensureInitialOrDemoData(forceReload: Boolean = false) {
        if (forceReload) {
            dao.clearChildren()
            dao.clearTasks()
            dao.clearCompletions()
            dao.clearRedemptions()
        }
    }

    companion object {
        fun getWeekStartMillis(timestamp: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun getMonthStartMillis(timestamp: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }
}
