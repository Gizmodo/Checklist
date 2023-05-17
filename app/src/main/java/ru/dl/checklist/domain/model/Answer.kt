package ru.dl.checklist.domain.model

enum class Answer(val value: Boolean?) {
    YES(true),
    NO(false),
    UNDEFINED(null)
}