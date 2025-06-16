package com.hamzaazman.rememberme.domain.repository

import com.hamzaazman.rememberme.data.model.Reminder
import kotlinx.coroutines.flow.Flow


interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getActiveReminders(): Flow<List<Reminder>>
    fun getCompletedReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(id: Int): Reminder?
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun updateReminderCompletion(reminder: Reminder, isCompleted: Boolean)
}