package ru.dl.checklist.data.model.remote

data class ReadyMedia(
    // TODO: Сменить в entity (внешний ключ)
    val uuid: Long,
    val media: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReadyMedia

        if (uuid != other.uuid) return false
        return media.contentEquals(other.media)
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + media.contentHashCode()
        return result
    }
}