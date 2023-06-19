package ru.dl.checklist.app.presenter.template

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.TemplateDomain

interface TemplatesListContract :
    UDFViewModel<TemplatesListContract.State, TemplatesListContract.Event,
            TemplatesListContract.Effect> {

    data class State(
        val templatesList: List<TemplateDomain> = listOf(),
        val refreshing: Boolean = false,
        val objectUUID: String = String()
    )

    sealed class Event {
        data object OnRefresh : Event()
        data class OnSendAssignment(val objectUUID: String, val checklistUUID: String) : Event()
        data class OnChangeObjectUUID(val objectUUID: String) : Event()
    }

    sealed class Effect {
        data object OnBackPressed : Effect()
        data class ShowMessage(val message: String) : Effect()
    }
}