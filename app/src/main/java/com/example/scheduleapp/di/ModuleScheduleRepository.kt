package com.example.scheduleapp.di

import androidx.work.WorkManager
import com.example.scheduleapp.data.notifWork.ClockProvider
import com.example.scheduleapp.data.notifWork.ScheduleRepositoryImpl
import com.example.scheduleapp.data.notifWork.SystemClockProvider
import com.example.scheduleapp.data.room.dao.ScheduleDao
import com.example.scheduleapp.domain.repository.ScheduleRepository
import com.google.android.datatransport.runtime.time.TestClock
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl
    ): ScheduleRepository
}
@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    fun provideClockProvider(): ClockProvider = SystemClockProvider()
}

