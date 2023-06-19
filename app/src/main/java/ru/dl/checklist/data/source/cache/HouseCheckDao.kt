package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.data.model.entity.HouseCheckEntity
import ru.dl.checklist.domain.model.HouseCheckDomain

@Dao
interface HouseCheckDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Transaction
    suspend fun insert(check: HouseCheckEntity): Long

    @Query("select house_check.* from house_check inner join house_checklist on house_checklist.id=house_check.checklistId where house_checklist.uuid = :uuid")
    fun getHouseChecksByUUIDOld(uuid: String): Flow<List<HouseCheckEntity>>

    @Query(
        "SELECT hc.*," +
                "count(hm.id) as mediacount" +
                "  FROM house_check AS hc" +
                "       left JOIN" +
                "       house_media AS hm ON hc.id = hm.houseCheckId" +
                "       inner join house_checklist hcl on hcl.id=hc.checklistId" +
                "       where hcl.uuid= :uuid" +
                "       group by hc.id"
    )
    fun getHouseChecksByUUID(uuid: String): Flow<List<HouseCheckDomain>>

    @Query("UPDATE house_check SET answer = :answer WHERE id = :houseCheckId")
    fun updateCheck(houseCheckId: Long, answer: Boolean)
}