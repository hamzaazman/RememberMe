package com.hamzaazman.rememberme.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hamzaazman.rememberme.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    // Tüm hatırlatmaları ID'ye göre azalan sırada (en yeniler en üstte) ve LiveData olarak al.
    // LiveData, veritabanındaki değişiklikleri otomatik olarak gözlemlememizi sağlar.
    @Query("SELECT * FROM reminders ORDER BY dateTime ASC") // Tarihe göre artan sıralama, en yakın tarihli olanlar üstte
    fun getAllReminders(): Flow<List<Reminder>> // Flow kullanarak Coroutines ile reaktif veri akışı sağlayalım

    // Tamamlanmamış hatırlatmaları al (Ana liste için daha uygun olabilir)
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY dateTime ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    // Tamamlanmış hatırlatmaları al (Farklı bir görünüm veya arşiv için)
    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY dateTime ASC")
    fun getCompletedReminders(): Flow<List<Reminder>>

    // Belirli bir ID'ye sahip hatırlatmayı al
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Int): Reminder?
}