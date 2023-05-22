package ru.dl.checklist.app.presenter.zone

sealed class ZoneListEvent {
    object LoadZoneListByCategory : ZoneListEvent()
    object SendChecklist : ZoneListEvent()
}