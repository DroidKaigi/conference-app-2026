package io.github.droidkaigi.confsched.feature.about

sealed interface DoodleScreenAction {
    data object Reload : DoodleScreenAction
}
