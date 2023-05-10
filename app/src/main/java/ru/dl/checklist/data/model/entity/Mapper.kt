package ru.dl.checklist.data.model.entity

import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.ZoneDomain

object Mapper {
    fun ChecklistDomain.toEntity(): ChecklistEntity {
        return ChecklistEntity(
            address = address.value,
            auditDate = auditDate.value,
            checker = checker.value,
            senior = senior.value,
            shortName = shortName.value
        )
    }

    fun ZoneDomain.toEntity(checklistId: Long): ZoneEntity {
        return ZoneEntity(
            checklistId = checklistId,
            zone = zone
        )
    }

    fun MarkDomain.toEntity(zoneId: Long): MarkEntity {
        return MarkEntity(
            zoneId = zoneId,
            points = points,
            title = title
        )
    }
}
