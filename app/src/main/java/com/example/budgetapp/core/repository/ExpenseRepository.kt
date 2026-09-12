package com.example.budgetapp.core.repository

import com.example.budgetapp.data.local.AppDatabase
import com.example.budgetapp.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val db: AppDatabase
) {

    private val expenseDao = db.expenseDao()
    suspend fun deleteAllExpenses() = db.expenseDao().deleteAllExpenses()


    suspend fun addExpense(
        amount: Double,
        categoryId: Long,
        dateMillis: Long,
        note: String?
    ) {
        val expense = ExpenseEntity(
            amount = amount,
            categoryId = categoryId,
            dateMillis = dateMillis,
            note = note
        )
        expenseDao.insertExpense(expense)
    }

    fun getAllExpenses(): Flow<List<ExpenseEntity>> =
        expenseDao.getAllExpenses()
}
