package ru.dl.checklist.app.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.dl.checklist.data.repository.CheckListRepositoryImpl
import ru.dl.checklist.data.source.cache.AppDatabase
import ru.dl.checklist.data.source.cache.ChecklistDao
import ru.dl.checklist.data.source.cache.MarkDao
import ru.dl.checklist.data.source.cache.MediaDao
import ru.dl.checklist.data.source.cache.ZoneDao
import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Singleton


@Module
class DataBaseModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideChecklistDao(database: AppDatabase): ChecklistDao = database.checklistDao()

    @Singleton
    @Provides
    fun provideZoneDao(database: AppDatabase): ZoneDao = database.zoneDao()

    @Singleton
    @Provides
    fun provideMarkDao(database: AppDatabase): MarkDao = database.markDao()

    @Singleton
    @Provides
    fun provideMediaDao(database: AppDatabase): MediaDao = database.mediaDao()

    @Provides
    @Singleton
    fun providesDatabaseRepository1(repository: CheckListRepositoryImpl): CheckListRepository =
        repository

    @Provides
    @Singleton
    fun providesApplicationContext(): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideDBInstance(context: Context): AppDatabase = AppDatabase.getDatabase(context)
}