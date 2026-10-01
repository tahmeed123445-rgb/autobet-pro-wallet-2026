package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {
    @Query("SELECT * FROM payment_methods ORDER BY displayOrder ASC")
    fun getAllPaymentMethods(): Flow<List<PaymentMethodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(method: PaymentMethodEntity)

    @Query("DELETE FROM payment_methods WHERE id = :id")
    suspend fun deletePaymentMethod(id: Long)

    @Query("UPDATE payment_methods SET name = :name, accountNumber = :accountNumber, accountTitle = :accountTitle, instructions = :instructions, isEnabled = :isEnabled, displayOrder = :displayOrder WHERE id = :id")
    suspend fun updatePaymentMethod(id: Long, name: String, accountNumber: String, accountTitle: String, instructions: String, isEnabled: Boolean, displayOrder: Int)
}
