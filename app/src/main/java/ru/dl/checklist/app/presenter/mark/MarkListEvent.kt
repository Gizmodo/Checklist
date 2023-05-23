package ru.dl.checklist.app.presenter.mark

import android.graphics.Bitmap

sealed class MarkListEvent {
    object LoadMarkListByZone : MarkListEvent()
    data class SetZoneId(val zoneId: Long) : MarkListEvent()
    data class ChangeAttachment(val markId: Long, val bitmap: Bitmap) : MarkListEvent()
    data class ChangeMark(
        val markId: Long,
        val comment: String,
        val answer: Float,
        val pkd: String
    ) :
        MarkListEvent()
}