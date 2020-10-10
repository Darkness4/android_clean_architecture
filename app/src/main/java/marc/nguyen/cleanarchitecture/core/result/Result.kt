package marc.nguyen.cleanarchitecture.core.result

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure<out T>(val throwable: Throwable) : Result<T>()
    val isSuccess: Boolean
        get() = this is Success
    val isFailure: Boolean
        get() = this is Failure

    fun valueOrNull(): T? {
        return if (this is Success) value
        else null
    }

    fun exceptionOrNull(): Throwable? {
        return if (this is Failure) throwable
        else null
    }
}

inline fun <R, T> Result<T>.doOnSuccess(
    onSuccess: (value: T) -> R,
): R? {
    return if (this is Result.Success) onSuccess(value)
    else null
}

inline fun <R, T> Result<T>.doOnFailure(
    onFailure: (exception: Throwable) -> R
): R? {
    return if (this is Result.Failure) onFailure(throwable)
    else null
}

inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R {
    return when (this) {
        is Result.Success -> onSuccess(value)
        is Result.Failure -> onFailure(throwable)
    }
}
