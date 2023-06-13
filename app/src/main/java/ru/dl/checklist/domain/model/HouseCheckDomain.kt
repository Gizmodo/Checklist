package ru.dl.checklist.domain.model

data class HouseCheckDomain(
    val uuid: String,
    val name: String,
    val start: Boolean,
    val end: Boolean,
    val next: String? = null,
    val photoRequired: Boolean
)