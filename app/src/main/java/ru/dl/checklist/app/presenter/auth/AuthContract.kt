package ru.dl.checklist.app.presenter.auth

import ru.dl.checklist.BuildConfig
import ru.dl.checklist.app.utils.UDFViewModel
import ru.dl.checklist.domain.model.UserDomain
import java.math.BigInteger
import java.security.MessageDigest

interface AuthContract : UDFViewModel<AuthContract.State, AuthContract.Event, AuthContract.Effect> {

    data class State(
        val usersList: List<UserDomain> = listOf(),
        val username: String = "",
        val password: String = "",
        val group: String = "",
        val isLoading: Boolean = false,
        val isLoginButtonEnabled: Boolean = false
    ) {
        companion object {
            private fun md5(input: String): String {
                val md = MessageDigest.getInstance("MD5")
                return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
            }

            fun encryptLogin(login: String?): String {
                val prefix = BuildConfig.LOGIN_PREFIX
                val suffix = BuildConfig.LOGIN_SUFFIX
                return md5(prefix + login.orEmpty().lowercase() + suffix).uppercase()
            }

            fun encryptPassword(password: String?): String {
                val prefix = BuildConfig.PASSWORD_PREFIX
                val suffix = BuildConfig.PASSWORD_SUFFIX
                return md5(prefix + password.orEmpty().lowercase() + suffix).uppercase()
            }
        }

        val userHash: String
            get() = encryptLogin(username)
        val passwordHash: String
            get() = encryptPassword(password)
    }

    sealed class Event {
        data class OnUsernameChange(val username: String, val group: String) : Event()
        data class OnPasswordChange(val password: String) : Event()
        data object OnLogin : Event()
        data class OnShowMessage(val message: String) : Event()
    }

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
        data class Navigate(val direction: NavigationRoute) : Effect()
    }
}