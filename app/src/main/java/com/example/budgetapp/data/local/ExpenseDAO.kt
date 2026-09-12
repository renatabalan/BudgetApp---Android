package com.example.budgetapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY dateMillis DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE dateMillis BETWEEN :from AND :to
        ORDER BY dateMillis DESC
    """)
    fun getExpensesBetween(from: Long, to: Long): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE dateMillis BETWEEN :from AND :to
    """)
    fun getTotalBetween(from: Long, to: Long): Flow<Double?>

    @Query("""
        SELECT c.name AS categoryName, SUM(e.amount) AS total
        FROM expenses e 
        JOIN categories c ON e.categoryId = c.id
        WHERE e.dateMillis BETWEEN :from AND :to
        GROUP BY e.categoryId
    """)
    fun getTotalsPerCategoryBetween(from: Long, to: Long): Flow<List<CategoryTotal>>
    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    data class CategoryTotal(
        val categoryName: String,
        val total: Double
    )
}
