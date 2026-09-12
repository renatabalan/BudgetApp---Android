package com.example.budgetapp.core.repository

import com.example.budgetapp.data.local.AppDatabase
import com.example.budgetapp.data.local.MonthlyBudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(
    private val db: AppDatabase
) {

    private val budgetDao = db.monthlyBudgetDao()

    suspend fun setMonthlyBudget(yearMonth: String, amount: Double) {
        val budget = MonthlyBudgetEntity(
            yearMonth = yearMonth,
            amount = amount
        )
        budgetDao.upsertBudget(budget)
    }

    fun getBudgetForMonth(yearMonth: String): Flow<MonthlyBudgetEntity?> {
        return budgetDao.getBudgetForMonth(yearMonth)
    }
}
