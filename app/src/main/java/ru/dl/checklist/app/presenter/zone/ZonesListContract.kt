package ru.dl.checklist.app.presenter.zone

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.ZoneDomain

interface ZonesListContract :
    UDFViewModel<ZonesListContract.State, ZonesListContract.Event, ZonesListContract.Effect> {
    data class State(
        val list: List<ZoneDomain> = listOf(),
        val refreshing: Boolean = false
    )

    sealed class Event {
        data class OnItemClick(val item: ZoneDomain) : Event()
        data object OnRefresh : Event()
        data object OnSend : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }
}