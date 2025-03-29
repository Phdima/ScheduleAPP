package com.example.scheduleapp.di

import androidx.work.WorkManager
import com.example.scheduleapp.data.notifWork.ScheduleRepositoryImpl
import com.example.scheduleapp.data.room.dao.ScheduleDao
import com.example.scheduleapp.domain.repository.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
