package ru.dl.checklist.app.presenter.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dl.checklist.app.app.App
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.AssignedTemplateObject
import ru.dl.checklist.domain.usecase.GetChecklistTemplatesUseCase
import ru.dl.checklist.domain.usecase.SendAssignmentTemplateByObjectUseCase
import timber.log.Timber
import javax.inject.Inject

class TemplateViewModel : ViewModel(), TemplatesListContract {

    @Inject
    lateinit var getChecklistTemplatesUseCase: dagger.Lazy<GetChecklistTemplatesUseCase>

    @Inject
    lateinit var sendAssignmentTemplateByObjectUseCase: dagger.Lazy<SendAssignmentTemplateByObjectUseCase>

    private val mutableState = MutableStateFlow(TemplatesListContract.State())
    override val state: StateFlow<TemplatesListContract.State>
        get() = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<TemplatesListContract.Effect>()
    override val effect: SharedFlow<TemplatesListContract.Effect> =
        effectFlow.asSharedFlow()

    init {
        App.appComponent.inject(this)
    }

    override fun event(event: TemplatesListContract.Event) = when (event) {
        TemplatesListContract.Event.OnRefresh -> getData(mutableState.value.objectUUID)
        is TemplatesListContract.Event.OnSendAssignment -> sendAssignment(
            event.objectUUID,
            event.checklistUUID
        )

        is TemplatesListContract.Event.OnChangeObjectUUID -> {
            mutableState.update {
                it.copy(objectUUID = event.objectUUID)
            }
            getData(mutableState.value.objectUUID)
        }
    }

    private fun sendAssignment(objectUUID: String, checklistUUID: String) {
        Timber.i("Назначен $checklistUUID на $objectUUID")
        viewModelScope.launch {
            sendAssignmentTemplateByObjectUseCase.get().run(
                AssignedTemplateObject(
                    objectUUID = objectUUID,
                    templateUUID = checklistUUID
                )
            ).catch { exception ->
                effectFlow.emit(
                    TemplatesListContract.Effect.ShowMessage(
                        message = exception.localizedMessage ?: "An unexpected error occurred."
                    )
                )
            }.onEach { result ->
                when (result) {
                    is ApiResult.Error -> {
                        mutableState.update { it.copy(refreshing = false) }
                        effectFlow.emit(TemplatesListContract.Effect.ShowMessage(message = result._error))
                    }

                    ApiResult.Loading -> {
                        mutableState.update { it.copy(refreshing = true) }
                    }

                    is ApiResult.Success -> {
                        mutableState.update {
                            it.copy(refreshing = false)
                        }
                        effectFlow.emit(TemplatesListContract.Effect.ShowMessage(result._data.message))
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun getData(objectUUID: String) {
        viewModelScope.launch {
            if (mutableState.value.templatesList.isEmpty()) {
                getChecklistTemplates(objectUUID)
            }
        }
    }

    private suspend fun getChecklistTemplates(objectUUID: String) =
        getChecklistTemplatesUseCase.get().run(objectUUID)
            .catch { exception ->
                effectFlow.emit(
                    TemplatesListContract.Effect.ShowMessage(
                        message = exception.localizedMessage ?: "An unexpected error occurred."
                    )
                )
            }
            .onEach { result ->
                when (result) {
                    is ApiResult.Error -> {
                        mutableState.update { it.copy(refreshing = false) }
                        effectFlow.emit(TemplatesListContract.Effect.ShowMessage(message = result._error))
                    }

                    ApiResult.Loading -> {
                        mutableState.update { it.copy(refreshing = true) }
                    }

                    is ApiResult.Success -> {
                        mutableState.update {
                            it.copy(templatesList = result._data, refreshing = false)
                        }
                    }
                }

        }
        .launchIn(viewModelScope)
}
