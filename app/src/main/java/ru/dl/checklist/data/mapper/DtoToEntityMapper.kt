package ru.dl.checklist.data.mapper

import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.remote.ChecklistDto

object DtoToEntityMapper {
    fun ChecklistDto.toEntity(): ChecklistEntity {
        return ChecklistEntity(
            uuid = uuid ?: "",
            address = address ?: "",
            auditDate = auditDate ?: "",
            checker = checker ?: "",
            senior = senior ?: "",
            shortName = shortName ?: ""
        )
    }

}