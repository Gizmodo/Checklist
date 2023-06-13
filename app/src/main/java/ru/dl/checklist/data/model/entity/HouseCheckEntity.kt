package ru.dl.checklist.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "house_check", foreignKeys = [
        ForeignKey(
            entity = HouseChecklistEntity::class,
            parentColumns = ["id"],
            childColumns = ["checklistId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["checklistId"])
    ]
)
data class HouseCheckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "uuid") val uuid: String,
    @ColumnInfo(name = "checklistId") val checklistId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "isPhotoRequired") val isPhotoRequired: Boolean,
    @ColumnInfo(name = "next_check_uuid") val nextCheckUUID: String?,
    @ColumnInfo(name = "isStart") val isStart: Boolean,
    @ColumnInfo(name = "isEnd") val isEnd: Boolean,
)