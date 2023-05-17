package ru.dl.checklist.domain.model

data class MarkDomain(
    val id: Long,
    val points: Int,
    val title: String,
    val answer: Answer,
    val comment: String
)