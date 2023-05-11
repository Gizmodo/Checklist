package ru.dl.checklist.domain.model

data class ZoneDomain(
    val marks: List<MarkDomain>,
    val zone: String
)

data class ZoneDomain2(
    val id:Long,
    val zone:String
)