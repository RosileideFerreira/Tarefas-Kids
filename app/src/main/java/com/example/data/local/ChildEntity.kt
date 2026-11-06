package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Child

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: String = "default_family",
    val name: String,
    val avatarEmoji: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toJavaModel(): Child {
        return Child(id, parentId, name, avatarEmoji, createdAt)
    }

    companion object {
        fun fromJavaModel(child: Child): ChildEntity {
            return ChildEntity(
                id = child.id,
                parentId = if (child.parentId != null) child.parentId else "default_family",
                name = child.name,
                avatarEmoji = child.avatarEmoji,
                createdAt = child.createdAt
            )
        }
    }
}
