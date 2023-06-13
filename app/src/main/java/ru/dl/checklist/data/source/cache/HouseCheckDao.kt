package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import ru.dl.checklist.data.model.entity.HouseCheckEntity

@Dao
interface HouseCheckDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Transaction
    suspend fun insert(check: HouseCheckEntity): Long
}