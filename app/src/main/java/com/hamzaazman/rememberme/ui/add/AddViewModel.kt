package com.hamzaazman.rememberme.ui.add

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamzaazman.rememberme.data.model.Reminder
import com.hamzaazman.rememberme.data.model.ReminderPriority
import com.hamzaazman.rememberme.di.ApplicationScope
import com.hamzaazman.rememberme.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AddViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    private val reminderId: Int = savedStateHandle.get<Int>("reminderId") ?: 0

    init {
        if (reminderId != 0) {
            loadReminder(reminderId)
        } else {
            val now = LocalDateTime.now()
            _uiState.update { it.copy(dateTimeMillis = now.toEpochSecond(ZoneOffset.UTC) * 1000) }
        }
    }

    private fun loadReminder(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val reminder = reminderRepository.getReminderById(id)
                if (reminder != null) {
                    _uiState.update {
                        it.copy(
                            reminderId = reminder.id,
                            title = reminder.title,
                            description = reminder.description ?: "",
                            dateTimeMillis = reminder.dateTime,
                            selectedPriority = reminder.priority,
                            isCompleted = reminder.isCompleted,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Reminder not found") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load reminder: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun updateDateTime(newMillis: Long) {
        _uiState.update { it.copy(dateTimeMillis = newMillis) }
    }

    fun updatePriority(newPriority: ReminderPriority) {
        _uiState.update { it.copy(selectedPriority = newPriority) }
    }

    fun getFormattedDateTime(millis: Long?): String {
        return millis?.let {
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        } ?: ""
    }

    fun saveReminder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSaved = false) }

            val currentUiState = _uiState.value
            if (currentUiState.title.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Title cannot be empty") }
                return@launch
            }
            if (currentUiState.dateTimeMillis == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Date and time cannot be empty"
                    )
                }
                return@launch
            }

            try {
                val reminderToSave = Reminder(
                    id = currentUiState.reminderId,
                    title = currentUiState.title,
                    description = currentUiState.description,
                    dateTime = currentUiState.dateTimeMillis,
                    priority = currentUiState.selectedPriority,
                    isCompleted = currentUiState.isCompleted
                )

                if (currentUiState.reminderId == 0) {
                    reminderRepository.insertReminder(reminderToSave)
                    scheduleReminder(reminderToSave)
                } else {
                    reminderRepository.updateReminder(reminderToSave)
                    cancelReminder(reminderToSave)
                    scheduleReminder(reminderToSave)
                }

                _uiState.update { it.copy(isLoading = false, error = null, isSaved = true) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save reminder: ${e.message}",
                        isSaved = false
                    )
                }
            }
        }
    }


    fun getFormattedDate(millis: Long?): String {
        return millis?.let {
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } ?: ""
    }

    fun getFormattedTime(millis: Long?): String {
        return millis?.let {
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } ?: ""
    }

    fun resetUiStateFlags() {
        _uiState.update { it.copy(error = null, isSaved = false) }
    }


    private fun scheduleReminder(reminder: Reminder) { /* AlarmManager kodu buraya gelecek */
    }

    private fun cancelReminder(reminder: Reminder) { /* AlarmManager iptal kodu buraya gelecek */
    }
}