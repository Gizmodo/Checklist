package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.data.model.entity.MarkEntity

@Dao
interface MarkDao {
    @Query("SELECT * FROM mark WHERE zoneId = :zoneId")
    fun getAllByZoneId(zoneId: Long): List<MarkEntity>

    @Query("SELECT * FROM mark WHERE id = :id")
    fun getById(id: Long): MarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(mark: MarkEntity): Long

    @Update
    fun update(mark: MarkEntity)

    @Delete
    fun delete(mark: MarkEntity)

    @Query(
        "select mark.* from mark\n" +
                "inner join zone on zone.id=mark.zoneId\n" +
                "where zone.id=:zoneId"
    )
    fun getMarkListByZone(zoneId: Long): Flow<List<MarkEntity>>
}
