package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val imageUrl: String,
    val linkUrl: String,
    val isEnabled: Boolean = true,
    val displayOrder: Int = 0
)
