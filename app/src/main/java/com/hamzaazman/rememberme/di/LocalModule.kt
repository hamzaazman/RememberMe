package com.hamzaazman.rememberme.di

import android.content.Context
import androidx.room.Room
import com.hamzaazman.rememberme.data.source.local.ReminderDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context,
    ) = Room.databaseBuilder(
        app,
        ReminderDatabase::class.java,
        "reminder_database"
    )
        .fallbackToDestructiveMigration(false)
        .build()

    @Provides
    @Singleton
    fun provideReminderDao(db: ReminderDatabase) = db.reminderDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}