package ru.dl.checklist.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "house_checklist")
data class HouseChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "uuid") val uuid: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "time_start") val timeStart: String,
    @ColumnInfo(name = "time_end") val timeEnd: String,
)
