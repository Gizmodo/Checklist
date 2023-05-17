package ru.dl.checklist.app.presenter.mark

import ru.dl.checklist.domain.model.MarkDomain

sealed class MarkListEvent {
    object LoadMarkListByZone : MarkListEvent()
    data class SetZoneId(val zoneId: Long) : MarkListEvent()
    data class ChangeAnswer(val mark: MarkDomain) : MarkListEvent()
    data class ChangeComment(val mark: MarkDomain) : MarkListEvent()
}