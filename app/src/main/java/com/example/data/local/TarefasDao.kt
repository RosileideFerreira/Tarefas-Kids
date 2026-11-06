package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefasDao {

    // --- Children ---
    @Query("SELECT * FROM children ORDER BY id ASC")
    fun getAllChildren(): Flow<List<ChildEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildEntity): Long

    @Query("DELETE FROM children WHERE id = :childId")
    suspend fun deleteChild(childId: Long)

    @Query("SELECT COUNT(*) FROM children")
    suspend fun getChildCount(): Int

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE active = 1 ORDER BY createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    // --- Completions ---
    @Query("SELECT * FROM completions ORDER BY completedAt DESC")
    fun getAllCompletions(): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completions WHERE status = 'pending' ORDER BY completedAt DESC")
    fun getPendingCompletions(): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completions WHERE childId = :childId ORDER BY completedAt DESC")
    fun getCompletionsForChild(childId: Long): Flow<List<CompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: CompletionEntity): Long

    @Query("UPDATE completions SET status = :status, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, status: String, reviewedAt: Long)

    @Query("DELETE FROM completions WHERE id = :id")
    suspend fun deleteCompletion(id: Long)

    // --- Redemptions ---
    @Query("SELECT * FROM redemptions ORDER BY createdAt DESC")
    fun getAllRedemptions(): Flow<List<RedemptionEntity>>

    @Query("SELECT * FROM redemptions WHERE status = 'pending' ORDER BY createdAt DESC")
    fun getPendingRedemptions(): Flow<List<RedemptionEntity>>

    @Query("SELECT * FROM redemptions WHERE childId = :childId ORDER BY createdAt DESC")
    fun getRedemptionsForChild(childId: Long): Flow<List<RedemptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedemption(redemption: RedemptionEntity): Long

    @Query("UPDATE redemptions SET status = :status, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateRedemptionStatus(id: Long, status: String, reviewedAt: Long)

    @Query("DELETE FROM redemptions WHERE id = :id")
    suspend fun deleteRedemption(id: Long)

    // --- Reset / Demo ---
    @Query("DELETE FROM children")
    suspend fun clearChildren()

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("DELETE FROM completions")
    suspend fun clearCompletions()

    @Query("DELETE FROM redemptions")
    suspend fun clearRedemptions()

    // --- Parent Account ---
    @Query("SELECT * FROM parent_accounts WHERE id = 1 LIMIT 1")
    fun getParentAccountFlow(): Flow<ParentAccountEntity?>

    @Query("SELECT * FROM parent_accounts WHERE id = 1 LIMIT 1")
    suspend fun getParentAccount(): ParentAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParentAccount(account: ParentAccountEntity)

    @Query("UPDATE parent_accounts SET isLoggedIn = :loggedIn WHERE id = 1")
    suspend fun updateLoginState(loggedIn: Boolean)

    @Query("DELETE FROM parent_accounts")
    suspend fun deleteParentAccount()
}
