package io.github.droidkaigi.confsched.core.common

import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.util.cio.ChannelReadException
import kotlinx.coroutines.TimeoutCancellationException

sealed class AppError(cause: Throwable?) : RuntimeException(cause) {
    sealed class ApiException(cause: Throwable?) : AppError(cause) {
        class NetworkException(cause: Throwable?) : ApiException(cause)

        class ServerException(cause: Throwable?) : ApiException(cause)

        class TimeoutException(cause: Throwable?) : ApiException(cause)
    }

    class InternetConnectionException(cause: Throwable?) : AppError(cause)

    class UnknownException(cause: Throwable?) : AppError(cause)
}

expect fun knownPlatformExceptionOrNull(e: Throwable): AppError?

fun Throwable.toAppError(): AppError {
    knownPlatformExceptionOrNull(this)?.let { return it }
    return when (this) {
        is AppError -> this

        is ResponseException -> AppError.ApiException.ServerException(this)

        is ChannelReadException -> AppError.ApiException.NetworkException(this)

        is TimeoutCancellationException,
        is HttpRequestTimeoutException,
        is SocketTimeoutException,
        -> AppError.ApiException.TimeoutException(this)

        else -> AppError.UnknownException(this)
    }
}
