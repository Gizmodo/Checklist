package ru.dl.checklist.data.model.remote

import ru.dl.checklist.domain.model.Answer

data class ReadyMark(
    val id: Long,
    val points: Int,
    val answer: Answer,
    val comment: String
)