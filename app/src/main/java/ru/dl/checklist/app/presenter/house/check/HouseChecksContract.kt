package ru.dl.checklist.app.presenter.house.check

import android.graphics.Bitmap
import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.HouseCheckDomain

interface HouseChecksContract :
    UDFViewModel<HouseChecksContract.State, HouseChecksContract.Event, HouseChecksContract.Effect> {

    data class State(
        val list: List<HouseCheckDomain> = listOf(),
        val refreshing: Boolean = false,
        val isPrevEnabled: Boolean = false,
        val isNextVisible: Boolean = false,
        val isSendVisible: Boolean = false,
        val isSendEnabled: Boolean = false,
        val isNextEnabled: Boolean = true,
    )

    sealed class Event {
        data class OnItemClick(val item: HouseCheckDomain) : Event()
        data object OnRefresh : Event()
        data object OnSend : Event()
        data class OnChangeSlide(val position: Int, val item: HouseCheckDomain) : Event()
        data class OnNoClick(val item: HouseCheckDomain) : Event()
        data class OnYesClick(val item: HouseCheckDomain) : Event()
        data class OnChangeAttachment(val houseCheckId: Long, val bitmap: Bitmap) : Event()
        data class OnCheckPhoto(val position: Int, val item: HouseCheckDomain) : Event()
    }

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
    }
}