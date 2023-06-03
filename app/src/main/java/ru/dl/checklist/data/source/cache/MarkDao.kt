package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.data.model.remote.ReadyMark
import ru.dl.checklist.data.model.remote.ReadyMedia
import ru.dl.checklist.domain.model.MarkDomainWithCount

@Dao
interface MarkDao {
    @Query("SELECT * FROM mark WHERE zoneId = :zoneId")
    fun getAllByZoneId(zoneId: Long): List<MarkEntity>

    @Query("SELECT * FROM mark WHERE id = :id")
    fun getById(id: Long): MarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(mark: MarkEntity): Long

    @Query(
        "select mark.* from mark\n" +
                "inner join zone on zone.id=mark.zoneId\n" +
                "where zone.id=:zoneId"
    )
    fun getMarkListByZone(zoneId: Long): Flow<List<MarkEntity>>

    @Query("UPDATE mark SET comment = :comment,answer = :answer, pkd = :pkd WHERE id = :markId")
    fun updateMark(markId: Long, comment: String, answer: Float, pkd: String)

    @Query(
        "SELECT mark.*,\n" +
                "       count(media.id) as count\n" +
                "  FROM mark\n" +
                "       LEFT JOIN\n" +
                "       media ON mark.id = media.markId\n" +
                " WHERE mark.zoneId = :zoneId\n" +
                " GROUP BY mark.id"
    )
    fun getMarkListByZoneWithCount(zoneId: Long): Flow<List<MarkDomainWithCount>>

    @Query(
        "SELECT mark.id,\n" +
                "       mark.points,\n" +
                "       mark.answer,\n" +
                "       mark.comment,\n" +
                "       mark.pkd\n" +
                "  FROM mark\n" +
                "       JOIN\n" +
                "       zone ON mark.zoneId = zone.id\n" +
                "       JOIN\n" +
                "       checklist ON zone.checklistId = checklist.id\n" +
                " WHERE checklist.uuid = :uuid"
    )
    suspend fun getMarkListByChecklist(uuid: String): List<ReadyMark>

    @Query(
        "SELECT mark.id AS uuid,\n" +
                "       media.media\n" +
                "  FROM mark\n" +
                "       JOIN\n" +
                "       media ON media.markId = mark.id\n" +
                "       JOIN\n" +
                "       zone ON mark.zoneId = zone.id\n" +
                "       JOIN\n" +
                "       checklist ON zone.checklistId = checklist.id\n" +
                " WHERE checklist.uuid = :uuid"
    )
    suspend fun getMediaListByChecklist(uuid: String): List<ReadyMedia>
}
