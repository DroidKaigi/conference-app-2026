package io.github.droidkaigi.confsched.feature.about

sealed interface DoodleScreenActionResult {
    data object Reloaded : DoodleScreenActionResult
}
