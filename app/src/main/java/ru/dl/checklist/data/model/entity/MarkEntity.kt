package ru.dl.checklist.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.dl.checklist.domain.model.Answer

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
    val answer: Answer?,
    val comment: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MarkEntity

        if (id != other.id) return false
        if (zoneId != other.zoneId) return false
        if (points != other.points) return false
        if (title != other.title) return false
        if (answer != other.answer) return false
        if (comment != other.comment) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + zoneId.hashCode()
        result = 31 * result + points
        result = 31 * result + title.hashCode()
        result = 31 * result + answer.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}