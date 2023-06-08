package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.domain.model.ZoneDomain

@Dao
interface ZoneDao {
    @Query("SELECT * FROM zone WHERE checklistId = :checklistId")
    fun getAllByChecklistId(checklistId: Long): List<ZoneEntity>

    @Query("SELECT * FROM zone WHERE id = :id")
    fun getById(id: Long): ZoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(zone: ZoneEntity): Long

    @Update
    fun update(zone: ZoneEntity)

    @Delete
    fun delete(zone: ZoneEntity)

    @Query(
        "SELECT zone.id,\n" +
                "       zone.zone,\n" +
                "       mark2.percent\n" +
                "  FROM zone\n" +
                "       LEFT JOIN\n" +
                "       (\n" +
                "           SELECT zoneid,\n" +
                "                  CASE WHEN mainflag = 0 THEN calc ELSE 0 END AS percent\n" +
                "             FROM (\n" +
                "                      SELECT zoneid,\n" +
                "                             round(sum(points * (answer / 10.0) ) / sum(points) * 100, 2) AS calc,\n" +
                "                            max(CASE WHEN ( (answer = 10 AND \n" +
                "                                   flag = 1) OR \n" +
                "                                  (flag = 0) ) THEN 0 ELSE 1 END) AS mainflag\n" +
                "                        FROM mark\n" +
                "                       GROUP BY zoneid\n" +
                "                  )\n" +
                "                  AS calc\n" +
                "       )\n" +
                "       AS mark2 ON mark2.zoneId = zone.id\n" +
                "       LEFT JOIN\n" +
                "       checklist ON checklist.id = zone.checklistId\n" +
                " WHERE checklist.uuid = :uuid\n" +
                " GROUP BY zone.id"
    )
    fun getZoneListByChecklist(uuid: String): Flow<List<ZoneDomain>>
}
