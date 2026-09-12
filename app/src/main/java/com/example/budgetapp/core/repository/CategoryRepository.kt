package com.example.budgetapp.core.repository

import com.example.budgetapp.data.local.AppDatabase
import com.example.budgetapp.data.local.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val db: AppDatabase
) {

    private val categoryDao = db.categoryDao()

    fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategories()

    suspend fun addCategory(name: String, isDefault: Boolean = false) {
        val category = CategoryEntity(
            name = name,
            isDefault = isDefault
        )
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun ensureDefaultCategories() {
        val count = categoryDao.getCategoryCount()
        if (count == 0) {
            val defaults = listOf(
                "Food",
                "Transport",
                "Bills",
                "Entertainment",
                "Shopping",
                "Other"
            )
            defaults.forEach { name ->
                addCategory(name, isDefault = true)
            }
        }
    }
}
