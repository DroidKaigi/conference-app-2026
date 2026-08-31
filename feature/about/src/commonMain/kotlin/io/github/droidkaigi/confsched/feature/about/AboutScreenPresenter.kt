package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.model.Doodle

@Composable
context(presenterContext: AboutPresenterContext)
fun aboutScreenPresenter(doodle: Doodle): AboutScreenUiState {
    return AboutScreenUiState(
        versionName = presenterContext.buildConfig.versionName,
        doodle = doodle,
    )
}
