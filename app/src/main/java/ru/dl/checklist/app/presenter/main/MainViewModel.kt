package ru.dl.checklist.app.presenter.main

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
import ru.dl.checklist.domain.usecase.GetChecklistUseCase
import timber.log.Timber
import javax.inject.Inject

class MainViewModel : ViewModel(), MainContract {
    @Inject
    lateinit var getChecklistUseCase: dagger.Lazy<GetChecklistUseCase>

    init {
        App.appComponent.inject(this)
        getData()
    }

    private val mutableState = MutableStateFlow(MainContract.State())
    override val state: StateFlow<MainContract.State> = mutableState.asStateFlow()
    private val effectFlow = MutableSharedFlow<MainContract.Effect>()
    override val effect: SharedFlow<MainContract.Effect> = effectFlow.asSharedFlow()

    override fun event(event: MainContract.Event) {
        when (event) {
            is MainContract.Event.OnItemClick -> viewModelScope.launch {
                Timber.i("Item clicked ${event.item}")
                effectFlow.emit(
                    MainContract.Effect.Navigate(
                        NavigationRouteMain.RouteDetailChecklist(
                            event.item.address
                        )
                    )
                )
            }

            MainContract.Event.OnRefresh -> getData()
            MainContract.Event.OnAssignClick -> viewModelScope.launch {
                effectFlow.emit(MainContract.Effect.Navigate(NavigationRouteMain.RouteAssignChecklist))
            }

        }
    }

    private fun getData() {
        viewModelScope.launch { loadChecklist() }
    }

    private suspend fun loadChecklist() = getChecklistUseCase.get().run()
        .catch {
            effectFlow.emit(
                MainContract.Effect.ShowMessage(
                    message = it.localizedMessage ?: "An unexpected error occurred."
                )
            )
        }.onEach { result ->
            when (result) {
                is ApiResult.Error -> {
                    mutableState.update { it.copy(loading = false) }
                    effectFlow.emit(MainContract.Effect.ShowMessage(message = result._error))
                }

                ApiResult.Loading -> {
                    mutableState.update { it.copy(loading = true) }
                }

                is ApiResult.Success -> {
                    if (result._data.isNotEmpty()) {
                        mutableState.update { it.copy(list = result._data, loading = false) }
                    }
                }
            }
        }.launchIn(viewModelScope)
}