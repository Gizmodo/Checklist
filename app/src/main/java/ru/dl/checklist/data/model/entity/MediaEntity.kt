package ru.dl.checklist.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media", foreignKeys = [
        ForeignKey(
            entity = MarkEntity::class,
            parentColumns = ["id"],
            childColumns = ["markId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["markId"])
    ]
)
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val markId: Long,
    val media: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaEntity

        if (id != other.id) return false
        if (markId != other.markId) return false
        if (media != null) {
            if (other.media == null) return false
            if (!media.contentEquals(other.media)) return false
        } else if (other.media != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + markId.hashCode()
        result = 31 * result + (media?.contentHashCode() ?: 0)
        return result
    }
}