package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawal_requests ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawal_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getWithdrawalsForUser(userId: String): Flow<List<WithdrawalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(request: WithdrawalRequestEntity): Long

    @Query("UPDATE withdrawal_requests SET status = :status, rejectionReason = :rejectionReason WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, rejectionReason: String?)
}
