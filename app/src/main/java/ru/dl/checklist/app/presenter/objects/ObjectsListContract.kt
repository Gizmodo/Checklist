package ru.dl.checklist.app.presenter.objects

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.ObjectDomain

interface ObjectsListContract : UDFViewModel<ObjectsListContract.State,
        ObjectsListContract.Event, ObjectsListContract.Effect> {
    data class State(
        val objectsList: List<ObjectDomain> = listOf(),
        val refreshing: Boolean = false
    )

    sealed class Event {
        data class OnSearch(val searchString: String) : Event()
        data class OnItemClick(val item: ObjectDomain) : Event()
        data class OnSendAssignment(val objectUUID: String, val checklistUUID: String) : Event()
        object OnRefresh : Event()
    }

    sealed class Effect {
        data object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}