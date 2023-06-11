package ru.dl.checklist.app.presenter.house.main

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
import ru.dl.checklist.domain.usecase.GetHouseChecklistsUseCase
import javax.inject.Inject

class HouseViewModel : ViewModel(), HouseContract {
    private val mutableState = MutableStateFlow(HouseContract.State())
    override val state: StateFlow<HouseContract.State>
        get() = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<HouseContract.Effect>()
    override val effect: SharedFlow<HouseContract.Effect>
        get() = effectFlow.asSharedFlow()

    override fun event(event: HouseContract.Event) {
        when (event) {
            is HouseContract.Event.OnItemClick -> TODO()
            HouseContract.Event.OnRefresh -> getData()
            HouseContract.Event.OnSend -> TODO()
        }
    }

    @Inject
    lateinit var getHouseChecklistsUseCase: dagger.Lazy<GetHouseChecklistsUseCase>

    init {
        App.appComponent.inject(this)
        getData()
    }

    private fun getData() {
        viewModelScope.launch { getChecklists() }
    }

    private suspend fun getChecklists() = getHouseChecklistsUseCase.get().run()
        .catch {
            effectFlow.emit(
                HouseContract.Effect.ShowToast(
                    message = it.localizedMessage ?: "An unexpected error occurred."
                )
            )
        }
        .onEach { result ->
            when (result) {
                is ApiResult.Error -> {
                    mutableState.update { it.copy(refreshing = false) }
                    effectFlow.emit(HouseContract.Effect.ShowToast(message = result._error))
                }

                ApiResult.Loading -> {
                    mutableState.update { it.copy(refreshing = true) }
                }

                is ApiResult.Success -> {
                    mutableState.update { it.copy(list = result._data, refreshing = false) }
                }
            }
        }
        .launchIn(viewModelScope)
}