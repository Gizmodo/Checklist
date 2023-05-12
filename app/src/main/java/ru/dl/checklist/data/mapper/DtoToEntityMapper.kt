package ru.dl.checklist.data.mapper

import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.data.model.remote.ChecklistDto
import ru.dl.checklist.data.model.remote.MarkDto
import ru.dl.checklist.data.model.remote.ZoneDto

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

    fun ZoneDto.toEntity(checklistId: Long): ZoneEntity {
        return ZoneEntity(
            checklistId = checklistId,
            zone = zone ?: ""
        )
    }

    fun MarkDto.toEntity(zoneId: Long): MarkEntity {
        return MarkEntity(
            zoneId = zoneId,
            points = points ?: 0,
            title = title ?: ""
        )
    }
}