package com.example.expensetrackerassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.expensetrackerassignment.data.AppDatabase
import com.example.expensetrackerassignment.mainui.BottomNavigationBar
import com.example.expensetrackerassignment.mainui.Navigation
import com.example.expensetrackerassignment.ui.theme.ExpenseTrackerAssignmentTheme
import com.example.expensetrackerassignment.viewmodel.DashboardViewModel
import com.example.expensetrackerassignment.viewmodel.DashboardViewModelFactory
import com.example.expensetrackerassignment.viewmodel.ExpenseViewModel
import com.example.expensetrackerassignment.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory(database.expenseDao())
    }
    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(database.expenseDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerAssignmentTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Navigation(navController = navController, expenseViewModel = expenseViewModel, dashboardViewModel = dashboardViewModel)
                    }
                }
            }
        }
    }
}