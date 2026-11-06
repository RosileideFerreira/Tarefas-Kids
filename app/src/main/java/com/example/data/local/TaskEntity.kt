package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.TaskItem

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: String = "default_family",
    val title: String,
    val room: String,
    val rewardCents: Long,
    val active: Boolean = true,
    val description: String = "",
    val assignedChildId: Long? = null,
    val assignedChildName: String? = "Todas as crianças",
    val createdAt: Long = System.currentTimeMillis(),
    val targetWeek: Int? = null
) {
    fun toJavaModel(): TaskItem {
        val item = TaskItem(id, parentId, title, room, rewardCents, active, description, createdAt)
        item.assignedChildId = assignedChildId
        item.assignedChildName = assignedChildName ?: "Todas as crianças"
        item.targetWeek = targetWeek
        return item
    }

    companion object {
        fun fromJavaModel(task: TaskItem): TaskEntity {
            return TaskEntity(
                id = task.id,
                parentId = if (task.parentId != null) task.parentId else "default_family",
                title = task.title,
                room = task.room,
                rewardCents = task.rewardCents,
                active = task.isActive,
                description = if (task.description != null) task.description else "",
                assignedChildId = task.assignedChildId,
                assignedChildName = task.assignedChildName,
                createdAt = task.createdAt,
                targetWeek = task.targetWeek
            )
        }
    }
}
