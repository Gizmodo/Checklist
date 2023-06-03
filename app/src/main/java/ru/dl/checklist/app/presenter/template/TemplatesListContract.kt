package ru.dl.checklist.app.presenter.template

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.TemplateDomain

interface TemplatesListContract :
    UDFViewModel<TemplatesListContract.State, TemplatesListContract.Event,
            TemplatesListContract.Effect> {

    data class State(
        val templatesList: List<TemplateDomain> = listOf(),
        val refreshing: Boolean = false
    )

    sealed class Event {
        object OnRefresh : Event()
    }

    sealed class Effect {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}