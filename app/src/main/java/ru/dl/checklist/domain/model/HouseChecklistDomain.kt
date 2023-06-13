package ru.dl.checklist.domain.model

data class HouseChecklistDomain(
    val uuid: String,
    val title: String,
    val timeEnd: String,
    val timeStart: String
)