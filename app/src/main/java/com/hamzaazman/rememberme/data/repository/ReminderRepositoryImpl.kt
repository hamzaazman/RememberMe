package com.hamzaazman.rememberme.data.repository

import com.hamzaazman.rememberme.data.model.Reminder
import com.hamzaazman.rememberme.data.source.local.ReminderDao
import com.hamzaazman.rememberme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders()
    }

    override fun getActiveReminders(): Flow<List<Reminder>> {
        return reminderDao.getActiveReminders()
    }

    override fun getCompletedReminders(): Flow<List<Reminder>> {
        return reminderDao.getCompletedReminders()
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return reminderDao.getReminderById(id)
    }

    override suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    override suspend fun updateReminderCompletion(reminder: Reminder, isCompleted: Boolean) {
        reminderDao.updateReminder(reminder.copy(isCompleted = isCompleted))
    }
}