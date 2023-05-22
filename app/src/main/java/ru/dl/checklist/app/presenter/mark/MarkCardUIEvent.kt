package ru.dl.checklist.app.presenter.mark

import ru.dl.checklist.domain.model.MarkDomain

sealed class MarkCardUIEvent {
    data class ChangeAnswer(val item: MarkDomain) : MarkCardUIEvent()
    data class ChangeAttach(val item: MarkDomain) : MarkCardUIEvent()
    data class ChangeComment(val item: MarkDomain) : MarkCardUIEvent()
}