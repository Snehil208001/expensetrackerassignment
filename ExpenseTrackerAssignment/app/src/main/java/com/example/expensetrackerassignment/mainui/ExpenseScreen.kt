package com.example.expensetrackerassignment.mainui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetrackerassignment.data.Expense
import com.example.expensetrackerassignment.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    // Observe state from the ViewModel. The UI will automatically recompose when these change.
    val expenses by viewModel.allExpenses.collectAsState()
    val showDialog by remember { mutableStateOf(false) } // This state will be managed within the screen for simplicity

    // Main screen layout using Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Expense Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* We need to show the dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Show a message if the list of expenses is empty
            if (expenses.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("No expenses yet. Tap '+' to add one!")
                }
            } else {
                // Display the list of expenses using LazyColumn for performance
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    items(expenses) { expense ->
                        ExpenseListItem(
                            expense = expense,
                            onDelete = { viewModel.deleteExpense(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseListItem(expense: Expense, onDelete: (Expense) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (expense.note.isNotBlank()) {
                    Text(expense.note, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(expense.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Text(
                text = "$%.2f".format(expense.amount),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { onDelete(expense) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Expense", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onConfirm: (Expense) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = null // Clear error when user types
                    },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    singleLine = true
                )
                if (amountError != null) {
                    Text(amountError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        categoryError = null // Clear error when user types
                    },
                    label = { Text("Category (e.g., Food, Travel)") },
                    isError = categoryError != null,
                    singleLine = true
                )
                if (categoryError != null) {
                    Text(categoryError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountDouble = amount.toDoubleOrNull()
                // --- Basic Validation ---
                if (amountDouble == null || amountDouble <= 0) {
                    amountError = "Enter a valid amount"
                } else if (category.isBlank()) {
                    categoryError = "Category cannot be empty"
                } else {
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val newExpense = Expense(
                        amount = amountDouble,
                        category = category.trim(),
                        note = note.trim(),
                        date = currentDate
                    )
                    onConfirm(newExpense)
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}