package ru.dl.checklist.app.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.dl.checklist.data.source.cache.AppDatabase
import ru.dl.checklist.data.source.cache.ProtocolDao
import ru.dl.checklist.data.source.cache.ProtocolsRepository
import ru.dl.checklist.data.source.cache.ProtocolsRepositoryImpl
import javax.inject.Singleton


@Module
class DataBaseModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideProtocolDao(database: AppDatabase): ProtocolDao = database.protocolDao()

    @Provides
    @Singleton
    fun providesDatabaseRepository1(repository: ProtocolsRepositoryImpl): ProtocolsRepository =
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