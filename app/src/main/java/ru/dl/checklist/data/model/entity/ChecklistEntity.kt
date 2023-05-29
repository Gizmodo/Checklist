package ru.dl.checklist.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "uuid") val uuid: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "audit_date") val auditDate: String,
    @ColumnInfo(name = "checker") val checker: String,
    @ColumnInfo(name = "senior") val senior: String,
    @ColumnInfo(name = "short_name") val shortName: String,
    @ColumnInfo(name = "title") val title: String,
)