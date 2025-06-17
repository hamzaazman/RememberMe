package com.hamzaazman.rememberme.ui.add

import com.hamzaazman.rememberme.data.model.ReminderPriority

data class AddUiState(
    val reminderId: Int = 0, // 0 yeni hatırlatma, >0 düzenleme
    val title: String = "",
    val description: String = "",
    val dateTimeMillis: Long? = null, // Seçilen tarih/saatin epoch mili saniye karşılığı
    val selectedPriority: ReminderPriority = ReminderPriority.LOW,
    val isCompleted: Boolean = false, // Düzenleme durumunda kullanılacak
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)