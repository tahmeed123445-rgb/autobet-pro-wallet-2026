package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_demo_1",
    val username: String = "DemoUser",
    val betProUsername: String = "",
    val method: String,
    val accountHolderName: String,
    val accountNumber: String,
    val bankName: String? = null,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val rejectionReason: String? = null
)
