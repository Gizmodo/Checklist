package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.domain.model.ChecklistDomain

@Dao
interface ChecklistDao {
    @Query(
       "SELECT checklist.id,\n" +
               "       checklist.uuid,\n" +
               "       checklist.address,\n" +
               "       checklist.audit_date AS auditDate,\n" +
               "       checklist.checker,\n" +
               "       checklist.senior,\n" +
               "       checklist.short_name AS shortName,\n" +
               "       checklist.title AS title,\n" +
               "       ROUND(AVG(tbl.percent), 2) AS percent\n" +
               "  FROM checklist\n" +
               "       JOIN\n" +
               "       (\n" +
               "           SELECT zone.id,\n" +
               "                  zone.checklistId,\n" +
               "                  zone.zone,\n" +
               "                  mark2.percent\n" +
               "             FROM zone\n" +
               "                  LEFT JOIN\n" +
               "                  (\n" +
               "                      SELECT zoneid,\n" +
               "                             CASE WHEN mainflag = 0 THEN calc ELSE 0 END AS percent\n" +
               "                        FROM (\n" +
               "                                 SELECT zoneid,\n" +
               "                                        ROUND(SUM(points * (answer / 10.0) ) / SUM(points) * 100, 2) AS calc,\n" +
               "                                        MAX(CASE WHEN ( (answer = 10 AND \n" +
               "                                                         flag = 1) OR \n" +
               "                                                        (flag = 0) ) THEN 0 ELSE 1 END) AS mainflag\n" +
               "                                   FROM mark\n" +
               "                                  GROUP BY zoneid\n" +
               "                             )\n" +
               "                             AS calc\n" +
               "                  )\n" +
               "                  AS mark2 ON mark2.zoneId = zone.id\n" +
               "            GROUP BY zone.id\n" +
               "       )\n" +
               "       AS tbl ON tbl.checklistId = checklist.id\n" +
               " GROUP BY checklist.id"
    )
    suspend fun getAll(): List<ChecklistDomain>

    @Query("SELECT * FROM checklist WHERE uuid = :uuid")
    fun getByUUID(uuid: String): ChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Transaction
    suspend fun insert(checklist: ChecklistEntity): Long

    @Delete
    @Transaction
    fun delete(checklist: ChecklistEntity)
}