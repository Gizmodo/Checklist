package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.dl.checklist.data.model.entity.ZoneEntity

@Dao
interface ZoneDao {
    @Query("SELECT * FROM zone WHERE checklistId = :checklistId")
    fun getAllByChecklistId(checklistId: Long): List<ZoneEntity>

    @Query("SELECT * FROM zone WHERE id = :id")
    fun getById(id: Long): ZoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(zone: ZoneEntity): Long

    @Update
    fun update(zone: ZoneEntity)

    @Delete
    fun delete(zone: ZoneEntity)
}
