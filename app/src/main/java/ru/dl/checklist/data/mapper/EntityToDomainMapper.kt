package ru.dl.checklist.data.mapper

import ru.dl.checklist.app.ext.findBy
import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.domain.model.Answer
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.model.valueclasses.ValueClasses

object EntityToDomainMapper {
    fun ChecklistEntity.toDomain(): ChecklistDomain {
        return ChecklistDomain(
            id = id,
            uuid = uuid,
            address = ValueClasses.Address(address),
            auditDate = ValueClasses.AuditDate(auditDate),
            checker = ValueClasses.Checker(checker),
            senior = ValueClasses.Senior(senior),
            shortName = ValueClasses.ShortName(shortName)
        )
    }

    fun ZoneEntity.toDomain(): ZoneDomain = ZoneDomain(id, zone)

    fun MarkEntity.toDomain(): MarkDomain {
        val answerValue = (Answer::value findBy this.answer) ?: Answer.UNDEFINED
        return MarkDomain(id, points, title, answerValue, comment)
    }
}