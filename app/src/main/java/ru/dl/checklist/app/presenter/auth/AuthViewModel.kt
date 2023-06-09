package ru.dl.checklist.app.presenter.auth

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
import ru.dl.checklist.domain.usecase.GetUsersUseCase
import javax.inject.Inject

class AuthViewModel : ViewModel(), AuthContract {
    @Inject
    lateinit var getUsersUseCase: dagger.Lazy<GetUsersUseCase>

    private val mutableState = MutableStateFlow(AuthContract.State())

    override val state: StateFlow<AuthContract.State>
        get() = mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<AuthContract.Effect>()
    override val effect: SharedFlow<AuthContract.Effect> = effectFlow.asSharedFlow()

    override fun event(event: AuthContract.Event) {
        when (event) {
            AuthContract.Event.OnLogin -> {
                viewModelScope.launch {
                    effectFlow.emit(AuthContract.Effect.ShowMessage("Попытка войти!"))
                }
            }

            is AuthContract.Event.onPasswordChange -> {
                mutableState.update {
                    it.copy(
                        password = event.password,
                        isLoginButtonEnabled = state.value.username.isNotEmpty() && event.password.isNotEmpty()
                    )
                }
            }

            is AuthContract.Event.onUsernameChange -> {
                mutableState.update {
                    it.copy(
                        username = event.username,
                        group = event.group,
                        isLoginButtonEnabled = event.username.isNotEmpty() && state.value.password.isNotEmpty()
                    )
                }
            }
        }
    }

    init {
        App.appComponent.inject(this)
        getData()
    }

    private fun getData() {
        viewModelScope.launch { getUsersList() }
    }

    private suspend fun getUsersList() = getUsersUseCase.get().run()
        .catch { exception ->
            effectFlow.emit(
                AuthContract.Effect.ShowMessage(
                    message = exception.localizedMessage ?: "An unexpected error occurred."
                )
            )
        }
        .onEach { result ->
            when (result) {
                is ApiResult.Error -> {
                    mutableState.update { it.copy(isLoading = false) }
                    effectFlow.emit(AuthContract.Effect.ShowMessage(result._error))
                }

                ApiResult.Loading -> mutableState.update { it.copy(isLoading = true) }
                is ApiResult.Success -> {
                    mutableState.update { it.copy(isLoading = false, usersList = result._data) }
                }
            }
        }.launchIn(viewModelScope)

}