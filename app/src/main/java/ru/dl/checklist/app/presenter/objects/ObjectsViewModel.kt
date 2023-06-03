package ru.dl.checklist.app.presenter.objects

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
import ru.dl.checklist.domain.model.ObjectDomain
import ru.dl.checklist.domain.usecase.GetObjectsListUseCase
import timber.log.Timber
import javax.inject.Inject

class ObjectsViewModel : ViewModel(), ObjectsListContract {
    private var originalList: List<ObjectDomain> = mutableListOf()

    @Inject
    lateinit var getObjectsListUseCase: dagger.Lazy<GetObjectsListUseCase>

    private val mutableState = MutableStateFlow(ObjectsListContract.State())

    override val state: StateFlow<ObjectsListContract.State>
        get() = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<ObjectsListContract.Effect>()

    override val effect: SharedFlow<ObjectsListContract.Effect> =
        effectFlow.asSharedFlow()

    init {
        App.appComponent.inject(this)
        getData()
    }

    override fun event(event: ObjectsListContract.Event) = when (event) {
        is ObjectsListContract.Event.OnItemClick -> {
            Timber.i("Item clicked ${event.item}")
        }

        ObjectsListContract.Event.OnRefresh -> getData()
        is ObjectsListContract.Event.OnSearch -> filterList(event.searchString)
        is ObjectsListContract.Event.OnSendAssignment -> sendAssigmnment(
            event.objectUUID,
            event.checklistUUID
        )
    }

    private fun sendAssigmnment(objectUUID: String, checklistUUID: String) {
        Timber.i("Назначен $checklistUUID на $objectUUID")
        // TODO: Add POST request
    }

    private fun filterList(searchString: String) {
        val filtered = originalList.filter { item -> item.name.contains(searchString) }
        mutableState.update { it.copy(objectsList = filtered) }
    }

    private fun getData() {
        viewModelScope.launch { getNewsList() }
    }

    private suspend fun getNewsList() = getObjectsListUseCase.get().run()
        .catch { exception ->
            effectFlow.emit(
                ObjectsListContract.Effect.ShowToast(
                    message = exception.localizedMessage ?: "An unexpected error occurred."
                )
            )
        }
        .onEach { result ->
            when (result) {
                is ApiResult.Error -> {
                    mutableState.update { it.copy(refreshing = false) }
                    effectFlow.emit(ObjectsListContract.Effect.ShowToast(message = result._error))
                }

                ApiResult.Loading -> {
                    mutableState.update { it.copy(refreshing = true) }
                }

                is ApiResult.Success -> {
                    originalList = result._data
                    mutableState.update { it.copy(objectsList = originalList, refreshing = false) }
                }
            }
        }
        .launchIn(viewModelScope)
}