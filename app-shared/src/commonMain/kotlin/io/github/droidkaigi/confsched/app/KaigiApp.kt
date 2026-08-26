package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.retain.retain
import io.github.droidkaigi.confsched.core.common.NavigatorEffect
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.designsystem.KaigiTheme
import io.github.droidkaigi.confsched.core.preview.LocalPreviewImageResolver
import io.github.droidkaigi.confsched.core.ui.SetupRemoteImageLoader
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.SwrClientProvider
import soil.query.compose.rememberSubscription
import kotlin.random.Random

private val appSketchBaseSeed = Random.nextInt()

@Composable
context(appGraph: AppGraph)
fun KaigiApp() {
    val uiGraph = retain { appGraph.uiGraph }
    val backStack = context(uiGraph) { rememberKaigiBackStack() }

    uiGraph.historySyncEffect(backStack)
    uiGraph.backStackDebuggingEffect(backStack)
    uiGraph.semanticsDebuggingEffect()

    SetupRemoteImageLoader()

    CompositionLocalProvider(LocalPreviewImageResolver provides uiGraph.previewImageResolver) {
        SwrClientProvider(client = uiGraph.swrClient) {
            SoilDataBoundary(
                state = rememberSubscription(uiGraph.appearanceSubscriptionKey),
            ) { appearance ->
                KaigiTheme(
                    colorScheme = appearance.colorScheme,
                    fontFamily = appearance.settings.fontFamily,
                    sketchStrength = appearance.settings.sketchStrength,
                    sketchBaseSeed = appSketchBaseSeed,
                ) {
                    NavigatorEffect(
                        navigator = uiGraph.appNavigator,
                        backStack = backStack,
                        logger = uiGraph.logger,
                    )
                    DeepLinkEffect(
                        deepLinkStore = uiGraph.deepLinkStore,
                        backStack = backStack,
                        logger = uiGraph.logger,
                        onNavigate = uiGraph.appNavigator::moveToTop,
                    )
                    IosTabBarSyncEffect(
                        backStack = backStack,
                        rootTabNavigator = appGraph.rootTabNavigator,
                        rootTabBarAppearance = appGraph.rootTabBarAppearance,
                        colorScheme = appearance.colorScheme,
                        onSelectTab = { tab -> uiGraph.appNavigator.moveToTop(tab.key) },
                    )

                    KaigiNavDisplay(
                        backStack = backStack,
                        onBack = uiGraph.appNavigator::back,
                        onSelectTab = { tab -> uiGraph.appNavigator.moveToTop(tab.key) },
                        entryProvider = uiGraph.appEntryProvider.entryProvider,
                    )

                    uiGraph.soilErrorMonitor.Overlay()
                    uiGraph.clockOverlay.Overlay()
                }
            }
        }
    }
}
