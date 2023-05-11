package ru.dl.checklist.app.presenter.main

sealed class ChecklistEvent {
    object LoadChecklist:ChecklistEvent()
}