package ru.dl.checklist.app.presenter.detail

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
import ru.dl.checklist.domain.usecase.GetChecklistByAddressUseCase
import timber.log.Timber
import javax.inject.Inject

class DetailViewModel : ViewModel(), DetailContract {
    @Inject
    lateinit var getChecklistByAddressUseCase: dagger.Lazy<GetChecklistByAddressUseCase>
    var address: String = ""

    init {
        App.appComponent.inject(this)
        getData(address)
    }

    private val mutableState = MutableStateFlow(DetailContract.State())
    override val state: StateFlow<DetailContract.State> = mutableState.asStateFlow()
    private val effectFlow = MutableSharedFlow<DetailContract.Effect>()
    override val effect: SharedFlow<DetailContract.Effect> = effectFlow.asSharedFlow()

    override fun event(event: DetailContract.Event) {
        when (event) {
            is DetailContract.Event.OnItemClick -> viewModelScope.launch {
                Timber.i("Item clicked ${event.item}")
                effectFlow.emit(
                    DetailContract.Effect.Navigate(
                        NavigationRouteDetail.RouteDetailToZone(
                            event.item.uuid
                        )
                    )
                )
            }

            DetailContract.Event.OnRefresh -> getData(address)

        }
    }

    private fun getData(address: String) {
        if (address.isNotEmpty()) {
            viewModelScope.launch { loadChecklist(address) }
        }
    }

    private suspend fun loadChecklist(address: String) =
        getChecklistByAddressUseCase.get().run(address)
            .catch {
                effectFlow.emit(
                    DetailContract.Effect.ShowMessage(
                        message = it.localizedMessage ?: "An unexpected error occurred."
                    )
                )
            }.onEach { result ->
                mutableState.update { it.copy(list = result) }
            }.launchIn(viewModelScope)
}