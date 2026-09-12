package com.example.budgetapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetapp.data.local.CategoryEntity

@Composable
fun CategoryManagementScreen(
    viewModel: AppViewModel
) {
    val categories by viewModel.categories.collectAsState()

    var newCategoryName by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Manage Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newCategoryName,
            onValueChange = {
                newCategoryName = it
                errorText = null
            },
            label = { Text("New category name") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val success = viewModel.addCategory(newCategoryName)
                if (!success) {
                    errorText = "Please enter a category name"
                } else {
                    newCategoryName = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Category")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Existing Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (categories.isEmpty()) {
            Text("No categories yet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category: CategoryEntity ->
                    CategoryRow(category = category, onDelete = { id ->
                        viewModel.deleteCategoryById(id)
                    })
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: CategoryEntity,
    onDelete: (Long) -> Unit
) {
    val canDelete = !category.isDefault

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (category.isDefault) {
                    Text(
                        text = "Default",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (canDelete) {
                TextButton(
                    onClick = { onDelete(category.id) }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
