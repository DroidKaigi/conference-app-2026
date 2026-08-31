package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.consumeListDetailPaneInsets
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.detailPane
import io.github.droidkaigi.confsched.core.ui.rememberCalendarEventAdder
import io.github.droidkaigi.confsched.core.ui.rememberTextSharer

@ContributesIntoSet(UiScope::class)
@Inject
class TimetableItemDetailNavEntryProvider(
    private val screenGraphFactory: TimetableItemDetailScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<TimetableItemDetailNavKey>(
            metadata = detailPane() + consumeListDetailPaneInsets(WindowInsetsSides.Start),
        ) { key ->
            val graph = retain(key) { screenGraphFactory.createTimetableItemDetailScreenGraph(key.id) }
            val uriHandler = LocalUriHandler.current
            val addCalendarEvent = rememberCalendarEventAdder()
            val shareText = rememberTextSharer()
            context(graph.screenContext) {
                TimetableItemDetailScreenRoot(
                    onNavigateBack = { graph.screenNavigator.back(origin = key) },
                    onNavigateToSession = graph.screenNavigator::openSessionDetail,
                    onOpenUrl = uriHandler::openUri,
                    onAddCalendarEvent = addCalendarEvent,
                    onShareText = shareText,
                )
            }
        }
    }
}
