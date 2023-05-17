package ru.dl.checklist.app.presenter.mark

import ru.dl.checklist.domain.model.MarkDomain

sealed class MarkCardUIEvent {
    data class Yes(val item: MarkDomain) : MarkCardUIEvent()
    data class No(val item: MarkDomain) : MarkCardUIEvent()
    data class Attach(val item: MarkDomain) : MarkCardUIEvent()
    data class Comment(val text: String, val item: MarkDomain) : MarkCardUIEvent()
}