package ru.dl.checklist.data.source.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        InvItem::class,
    ],
    version = 1,
    exportSchema = true,

    )
/*@TypeConverters(
    LocalDateConverter::class,
    LocalDateTimeConverter::class
)*/
abstract class AppDatabase : RoomDatabase() {
    abstract fun checklistDAO(): ChecklistDAO

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
