package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.CompletionStatus
import com.example.model.TaskCompletion

@Entity(tableName = "completions")
data class CompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: String = "default_family",
    val taskId: Long,
    val childId: Long,
    val taskTitle: String,
    val taskRoom: String,
    val rewardCents: Long,
    val status: String = CompletionStatus.PENDING.code,
    val completedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
) {
    fun toJavaModel(): TaskCompletion {
        return TaskCompletion(
            id,
            parentId,
            taskId,
            childId,
            taskTitle,
            taskRoom,
            rewardCents,
            CompletionStatus.fromCode(status),
            completedAt,
            reviewedAt
        )
    }

    companion object {
        fun fromJavaModel(comp: TaskCompletion): CompletionEntity {
            return CompletionEntity(
                id = comp.id,
                parentId = if (comp.parentId != null) comp.parentId else "default_family",
                taskId = comp.taskId,
                childId = comp.childId,
                taskTitle = comp.taskTitle,
                taskRoom = comp.taskRoom,
                rewardCents = comp.rewardCents,
                status = comp.status.code,
                completedAt = comp.completedAt,
                reviewedAt = comp.reviewedAt
            )
        }
    }
}
