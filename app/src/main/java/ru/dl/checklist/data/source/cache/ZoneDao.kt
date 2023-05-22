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
        "SELECT zone.id, zone.zone, round(ifnull(sum(points * answer), 0) / sum(CAST (points AS REAL) ), 4) * 100 AS percent FROM zone\n" +
                "       LEFT JOIN\n" +
                "       mark ON mark.zoneId = zone.id\n" +
                "       left join \n" +
                "       checklist on checklist.id=zone.checklistId\n" +
                " WHERE checklist.uuid=:uuid GROUP BY zone.id"
    )
    fun getZoneListByChecklist(uuid: String): Flow<List<ZoneDomain>>
}
