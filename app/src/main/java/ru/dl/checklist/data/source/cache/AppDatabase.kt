package ru.dl.checklist.data.source.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


abstract class AppDatabase : RoomDatabase() {
    abstract fun protocolDao(): ProtocolDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "emias.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
