package ru.dl.checklist.app.presenter.mark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.dl.checklist.app.app.App
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.usecase.GetMarkListByZone
import timber.log.Timber
import javax.inject.Inject

class MarksListViewModel : ViewModel() {
    init {
        App.appComponent.inject(this)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }
    private val _listChannel = Channel<List<MarkDomain>>()
    val markListEvent = _listChannel.receiveAsFlow()

    @Inject
    lateinit var getMarkListByZoneLazy: dagger.Lazy<GetMarkListByZone>
    fun onEvent(event: MarkListEvent) {
        when (event) {
            is MarkListEvent.LoadMarkListByZone -> loadMarkList(event.zoneId)
        }
    }

    private fun loadMarkList(zoneId: Long) {
        viewModelScope.launch(exceptionHandler) {
            getMarkListByZoneLazy.get().run(zoneId).collectLatest { data ->
                _listChannel.send(data)
            }
        }
    }
}