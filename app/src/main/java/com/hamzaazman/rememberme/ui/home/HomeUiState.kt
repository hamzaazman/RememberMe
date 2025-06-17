package com.hamzaazman.rememberme.ui.home

import com.hamzaazman.rememberme.data.model.Reminder

data class HomeUiState(
    val isLoading: Boolean = false,
    val reminders: List<Reminder> = emptyList(),
    val error: String? = null,
)