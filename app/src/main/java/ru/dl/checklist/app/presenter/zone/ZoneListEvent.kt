package ru.dl.checklist.app.presenter.zone

sealed class ZoneListEvent {
    data class LoadZoneListByCategory(val uuid:String):ZoneListEvent()
}