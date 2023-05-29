package ru.dl.checklist.data.mapper

import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.ZoneDomain

object EntityToDomainMapper {
    fun ChecklistEntity.toDomain(): ChecklistDomain {
        return ChecklistDomain(
            id = id,
            uuid = uuid,
            address = address,
            auditDate = auditDate,
            checker = checker,
            senior = senior,
            shortName = shortName,
            0.0,
            title = title
        )
    }

    fun ZoneEntity.toDomain(): ZoneDomain = ZoneDomain(id, zone, 0.0)

    fun MarkEntity.toDomain(): MarkDomain =
        MarkDomain(id, points, title, answer ?: 0f, comment)
}