package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val username: String,
    val fullName: String,
    val mobileNumber: String,
    val password: String,
    val status: String = "PENDING_APPROVAL", // PENDING_APPROVAL, APPROVED, REJECTED
    val betProLink: String = "",
    val betProUsername: String = "",
    val betProPassword: String = "",
    val registrationTimestamp: Long = System.currentTimeMillis()
)
