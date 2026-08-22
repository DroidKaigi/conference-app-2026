package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.instantNavTransition

@ContributesIntoSet(UiScope::class)
@Inject
class AboutNavEntryProvider(
    private val screenGraphFactory: AboutScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<AboutNavKey>(metadata = instantNavTransition()) {
            val graph = retain(screenGraphFactory::createAboutScreenGraph)
            val uriHandler = LocalUriHandler.current
            context(graph.screenContext) {
                AboutScreenRoot(
                    onNavigateToSponsors = graph.screenNavigator::openSponsors,
                    onNavigateToContributors = graph.screenNavigator::openContributors,
                    onNavigateToStaff = graph.screenNavigator::openStaff,
                    onNavigateToLicenses = graph.screenNavigator::openLicenses,
                    onOpenCodeOfConduct = { uriHandler.openUri(CODE_OF_CONDUCT_URL) },
                    onOpenPrivacyPolicy = { uriHandler.openUri(PRIVACY_POLICY_URL) },
                    onOpenYoutube = { uriHandler.openUri(YOUTUBE_URL) },
                    onOpenX = { uriHandler.openUri(X_URL) },
                    onOpenMedium = { uriHandler.openUri(MEDIUM_URL) },
                    isDebugMenuAvailable = graph.screenNavigator.isDebugMenuAvailable,
                    onNavigateToDebug = graph.screenNavigator::openDebug,
                )
            }
        }
    }
}

private const val CODE_OF_CONDUCT_URL = "https://portal.droidkaigi.jp/about/code-of-conduct"
private const val PRIVACY_POLICY_URL = "https://portal.droidkaigi.jp/about/privacy"
private const val YOUTUBE_URL = "https://www.youtube.com/c/droidkaigi"
private const val X_URL = "https://x.com/DroidKaigi"
private const val MEDIUM_URL = "https://medium.com/droidkaigi"
