package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ru.dl.checklist.data.model.entity.HouseMediaEntity

@Dao
interface HouseMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(houseMediaEntity: HouseMediaEntity): Long
}