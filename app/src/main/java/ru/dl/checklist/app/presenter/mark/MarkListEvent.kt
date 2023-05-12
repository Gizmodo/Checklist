package ru.dl.checklist.app.presenter.mark

sealed class MarkListEvent {
    data class LoadMarkListByZone(val zoneId: Long) : MarkListEvent()
}