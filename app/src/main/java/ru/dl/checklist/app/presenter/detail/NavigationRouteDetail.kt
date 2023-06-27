package ru.dl.checklist.app.presenter.detail

sealed class NavigationRouteDetail {
    data class RouteDetailToZone(val checklistUUID: String) : NavigationRouteDetail()
}