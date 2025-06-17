package com.hamzaazman.rememberme.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamzaazman.rememberme.data.model.Reminder
import com.hamzaazman.rememberme.di.ApplicationScope
import com.hamzaazman.rememberme.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    init {
        // ViewModel başlatıldığında tüm hatırlatmaları yükle
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            // Yükleme başladığında UI durumunu güncelle
            _uiState.update { it.copy(isLoading = true, error = null) }
            reminderRepository.getAllReminders()
                .catch { e ->
                    // Akışta bir hata oluşursa yakala ve UI durumunu güncelle
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = "Failed to load reminders: ${e.message}"
                        )
                    }
                }
                .collect { remindersList ->
                    // Veritabanından gelen yeni hatırlatma listesiyle UI durumunu güncelle
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            reminders = remindersList,
                            error = null // Başarılı yüklemede hata mesajını temizle
                        )
                    }
                }
        }
    }

    // Yeni bir hatırlatma ekleme işlemi
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                reminderRepository.insertReminder(reminder)
                // Flow otomatik güncelleneceği için _uiState.reminders listesini manuel değiştirmeye gerek yok.
                // Sadece hata ve yükleme durumunu resetle
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to add reminder: ${e.message}"
                    )
                }
            }
        }
    }

    // Bir hatırlatmayı tamamlama durumunu değiştirme
    fun updateReminderCompletion(reminder: Reminder, isCompleted: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                reminderRepository.updateReminderCompletion(reminder, isCompleted)
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to update reminder status: ${e.message}"
                    )
                }
            }
        }
    }

    // Bir hatırlatmayı silme
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                reminderRepository.deleteReminder(reminder)
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to delete reminder: ${e.message}"
                    )
                }
            }
        }
    }

    // Belirli bir hatırlatmayı ID'ye göre alma (düzenleme ekranı için)
    // Bu fonksiyon UI state'i değiştirmez, sadece veri döndürür.
    suspend fun getReminderById(id: Int): Reminder? {
        // isLoading veya error durumlarını burada güncellemeye gerek yok çünkü bu tekil bir veri alımıdır
        // ve bir liste yüklemesi gibi ana UI durumunu etkilemez.
        return reminderRepository.getReminderById(id)
    }

    // UI'daki bir hata mesajını temizlemek için
    fun clearErrorMessage() {
        _uiState.update { it.copy(error = null) }
    }

    // AlarmManager ile hatırlatmayı zamanlamak için fonksiyonlar
    fun scheduleReminder(reminder: Reminder) {
        applicationScope.launch {
            println("Hatırlatma zamanlandı: ${reminder.title} - ${java.util.Date(reminder.dateTime)}")
            // TODO: AlarmManager entegrasyonu burada yapılacak
        }
    }

    fun cancelReminder(reminder: Reminder) {
        applicationScope.launch {
            println("Hatırlatma iptal edildi: ${reminder.title}")
            // TODO: AlarmManager iptal entegrasyonu burada yapılacak
        }
    }
}