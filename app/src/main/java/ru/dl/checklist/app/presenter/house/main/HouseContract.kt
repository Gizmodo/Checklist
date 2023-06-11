package ru.dl.checklist.app.presenter.house.main

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.HouseChecklistDomain

interface HouseContract :
    UDFViewModel<HouseContract.State, HouseContract.Event, HouseContract.Effect> {
    data class State(
        val list: List<HouseChecklistDomain> = listOf(),
        val refreshing: Boolean = false
    )

    sealed class Event {
        data class OnItemClick(val item: HouseChecklistDomain) : Event()
        data object OnRefresh : Event()
        data object OnSend : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }
}