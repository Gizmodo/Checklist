package ru.dl.checklist.data.model.remote

data class ReadyMark(
    val id: Long,
    val points: Int,
    val answer: Float,
    val comment: String
)