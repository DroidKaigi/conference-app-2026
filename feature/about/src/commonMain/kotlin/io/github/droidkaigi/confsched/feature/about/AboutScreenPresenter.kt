package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable

@Composable
context(presenterContext: AboutPresenterContext)
fun aboutScreenPresenter(): AboutScreenUiState {
    return AboutScreenUiState(
        versionName = presenterContext.buildConfig.versionName,
    )
}
