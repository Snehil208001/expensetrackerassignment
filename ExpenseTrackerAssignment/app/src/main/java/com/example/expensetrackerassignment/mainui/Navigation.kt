package com.example.expensetrackerassignment.mainui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expensetrackerassignment.viewmodel.DashboardViewModel
import com.example.expensetrackerassignment.viewmodel.ExpenseViewModel

@Composable
fun Navigation(navController: NavHostController, expenseViewModel: ExpenseViewModel, dashboardViewModel: DashboardViewModel) {
    NavHost(navController = navController, startDestination = NavigationItem.Expenses.route) {
        composable(NavigationItem.Expenses.route) {
            ExpenseScreen(viewModel = expenseViewModel)
        }
        composable(NavigationItem.Dashboard.route) {
            DashboardScreen(viewModel = dashboardViewModel)
        }
    }
}