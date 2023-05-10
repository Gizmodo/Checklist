package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.dl.checklist.data.model.entity.ChecklistEntity

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist")
    fun getAll(): List<ChecklistEntity>

    @Query("SELECT * FROM checklist WHERE uuid = :uuid")
    fun getByUUID(uuid: String): ChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(checklist: ChecklistEntity): Long

    @Delete
    @Transaction
    fun delete(checklist: ChecklistEntity)
}