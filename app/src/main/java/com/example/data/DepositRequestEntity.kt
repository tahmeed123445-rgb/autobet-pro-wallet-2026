package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposit_requests")
data class DepositRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_demo_1",
    val betProUsername: String = "",
    val paymentMethod: String,
    val amount: Double,
    val bonus: Double,
    val screenshotUri: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val rejectionReason: String? = null
)
