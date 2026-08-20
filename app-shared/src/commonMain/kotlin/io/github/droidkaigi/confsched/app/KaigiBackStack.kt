package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey

@Composable
context(uiGraph: UiGraph)
internal fun rememberKaigiBackStack(): NavBackStack<NavKey> {
    val initialStack = remember {
        uiGraph.initialNavKeyOverrideProvider.initialNavStackOverride
            ?: listOf(TimetableNavKey)
    }
    return rememberNavBackStack(
        configuration = remember(uiGraph.navKeySerializersProvider) {
            SavedStateConfiguration {
                serializersModule = uiGraph.navKeySerializersProvider.serializersModule
            }
        },
        *initialStack.toTypedArray(),
    )
}
