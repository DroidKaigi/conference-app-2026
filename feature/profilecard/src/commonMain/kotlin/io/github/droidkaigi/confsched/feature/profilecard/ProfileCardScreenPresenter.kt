package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable

@Composable
context(_: ProfileCardPresenterContext)
fun profileCardScreenPresenter(): ProfileCardScreenUiState {
    return ProfileCardScreenUiState.Form()
}
