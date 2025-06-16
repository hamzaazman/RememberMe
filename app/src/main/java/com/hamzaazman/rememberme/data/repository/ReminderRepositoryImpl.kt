package com.hamzaazman.rememberme.data.repository

import com.hamzaazman.rememberme.data.model.Reminder
import com.hamzaazman.rememberme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(

) : ReminderRepository {
    override fun getAllReminders(): Flow<List<Reminder>> {
        TODO("Not yet implemented")
    }

    override fun getActiveReminders(): Flow<List<Reminder>> {
        TODO("Not yet implemented")
    }

    override fun getCompletedReminders(): Flow<List<Reminder>> {
        TODO("Not yet implemented")
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        TODO("Not yet implemented")
    }

    override suspend fun insertReminder(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminder(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminderCompletion(
        reminder: Reminder,
        isCompleted: Boolean
    ) {
        TODO("Not yet implemented")
    }
}