package com.example.budgetapp.gamification

import android.content.Context
import java.time.LocalDate
import androidx.core.content.edit

class GamificationManager(context: Context) {

    private val prefs = context.getSharedPreferences("gamification_prefs", Context.MODE_PRIVATE)

    var xp: Int
        get() = prefs.getInt("xp", 0)
        private set(value) {
            prefs.edit { putInt("xp", value) }
        }

    var currentStreak: Int
        get() = prefs.getInt("current_streak", 0)
        private set(value) {
            prefs.edit { putInt("current_streak", value) }
        }

    private var lastLogEpochDay: Long
        get() = prefs.getLong("last_log_epoch_day", -1L)
        set(value) {
            prefs.edit { putLong("last_log_epoch_day", value) }
        }

    fun onExpenseLogged(today: LocalDate = LocalDate.now()) {
        val todayEpoch = today.toEpochDay()
        val last = lastLogEpochDay

        when {
            last == -1L -> {
                currentStreak = 1
            }
            todayEpoch == last -> {
            }
            todayEpoch == last + 1 -> {
                currentStreak = currentStreak + 1
            }
            else -> {
                currentStreak = 1
            }
        }

        lastLogEpochDay = todayEpoch
        addXp(5)
    }

    private fun addXp(amount: Int) {
        xp = xp + amount
    }
}
