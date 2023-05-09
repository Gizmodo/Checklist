package ru.dl.checklist.domain.model

data class ChecklistsDomain(
    val checklists: List<ChecklistDomain>
)

data class ChecklistDomain(
    val address: Address,
    val auditDate: AuditDate,
    val checker: Checker,
    val senior: Senior,
    val shortName: ShortName,
    val zones: List<ZoneDomain>
)

data class ZoneDomain(
    val id: Int,
    val marks: List<MarkDomain>,
    val zone: String
)

data class MarkDomain(
    val points: Point,
    val title: Title
)

@JvmInline
value class Address(val value: String)

@JvmInline
value class AuditDate(val value: String)

@JvmInline
value class Checker(val value: String)

@JvmInline
value class Senior(val value: String)

@JvmInline
value class ShortName(val value: String)


@JvmInline
value class Point(val value: Int)

@JvmInline
value class Title(val value: String)
