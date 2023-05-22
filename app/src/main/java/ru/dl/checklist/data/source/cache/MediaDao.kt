package ru.dl.checklist.data.source.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.data.model.entity.MediaEntity

@Dao
interface MediaDao {

    @Query("select * from media where media.markId = :markId")
    fun getMediaByMarkId(markId: Long): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaEntity: MediaEntity): Long

    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun getById(id: Long): MediaEntity?

    @Query("SELECT * FROM media WHERE markId = :markId")
    suspend fun getByMarkId(markId: Long): List<MediaEntity>

    @Delete
    suspend fun delete(mediaEntity: MediaEntity)

    @Query("DELETE FROM media")
    suspend fun deleteAll()
}