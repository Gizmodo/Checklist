package ru.dl.checklist.app.presenter.main

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.ChecklistGroupedByAddressDomain

interface MainContract : UDFViewModel<MainContract.State, MainContract.Event, MainContract.Effect> {

    data class State(
        val list: List<ChecklistGroupedByAddressDomain> = listOf(),
        val loading: Boolean = false
    )

    sealed class Event {
        data class OnItemClick(val item: ChecklistGroupedByAddressDomain) : Event()
        data object OnRefresh : Event()
        data object OnAssignClick : Event()
    }

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
        data class Navigate(val direction: NavigationRouteMain) : Effect()
    }
}