package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.domain.model.Answer

@Dao
interface MarkDao {
    @Query("SELECT * FROM mark WHERE zoneId = :zoneId")
    fun getAllByZoneId(zoneId: Long): List<MarkEntity>

    @Query("SELECT * FROM mark WHERE id = :id")
    fun getById(id: Long): MarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(mark: MarkEntity): Long

    @Delete
    fun delete(mark: MarkEntity)

    @Query(
        "select mark.* from mark\n" +
                "inner join zone on zone.id=mark.zoneId\n" +
                "where zone.id=:zoneId"
    )
    fun getMarkListByZone(zoneId: Long): Flow<List<MarkEntity>>

    @Query("UPDATE mark SET answer = :answer WHERE id = :markId")
    fun updateMarkAnswer(markId: Long, answer: Answer)

    @Query("UPDATE mark SET comment = :comment WHERE id = :markId")
    fun updateMarkComment(markId: Long, comment: String)
}
