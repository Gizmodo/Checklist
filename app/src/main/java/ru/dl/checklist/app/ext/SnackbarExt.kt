package ru.dl.checklist.app.ext

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG) = snack(message, length) {}
fun View.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG) =
    snack(messageRes, length) {}

/**
 * Show a snackbar with [message], execute [f] and show it
 */
inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

/**
 * Show a snackbar with [messageRes], execute [f] and show it
 */
inline fun View.snack(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    val snack = Snackbar.make(this, messageRes, length)
    snack.f()
    snack.show()
}