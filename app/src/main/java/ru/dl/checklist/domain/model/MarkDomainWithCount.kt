package ru.dl.checklist.domain.model

data class MarkDomainWithCount(
    val id: Long,
    val points: Int,
    val title: String,
    val answer: Answer,
    val comment: String,
    val count: Int
)
