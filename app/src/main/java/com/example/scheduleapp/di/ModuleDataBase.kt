package com.example.scheduleapp.di

import android.content.Context
import androidx.room.Room
import com.example.scheduleapp.data.room.dao.ScheduleDao
import com.example.scheduleapp.data.room.dataBase.AppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "schedule.db"
        ).build()
    }

    @Provides
    fun provideScheduleDao(database: AppDataBase): ScheduleDao {
        return database.scheduleDao()
    }
}