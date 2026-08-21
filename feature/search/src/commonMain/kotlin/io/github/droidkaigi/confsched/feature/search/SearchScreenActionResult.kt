package io.github.droidkaigi.confsched.feature.search

sealed interface SearchScreenActionResult {
    data object Reloaded : SearchScreenActionResult
}
