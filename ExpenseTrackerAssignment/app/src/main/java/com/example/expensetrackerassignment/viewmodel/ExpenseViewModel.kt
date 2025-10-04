package com.example.expensetrackerassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerassignment.data.Expense
import com.example.expensetrackerassignment.data.ExpenseDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(private val dao: ExpenseDao) : ViewModel() {

    val allExpenses = dao.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            dao.insert(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            dao.update(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            dao.delete(expense)
        }
    }
}

class ExpenseViewModelFactory(private val dao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}