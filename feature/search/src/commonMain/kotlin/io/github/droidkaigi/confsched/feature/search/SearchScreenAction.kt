package io.github.droidkaigi.confsched.feature.search

sealed interface SearchScreenAction {
    data object Reload : SearchScreenAction
}
