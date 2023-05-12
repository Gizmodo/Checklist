package ru.dl.checklist.data.mapper

import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.model.valueclasses.valueclasses

object EntityToDomainMapper {
    fun ChecklistEntity.toDomain(): ChecklistDomain {
        return ChecklistDomain(
            id = id,
            uuid = uuid,
            address = valueclasses.Address(address),
            auditDate = valueclasses.AuditDate(auditDate),
            checker = valueclasses.Checker(checker),
            senior = valueclasses.Senior(senior),
            shortName = valueclasses.ShortName(shortName)
        )
    }

    fun ZoneEntity.toDomain(): ZoneDomain = ZoneDomain(id, zone)
}