package ru.dl.checklist.app.ext

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import ru.dl.checklist.app.presenter.bottomsheet.BottomSheetFragment
import ru.dl.checklist.app.presenter.bottomsheet.BottomSheetYesNoFragment

fun <T : Any> BottomSheetFragment.viewBottomSheetLifecycleLazy(initializer: (View) -> T): Lazy<T> =
    viewBottomSheetLifecycleLazy(this, initializer)

private class viewBottomSheetLifecycleLazy<out T : Any>(
    private val fragment: BottomSheetFragment,
    private val initializer: (View) -> T,
) : Lazy<T>, LifecycleEventObserver {
    private var cached: T? = null

    override val value: T
        get() {
            return cached ?: run {
                val newValue = initializer(fragment.requireView())
                cached = newValue
                fragment.viewLifecycleOwner.lifecycle.addObserver(this)
                newValue
            }
        }

    override fun isInitialized() = cached != null

    override fun toString() = cached.toString()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            cached = null
        }
    }
}

fun <T : Any> BottomSheetYesNoFragment.viewBottomSheetLifecycleLazy(initializer: (View) -> T): Lazy<T> =
    viewBottomSheetYesNoLifecycleLazy(this, initializer)

private class viewBottomSheetYesNoLifecycleLazy<out T : Any>(
    private val fragment: BottomSheetYesNoFragment,
    private val initializer: (View) -> T,
) : Lazy<T>, LifecycleEventObserver {
    private var cached: T? = null

    override val value: T
        get() {
            return cached ?: run {
                val newValue = initializer(fragment.requireView())
                cached = newValue
                fragment.viewLifecycleOwner.lifecycle.addObserver(this)
                newValue
            }
        }

    override fun isInitialized() = cached != null

    override fun toString() = cached.toString()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            cached = null
        }
    }
}