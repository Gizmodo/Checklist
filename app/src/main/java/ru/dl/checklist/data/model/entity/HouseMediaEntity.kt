package ru.dl.checklist.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "house_media", foreignKeys = [
        ForeignKey(
            entity = HouseCheckEntity::class,
            parentColumns = ["id"],
            childColumns = ["houseCheckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["houseCheckId"])
    ]
)
data class HouseMediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val houseCheckId: Long,
    val media: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HouseMediaEntity

        if (id != other.id) return false
        if (houseCheckId != other.houseCheckId) return false
        if (media != null) {
            if (other.media == null) return false
            if (!media.contentEquals(other.media)) return false
        } else if (other.media != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + houseCheckId.hashCode()
        result = 31 * result + (media?.contentHashCode() ?: 0)
        return result
    }
}