package ru.dl.checklist.app.presenter.zone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dl.checklist.app.app.App
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.BackendResponseDomain
import ru.dl.checklist.domain.usecase.GetZoneListByChecklist
import ru.dl.checklist.domain.usecase.UploadImagesUseCase
import ru.dl.checklist.domain.usecase.UploadMarksUseCase
import timber.log.Timber
import javax.inject.Inject

class ZonesListViewModel : ViewModel(), ZonesListContract {
    var uuidArgs: String = ""
    private val mutableState = MutableStateFlow(ZonesListContract.State())
    override val state: StateFlow<ZonesListContract.State>
        get() = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<ZonesListContract.Effect>()
    override val effect: SharedFlow<ZonesListContract.Effect>
        get() = effectFlow.asSharedFlow()

    init {
        App.appComponent.inject(this)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }

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

    private fun getData(uuid: String) {
        viewModelScope.launch { getZonesList(uuid) }
    }

    private suspend fun getZonesList(uuid: String) = getZoneListByChecklist.get().run(uuid)
        .catch { exception ->
            effectFlow.emit(
                ZonesListContract.Effect.ShowToast(
                    message = exception.localizedMessage ?: "An unexpected error occurred."
                )
            )
        }
        .onEach { result ->
            mutableState.update { it.copy(list = result) }
        }
        .launchIn(viewModelScope)

    override fun event(event: ZonesListContract.Event) {
        when (event) {
            is ZonesListContract.Event.OnItemClick -> TODO()
            ZonesListContract.Event.OnRefresh -> getData(uuidArgs)
            ZonesListContract.Event.OnSend -> sendChecklist(uuidArgs)
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
}