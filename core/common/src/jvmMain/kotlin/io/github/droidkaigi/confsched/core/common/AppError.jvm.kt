package io.github.droidkaigi.confsched.core.common

import java.net.UnknownHostException

actual fun knownPlatformExceptionOrNull(e: Throwable): AppError? =
    if (e is UnknownHostException) AppError.InternetConnectionException(e) else null
