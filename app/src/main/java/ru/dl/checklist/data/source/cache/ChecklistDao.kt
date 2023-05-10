package ru.dl.checklist.data.source.cache

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.dl.checklist.data.model.entity.ChecklistEntity

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist")
    fun getAll(): List<ChecklistEntity>

    @Query("SELECT * FROM checklist WHERE id = :id")
    fun getById(id: Long): ChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(checklist: ChecklistEntity): Long

    @Delete
    fun delete(checklist: ChecklistEntity)
}