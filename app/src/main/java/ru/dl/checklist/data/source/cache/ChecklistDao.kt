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
                "       round(avg(tbl.percent), 2) AS percent\n" +
                "  FROM checklist\n" +
                "       JOIN\n" +
                "       (\n" +
                "           SELECT zone.*,\n" +
                "                  round(ifnull(sum(answer * points / 10), 0) / sum(CAST (points AS REAL) ), 4) * 100 AS percent\n" +
                "             FROM zone\n" +
                "                  LEFT JOIN\n" +
                "                  mark ON mark.zoneId = zone.id\n" +
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