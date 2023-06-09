package ru.dl.checklist.app.presenter.auth

sealed class NavigationRoute {
    data object Unknown : NavigationRoute()
    data object SelfProduction : NavigationRoute()
    data object SH : NavigationRoute()
    data object KD : NavigationRoute()
}