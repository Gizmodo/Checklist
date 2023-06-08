package ru.dl.checklist.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mark", foreignKeys = [
        ForeignKey(
            entity = ZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["zoneId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["zoneId"])
    ]
)
data class MarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zoneId: Long,
    val points: Int,
    val title: String,
    val answer: Float?,
    val comment: String,
    val pkd: String,
    val flag: Int
)