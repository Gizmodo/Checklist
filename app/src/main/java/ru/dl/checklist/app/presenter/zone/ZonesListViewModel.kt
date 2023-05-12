package ru.dl.checklist.app.presenter.zone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.dl.checklist.app.app.App
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.usecase.GetZoneListByChecklist
import timber.log.Timber
import javax.inject.Inject

class ZonesListViewModel : ViewModel() {
    init {
        App.appComponent.inject(this)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }
    private val _listChannel = Channel<List<ZoneDomain>>()
    val zoneListEvent = _listChannel.receiveAsFlow()

    @Inject
    lateinit var getZoneListByChecklist: dagger.Lazy<GetZoneListByChecklist>
    fun onEvent(event: ZoneListEvent) {
        when (event) {
            is ZoneListEvent.LoadZoneListByCategory -> loadZoneList(event.uuid)
        }
    }

    private fun loadZoneList(uuid: String) {
        viewModelScope.launch(exceptionHandler) {
            getZoneListByChecklist.get().run(uuid).collectLatest { data ->
                _listChannel.send(data)
            }
        }
    }
}