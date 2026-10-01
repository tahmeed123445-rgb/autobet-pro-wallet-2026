package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {
    @Query("SELECT * FROM banners ORDER BY displayOrder ASC")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity)

    @Query("DELETE FROM banners WHERE id = :id")
    suspend fun deleteBanner(id: Long)

    @Query("UPDATE banners SET title = :title, imageUrl = :imageUrl, linkUrl = :linkUrl, isEnabled = :isEnabled, displayOrder = :displayOrder WHERE id = :id")
    suspend fun updateBanner(id: Long, title: String, imageUrl: String, linkUrl: String, isEnabled: Boolean, displayOrder: Int)
}
