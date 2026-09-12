package com.example.budgetapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budgetapp.gamification.GamificationManager
import com.example.budgetapp.core.repository.BudgetRepository
import com.example.budgetapp.core.repository.CategoryRepository
import com.example.budgetapp.core.repository.ExpenseRepository
import com.example.budgetapp.data.local.CategoryEntity
import com.example.budgetapp.data.local.ExpenseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AppViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val gamificationManager: GamificationManager
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    private val _monthlyBudget = MutableStateFlow<Double?>(null)
    val monthlyBudget: StateFlow<Double?> = _monthlyBudget

    private val _currentMonthTotal = MutableStateFlow(0.0)
    val currentMonthTotal: StateFlow<Double> = _currentMonthTotal

    private val _xp = MutableStateFlow(0)
    val xp: StateFlow<Int> = _xp

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak

    private val currentYearMonth: String = currentYearMonthString()

    init {
        _xp.value = gamificationManager.xp
        _streak.value = gamificationManager.currentStreak

        viewModelScope.launch {
            categoryRepository.ensureDefaultCategories()
            categoryRepository.getAllCategories().collect { list ->
                _categories.value = list
            }
        }

        viewModelScope.launch {
            expenseRepository.getAllExpenses().collect { list ->
                _expenses.value = list
                _currentMonthTotal.value = calculateCurrentMonthTotal(list)
            }
        }

        viewModelScope.launch {
            budgetRepository.getBudgetForMonth(currentYearMonth).collect { budgetEntity ->
                _monthlyBudget.value = budgetEntity?.amount
            }
        }
    }
    fun addCategory(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return false

        viewModelScope.launch {
            categoryRepository.addCategory(trimmed, isDefault = false)
        }
        return true
    }

    fun deleteCategoryById(id: Long) {
        viewModelScope.launch {
            val cat = categories.value.firstOrNull { it.id == id }
            if (cat != null && !cat.isDefault) {
                categoryRepository.deleteCategory(cat)
            }
        }
    }


    fun addExpense(
        amountText: String,
        note: String?,
        categoryId: Long?
    ): Boolean {
        val amount = amountText.toDoubleOrNull() ?: return false
        if (categoryId == null) return false

        viewModelScope.launch {
            expenseRepository.addExpense(
                amount = amount,
                categoryId = categoryId,
                dateMillis = System.currentTimeMillis(),
                note = note
            )

            gamificationManager.onExpenseLogged()
            _xp.value = gamificationManager.xp
            _streak.value = gamificationManager.currentStreak
        }

        return true
    }

    fun setMonthlyBudget(amountText: String): Boolean {
        val amount = amountText.toDoubleOrNull() ?: return false

        viewModelScope.launch {
            budgetRepository.setMonthlyBudget(currentYearMonth, amount)
        }

        return true
    }

    fun getCategoryNameFor(id: Long): String {
        val cats = categories.value
        val cat = cats.firstOrNull { it.id == id }
        return cat?.name ?: "Unknown"
    }


    private fun calculateCurrentMonthTotal(expenses: List<ExpenseEntity>): Double {
        val now = LocalDate.now()
        return expenses
            .filter { isInMonth(it.dateMillis, now.year, now.monthValue) }
            .sumOf { it.amount }
    }

    private fun isInMonth(millis: Long, year: Int, month: Int): Boolean {
        val date = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return date.year == year && date.monthValue == month
    }
    fun clearAllData() {
        viewModelScope.launch {
            expenseRepository.deleteAllExpenses()
        }
    }


    private fun currentYearMonthString(): String {
        val now = LocalDate.now()
        val monthString = if (now.monthValue < 10) "0${now.monthValue}" else "${now.monthValue}"
        return "${now.year}-$monthString" // e.g. 2025-12
    }
}

class AppViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val gamificationManager: GamificationManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                expenseRepository,
                categoryRepository,
                budgetRepository,
                gamificationManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
