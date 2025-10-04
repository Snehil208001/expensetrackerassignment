package com.example.expensetrackerassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerassignment.data.CategorySpending
import com.example.expensetrackerassignment.data.ExpenseDao
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(private val dao: ExpenseDao) : ViewModel() {

    val totalExpenses: StateFlow<Double?> = dao.getTotalExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )

    val spendingByCategory: StateFlow<List<CategorySpending>> = dao.getSpendingByCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}

class DashboardViewModelFactory(private val dao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}