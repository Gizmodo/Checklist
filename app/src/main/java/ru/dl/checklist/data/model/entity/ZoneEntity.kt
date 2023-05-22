package ru.dl.checklist.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "zone", foreignKeys = [
        ForeignKey(
            entity = ChecklistEntity::class,
            parentColumns = ["id"],
            childColumns = ["checklistId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["checklistId"])
    ]
)
data class ZoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val checklistId: Long,
    val zone: String
)