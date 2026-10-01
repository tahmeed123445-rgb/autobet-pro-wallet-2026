package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val accountNumber: String,
    val accountTitle: String,
    val instructions: String,
    val isEnabled: Boolean = true,
    val displayOrder: Int = 0
)
