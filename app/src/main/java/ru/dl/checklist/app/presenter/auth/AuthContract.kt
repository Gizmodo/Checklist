package ru.dl.checklist.app.presenter.auth

import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.UserDomain

interface AuthContract : UDFViewModel<AuthContract.State, AuthContract.Event, AuthContract.Effect> {

    data class State(
        val usersList: List<UserDomain> = listOf(),
        val username: String = "",
        val password: String = "",
        val group: String = "",
        val isLoading: Boolean = false,
        val isLoginButtonEnabled: Boolean = false
    )

    sealed class Event {
        data class onUsernameChange(val username: String, val group: String) : Event()
        data class onPasswordChange(val password: String) : Event()
        data object OnLogin : Event()
    }

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
    }
}