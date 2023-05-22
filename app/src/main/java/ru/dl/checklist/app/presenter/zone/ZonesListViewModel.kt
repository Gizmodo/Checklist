package ru.dl.checklist.app.presenter.zone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.dl.checklist.app.app.App
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.BackendResponseDomain
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.usecase.GetZoneListByChecklist
import ru.dl.checklist.domain.usecase.UploadImagesUseCase
import ru.dl.checklist.domain.usecase.UploadMarksUseCase
import timber.log.Timber
import javax.inject.Inject

class ZonesListViewModel : ViewModel() {
    init {
        App.appComponent.inject(this)
    }

    var uuidArgs: String = ""
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }
    private val _listChannel = Channel<List<ZoneDomain>>()
    val zoneListEvent = _listChannel.receiveAsFlow()

    private val _uploadMarksChannel = Channel<ApiResult<BackendResponseDomain>>()
    val uploadMarksChannel = _uploadMarksChannel.receiveAsFlow()

    private val _uploadImagesChannel = Channel<ApiResult<BackendResponseDomain>>()
    val uploadImagesChannel = _uploadImagesChannel.receiveAsFlow()

    @Inject
    lateinit var getZoneListByChecklist: dagger.Lazy<GetZoneListByChecklist>

    @Inject
    lateinit var uploadMarksUseCase: dagger.Lazy<UploadMarksUseCase>

    @Inject
    lateinit var uploadImagesUseCase: dagger.Lazy<UploadImagesUseCase>
    fun onEvent(event: ZoneListEvent) {
        when (event) {
            ZoneListEvent.LoadZoneListByCategory -> loadZoneList(uuidArgs)
            ZoneListEvent.SendChecklist -> sendChecklist(uuidArgs)
        }
    }

    private fun sendChecklist(uuid: String) {
        Timber.i("Отправка чеклиста: показатели и изображения")
        viewModelScope.launch(exceptionHandler) {
            uploadMarksUseCase.get().run(uuid).collectLatest { data ->
                _uploadMarksChannel.send(data)
            }
            uploadImagesUseCase.get().run(uuid).collectLatest { data ->
                _uploadImagesChannel.send(data)
            }
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