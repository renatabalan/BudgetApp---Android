package com.example.budgetapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun DashboardScreen(
    viewModel: AppViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()


    val monthTotal by viewModel.currentMonthTotal.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val xp by viewModel.xp.collectAsState()
    val streak by viewModel.streak.collectAsState()

    var budgetInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val progress: Float = remember(monthTotal, monthlyBudget) {
        val budget = monthlyBudget ?: 0.0
        if (budget == 0.0) 0f
        else (monthTotal / budget).toFloat().coerceIn(0f, 1f)
    }

    val level: Int = remember(xp) { xp / 50 + 1 }
    val xpIntoLevel: Int = remember(xp) { xp % 50 }
    val levelProgress: Float = remember(xp) {
        if (xpIntoLevel == 0 && xp == 0) 0f else xpIntoLevel / 50f
    }

    val hasFirstExpense = xp >= 5
    val hasThreeDayStreak = streak >= 3
    val hasSevenDayStreak = streak >= 7

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Progress",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Level: $level")
        Text(text = "XP: $xp")

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { levelProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${xpIntoLevel}/50 XP to next level",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Streak: $streak day(s) in a row")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Badges",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (!hasFirstExpense && !hasThreeDayStreak && !hasSevenDayStreak) {
            Text(text = "- No badges yet. Log some expenses!")
        } else {
            if (hasFirstExpense) {
                Text(text = "First Expense Logged")
            }
            if (hasThreeDayStreak) {
                Text(text = "3-Day Streak")
            }
            if (hasSevenDayStreak) {
                Text(text = "7-Day Streak")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Spending & Budget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "This month's spending: €${"%.2f".format(monthTotal)}")

        Spacer(modifier = Modifier.height(8.dp))

        if (monthlyBudget != null) {
            Text(text = "Monthly budget: €${"%.2f".format(monthlyBudget)}")
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}% of budget used",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(text = "No budget set for this month.")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set / Update Monthly Budget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = budgetInput,
            onValueChange = {
                budgetInput = it
                errorText = null
            },
            label = { Text("Budget amount (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = errorText ?: "", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val success = viewModel.setMonthlyBudget(budgetInput)
                if (!success) {
                    errorText = "Please enter a valid budget amount"
                } else {
                    Toast.makeText(context, "Budget saved", Toast.LENGTH_SHORT).show()
                    budgetInput = ""
                }
            }
        ) {
            Text("Save Budget")
        }
        Button(
            onClick = { viewModel.clearAllData() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Clear All Expenses")
        }

    }
}
