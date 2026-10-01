package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminSettingDao {
    @Query("SELECT * FROM admin_settings")
    fun getAllSettings(): Flow<List<AdminSettingEntity>>

    @Query("SELECT settingValue FROM admin_settings WHERE settingKey = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Query("SELECT settingValue FROM admin_settings WHERE settingKey = :key LIMIT 1")
    fun getSettingSync(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AdminSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setSettingSync(setting: AdminSettingEntity)
}
