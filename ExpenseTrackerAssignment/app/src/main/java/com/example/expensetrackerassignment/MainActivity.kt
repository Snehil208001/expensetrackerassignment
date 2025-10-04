package com.example.expensetrackerassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.expensetrackerassignment.data.AppDatabase
import com.example.expensetrackerassignment.mainui.ExpenseScreen
import com.example.expensetrackerassignment.ui.theme.ExpenseTrackerAssignmentTheme
import com.example.expensetrackerassignment.viewmodel.ExpenseViewModel
import com.example.expensetrackerassignment.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {

    // Lazily initialize the database and ViewModel
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val viewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(database.expenseDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerAssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the ViewModel to our main screen
                    ExpenseScreen(viewModel = viewModel)
                }
            }
        }
    }
}