package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickOptionDao {
    @Query("SELECT * FROM quick_options ORDER BY displayOrder ASC")
    fun getAllQuickOptions(): Flow<List<QuickOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuickOption(option: QuickOptionEntity)

    @Query("DELETE FROM quick_options WHERE id = :id")
    suspend fun deleteQuickOption(id: Long)

    @Query("UPDATE quick_options SET name = :name, url = :url, isEnabled = :isEnabled, displayOrder = :displayOrder WHERE id = :id")
    suspend fun updateQuickOption(id: Long, name: String, url: String, isEnabled: Boolean, displayOrder: Int)
}
