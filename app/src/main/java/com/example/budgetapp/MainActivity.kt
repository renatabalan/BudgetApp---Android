package com.example.budgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapp.data.local.AppDatabase
import com.example.budgetapp.core.repository.BudgetRepository
import com.example.budgetapp.core.repository.CategoryRepository
import com.example.budgetapp.core.repository.ExpenseRepository
import com.example.budgetapp.gamification.GamificationManager
import com.example.budgetapp.ui.theme.BudgetAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetAppTheme {
                BudgetApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetApp() {
    val context = LocalContext.current

    val db = remember { AppDatabase.getInstance(context) }

    val expenseRepository = remember(db) { ExpenseRepository(db) }
    val categoryRepository = remember(db) { CategoryRepository(db) }
    val budgetRepository = remember(db) { BudgetRepository(db) }
    val gamificationManager = remember { GamificationManager(context.applicationContext) }

    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(
            expenseRepository,
            categoryRepository,
            budgetRepository,
            gamificationManager
        )
    )

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("BudgetApp") },
                actions = {
                    TextButton(onClick = { selectedTab = 3}) {
                        Text("Categories")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Dashboard") },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Add") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("History") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = appViewModel)
                1 -> AddExpenseScreen(viewModel = appViewModel)
                2 -> ExpenseListScreen(viewModel = appViewModel)
                3 -> CategoryManagementScreen(viewModel = appViewModel)
            }
        }
    }
}
