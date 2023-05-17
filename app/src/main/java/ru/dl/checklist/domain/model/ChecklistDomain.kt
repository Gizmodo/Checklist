package ru.dl.checklist.domain.model

import ru.dl.checklist.domain.model.valueclasses.ValueClasses

data class ChecklistDomain(
    val id: Long,
    val uuid: String,
    val address: ValueClasses.Address,
    val auditDate: ValueClasses.AuditDate,
    val checker: ValueClasses.Checker,
    val senior: ValueClasses.Senior,
    val shortName: ValueClasses.ShortName
)