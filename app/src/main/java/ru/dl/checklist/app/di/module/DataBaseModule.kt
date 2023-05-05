package ru.dl.checklist.app.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.dl.checklist.data.source.cache.AppDatabase
import ru.dl.checklist.data.source.cache.CheckListRepository
import ru.dl.checklist.data.source.cache.CheckListRepositoryImpl
import ru.dl.checklist.data.source.cache.ChecklistDAO
import javax.inject.Singleton


@Module
class DataBaseModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideProtocolDao(database: AppDatabase): ChecklistDAO = database.checklistDAO()

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