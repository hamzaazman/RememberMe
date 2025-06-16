package com.hamzaazman.rememberme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class ReminderPriority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val dateTime: Long,
    val priority: ReminderPriority = ReminderPriority.LOW,
    val isCompleted: Boolean = false,
)