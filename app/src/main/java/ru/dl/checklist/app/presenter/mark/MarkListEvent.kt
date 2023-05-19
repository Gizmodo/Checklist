package ru.dl.checklist.app.presenter.mark

import android.graphics.Bitmap
import ru.dl.checklist.domain.model.Answer

sealed class MarkListEvent {
    object LoadMarkListByZone : MarkListEvent()
    data class SetZoneId(val zoneId: Long) : MarkListEvent()
    data class ChangeAnswer(val markId: Long, val answer: Answer) : MarkListEvent()
    data class ChangeComment(val markId: Long, val comment: String) : MarkListEvent()
    data class ChangeAttachment(val markId: Long, val bitmap: Bitmap) : MarkListEvent()
}