package ru.dl.checklist.app.ext

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.Navigation

fun Fragment.setActivityTitle(@StringRes id: Int) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = getString(id)
}

fun Fragment.setActivityTitle(title: String) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}


/**
 * A lazy property that gets cleaned up when the fragment's view is destroyed.
 *
 * Accessing this variable while the fragment's view is destroyed will throw NPE.
 */
fun <T : Any> Fragment.viewLifecycleLazy(initializer: (View) -> T): Lazy<T> =
    ViewLifecycleLazy(this, initializer)

private class ViewLifecycleLazy<out T : Any>(
    private val fragment: Fragment,
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

fun Fragment.navigateExt(directions: NavDirections) {
    view?.let { Navigation.findNavController(it).navigate(directions) }
}