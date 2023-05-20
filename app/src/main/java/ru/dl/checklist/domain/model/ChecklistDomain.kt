package ru.dl.checklist.domain.model

data class ChecklistDomain(
    val id: Long,
    val uuid: String,
    val address: String,
    val auditDate: String,
    val checker: String,
    val senior: String,
    val shortName: String,
    val percent: Double
)