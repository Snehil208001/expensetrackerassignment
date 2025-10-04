package com.example.expensetrackerassignment.mainui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
    val expenses by viewModel.allExpenses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }

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
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (expenses.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("No expenses yet. Tap '+' to add one!")
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    items(expenses) { expense ->
                        ExpenseListItem(
                            expense = expense,
                            onEdit = {
                                selectedExpense = it
                                showEditDialog = true
                            },
                            onDelete = { viewModel.deleteExpense(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = {
                viewModel.addExpense(it)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog) {
        EditExpenseDialog(
            expense = selectedExpense!!,
            onDismiss = { showEditDialog = false },
            onConfirm = {
                viewModel.updateExpense(it)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ExpenseListItem(expense: Expense, onEdit: (Expense) -> Unit, onDelete: (Expense) -> Unit) {
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
                text = "₹%.2f".format(expense.amount),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { onEdit(expense) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Expense")
            }
            IconButton(onClick = { onDelete(expense) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Expense", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onConfirm: (Expense) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    val categories = listOf(
        "Food", "Travel", "Bills", "Groceries", "Entertainment",
        "Shopping", "Health", "Education", "Gifts", "Other"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = null
                    },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    singleLine = true
                )
                if (amountError != null) {
                    Text(amountError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseDialog(expense: Expense, onDismiss: () -> Unit, onConfirm: (Expense) -> Unit) {
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var category by remember { mutableStateOf(expense.category) }
    var note by remember { mutableStateOf(expense.note) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    val categories = listOf(
        "Food", "Travel", "Bills", "Groceries", "Entertainment",
        "Shopping", "Health", "Education", "Gifts", "Other"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = null
                    },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    singleLine = true
                )
                if (amountError != null) {
                    Text(amountError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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
                if (amountDouble == null || amountDouble <= 0) {
                    amountError = "Enter a valid amount"
                } else if (category.isBlank()) {
                    categoryError = "Category cannot be empty"
                } else {
                    val updatedExpense = expense.copy(
                        amount = amountDouble,
                        category = category.trim(),
                        note = note.trim()
                    )
                    onConfirm(updatedExpense)
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