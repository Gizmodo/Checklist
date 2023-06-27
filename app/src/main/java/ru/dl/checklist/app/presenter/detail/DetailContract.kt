package ru.dl.checklist.app.presenter.detail

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.ChecklistDomain

interface DetailContract :
    UDFViewModel<DetailContract.State, DetailContract.Event, DetailContract.Effect> {

    data class State(
        val list: List<ChecklistDomain> = listOf(),
    )

    sealed class Event {
        data class OnItemClick(val item: ChecklistDomain) : Event()
        data object OnRefresh : Event()
    }

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
        data class Navigate(val direction: NavigationRouteDetail) : Effect()
    }
}