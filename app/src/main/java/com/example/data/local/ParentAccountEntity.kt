package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_accounts")
data class ParentAccountEntity(
    @PrimaryKey
    val id: Long = 1,
    val email: String,
    val passwordHash: String,
    val familyName: String = "Família",
    val isLoggedIn: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
