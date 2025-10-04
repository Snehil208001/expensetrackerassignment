package com.example.expensetrackerassignment.mainui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetrackerassignment.data.CategorySpending
import com.example.expensetrackerassignment.viewmodel.DashboardViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val spendingByCategory by viewModel.spendingByCategory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            TotalExpensesCard(totalExpenses = totalExpenses ?: 0.0)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Spending by Category",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(spendingByCategory) { categorySpending ->
            CategorySpendingItem(categorySpending, totalExpenses ?: 1.0)
        }
    }
}

@Composable
fun TotalExpensesCard(totalExpenses: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Expenses",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹%.2f".format(totalExpenses),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CategorySpendingItem(categorySpending: CategorySpending, totalExpenses: Double) {
    val percentage = if (totalExpenses > 0) (categorySpending.totalAmount / totalExpenses).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getIconForCategory(categorySpending.category),
                    contentDescription = categorySpending.category,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(text = categorySpending.category, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "₹%.2f".format(categorySpending.totalAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${(percentage * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
        }
    }
}

fun getIconForCategory(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Default.Restaurant
        "Travel" -> Icons.Default.Train
        "Bills" -> Icons.Default.Lightbulb
        "Groceries" -> Icons.Default.ShoppingCart
        "Shopping" -> Icons.Default.LocalMall
        "Education" -> Icons.Default.School
        else -> Icons.Default.Wallet
    }
}