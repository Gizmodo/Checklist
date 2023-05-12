package ru.dl.checklist.app.utils


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

sealed class SD<out T : Any> {
    data class Success<T : Any>(val result: T) : SD<T>()
    data class Error(val msg: String) : SD<Nothing>()
    object Loading : SD<Nothing>()

    inline fun <R : Any> map(transform: (T) -> R): SD<R> {
        return when (this) {
            is Loading -> Loading
            is Error -> Error(this.msg)
            is Success -> Success(transform(this.result))
        }
    }

    suspend inline fun <R : Any> suspendMap(crossinline transform: suspend (T) -> R): SD<R> {
        return when (this) {
            is Loading -> Loading
            is Error -> Error(this.msg)
            is Success -> Success(transform(this.result))
        }
    }
}

fun <T : Any> Flow<T>.asSD(): Flow<SD<T>> = wrapWithSD().catch {
    emit(SD.Error(it.message ?: "There was an error"))
}

fun <T : Any> Flow<T>.wrapWithSD(): Flow<SD<T>> = transform { value ->
    return@transform emit(SD.Success(value))
}

inline fun <T : Any, R : Any> Flow<SD<T>>.mapState(crossinline transform: suspend (value: T) -> R): Flow<SD<R>> =
    transform { value ->
        return@transform emit(value.suspendMap(transform))
    }

inline fun <T : Any> Flow<SD<T>>.onSuccessState(crossinline action: suspend (value: T) -> Unit): Flow<SD<T>> =
    onEach {
        if (it is SD.Success) action(it.result)
    }

inline fun <T : Any> Flow<SD<T>>.onErrorState(crossinline action: suspend (error: String) -> Unit): Flow<SD<T>> =
    onEach {
        if (it is SD.Error) action(it.msg)
    }
