package ru.dl.checklist.app.utils

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UDFViewModel<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}