package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_settings")
data class AdminSettingEntity(
    @PrimaryKey val settingKey: String,
    val settingValue: String
)
