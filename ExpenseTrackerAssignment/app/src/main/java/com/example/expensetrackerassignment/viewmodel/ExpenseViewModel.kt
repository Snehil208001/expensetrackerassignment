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

    // This exposes a stream of all expenses from the database to the UI.
    // The UI will automatically update when this data changes.
    val allExpenses = dao.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Functions to be called from the UI
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            dao.insert(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            dao.delete(expense)
        }
    }
}

// This factory is crucial for creating a ViewModel that needs dependencies (like our DAO).
class ExpenseViewModelFactory(private val dao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}