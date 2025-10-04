package com.example.expensetrackerassignment.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
}