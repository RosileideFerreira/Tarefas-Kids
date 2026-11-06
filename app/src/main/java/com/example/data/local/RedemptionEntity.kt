package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.CompletionStatus
import com.example.model.RewardRedemption

@Entity(tableName = "redemptions")
data class RedemptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: String = "default_family",
    val childId: Long,
    val childName: String,
    val title: String,
    val costCents: Long,
    val status: String = CompletionStatus.PENDING.code,
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
) {
    fun toJavaModel(): RewardRedemption {
        return RewardRedemption(
            id,
            parentId,
            childId,
            childName,
            title,
            costCents,
            CompletionStatus.fromCode(status),
            createdAt,
            reviewedAt
        )
    }

    companion object {
        fun fromJavaModel(red: RewardRedemption): RedemptionEntity {
            return RedemptionEntity(
                id = red.id,
                parentId = if (red.parentId != null) red.parentId else "default_family",
                childId = red.childId,
                childName = red.childName,
                title = red.title,
                costCents = red.costCents,
                status = red.status.code,
                createdAt = red.createdAt,
                reviewedAt = red.reviewedAt
            )
        }
    }
}
