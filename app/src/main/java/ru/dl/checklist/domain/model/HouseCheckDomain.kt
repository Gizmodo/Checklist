package ru.dl.checklist.domain.model

data class HouseCheckDomain(
    val id: Long,
    val uuid: String,
    val checklistId: Long,
    val name: String,
    val isPhotoRequired: Boolean,
    val next_check_uuid: String? = null,
    val isStart: Boolean,
    val isEnd: Boolean,
    val answer: Boolean? = null,
    val mediacount: Int
)