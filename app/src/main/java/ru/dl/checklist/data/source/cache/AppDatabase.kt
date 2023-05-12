package ru.dl.checklist.data.source.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.data.model.entity.ZoneEntity

@Database(
    entities = [
        ChecklistEntity::class,
        ZoneEntity::class,
        MarkEntity::class
    ],
    version = 2,
    exportSchema = true,
)
/*@TypeConverters(
    LocalDateConverter::class,
    LocalDateTimeConverter::class
)*/
abstract class AppDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao
    abstract fun zoneDao(): ZoneDao
    abstract fun markDao(): MarkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
