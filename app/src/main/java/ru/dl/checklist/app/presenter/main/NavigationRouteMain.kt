package ru.dl.checklist.app.presenter.main

sealed class NavigationRouteMain {
    data object RouteAssignChecklist : NavigationRouteMain()
    data class RouteDetailChecklist(val address: String) : NavigationRouteMain()
}