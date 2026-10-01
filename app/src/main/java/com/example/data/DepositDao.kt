package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposit_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposit_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRequestsForUser(userId: String): Flow<List<DepositRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: DepositRequestEntity): Long

    @Query("UPDATE deposit_requests SET status = :status, rejectionReason = :rejectionReason WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, rejectionReason: String?)
}
