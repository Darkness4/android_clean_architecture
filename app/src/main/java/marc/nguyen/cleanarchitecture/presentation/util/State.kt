package marc.nguyen.cleanarchitecture.presentation.util

sealed class State {
    data class Loaded<out T>(val data: T) : State()
    data class Error(val e: Throwable) : State()
    object Loading : State()
}