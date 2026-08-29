package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.AppError
import io.github.droidkaigi.confsched.core.common.UserMessage
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.error_internet_connection
import io.github.droidkaigi.confsched.core.ui.generated.resources.error_network
import io.github.droidkaigi.confsched.core.ui.generated.resources.error_server
import io.github.droidkaigi.confsched.core.ui.generated.resources.error_timeout
import io.github.droidkaigi.confsched.core.ui.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

val AppError.messageResource: StringResource
    get() = when (this) {
        is AppError.ApiException.NetworkException -> Res.string.error_network
        is AppError.ApiException.ServerException -> Res.string.error_server
        is AppError.ApiException.TimeoutException -> Res.string.error_timeout
        is AppError.InternetConnectionException -> Res.string.error_internet_connection
        is AppError.UnknownException -> Res.string.error_unknown
    }

@Composable
fun AppError.localizedMessage(): String = stringResource(messageResource)

suspend fun SnackbarHostState.showSnackbar(message: UserMessage) {
    showSnackbar(getString(message.error.messageResource))
}
