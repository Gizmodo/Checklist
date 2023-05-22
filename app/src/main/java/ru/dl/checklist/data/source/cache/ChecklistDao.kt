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
        "SELECT checklist.id, checklist.uuid, checklist.address, checklist.audit_date as auditDate, checklist.checker, checklist.senior, checklist.short_name as shortName,\n" +
                "       round(ifnull(sum(points * answer), 0) / sum(CAST (points AS REAL) ), 4) * 100 AS percent\n" +
                "  FROM checklist\n" +
                "       JOIN\n" +
                "       zone ON zone.checklistId = checklist.id\n" +
                "       JOIN\n" +
                "       mark ON mark.zoneId = zone.id\n" +
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