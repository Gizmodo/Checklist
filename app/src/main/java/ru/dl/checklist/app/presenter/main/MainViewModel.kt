package ru.dl.checklist.app.presenter.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.dl.checklist.app.app.App
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.app.utils.SD
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.usecase.GetChecklistUseCase
import javax.inject.Inject

class MainViewModel : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onException(throwable)
    }

    private fun onException(throwable: Throwable) {
        foodChannel.trySend(SD.Loading)
        // _statePrev.value = StatefulData.Error(throwable.message.toString())
    }

    init {
        App.appComponent.inject(this)
        //   loadFoodList()
    }

    private val foodChannel = Channel<SD<List<ChecklistDomain>>>()
    val foodEvents = foodChannel.receiveAsFlow()

    @Inject
    lateinit var getChecklistUseCase: dagger.Lazy<GetChecklistUseCase>
    fun onEvent(event: ChecklistEvent) {
        when (event) {
            ChecklistEvent.LoadChecklist -> loadFoodList()
        }
    }

    private fun loadFoodList() {
        viewModelScope.launch(exceptionHandler) {
            getChecklistUseCase.get().run().collectLatest { apiResult ->
                when (apiResult) {
                    is ApiResult.Error -> {
                        foodChannel.send(SD.Error(apiResult._error))
                    }

                    is ApiResult.Success -> {
                        foodChannel.send(SD.Success(apiResult._data))
                    }

                    ApiResult.Loading -> {
                        foodChannel.send(SD.Loading)
                    }
                }
            }
        }
    }
}