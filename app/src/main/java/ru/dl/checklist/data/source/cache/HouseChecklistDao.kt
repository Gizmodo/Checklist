package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.dl.checklist.data.model.entity.HouseChecklistEntity

@Dao
interface HouseChecklistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Transaction
    suspend fun insert(checklist: HouseChecklistEntity): Long

    @Query("SELECT * FROM house_checklist where uuid = :uuid")
    fun getByUUID(uuid: String): HouseChecklistEntity?

    @Query("select * from house_checklist")
    suspend fun getAll(): List<HouseChecklistEntity>
}