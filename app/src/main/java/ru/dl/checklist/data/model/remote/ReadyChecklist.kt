package ru.dl.checklist.data.model.remote

data class ReadyChecklist(
    val uuid: String,
    val marks: List<ReadyMark>,
)