package ru.dl.checklist.domain.model

import ru.dl.checklist.domain.model.valueclasses.valueclasses

data class ChecklistDomain(
    val id: Long,
    val uuid: String,
    val address: valueclasses.Address,
    val auditDate: valueclasses.AuditDate,
    val checker: valueclasses.Checker,
    val senior: valueclasses.Senior,
    val shortName: valueclasses.ShortName
)