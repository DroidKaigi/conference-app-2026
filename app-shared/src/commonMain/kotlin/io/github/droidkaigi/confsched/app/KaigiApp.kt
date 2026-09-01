package io.github.droidkaigi.confsched.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.retain.retain
import io.github.droidkaigi.confsched.core.common.NavigatorEffect
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.designsystem.KaigiTheme
import io.github.droidkaigi.confsched.core.designsystem.LocalSketchBaseSeed
import io.github.droidkaigi.confsched.core.preview.LocalPreviewImageResolver
import io.github.droidkaigi.confsched.core.ui.LocalDeviceTiltSource
import io.github.droidkaigi.confsched.core.ui.RemoteImageLoaderEffect
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.rememberDeviceTiltSource
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

    RemoteImageLoaderEffect()

    CompositionLocalProvider(
        LocalDeviceTiltSource provides rememberDeviceTiltSource(),
        LocalPreviewImageResolver provides uiGraph.previewImageResolver,
        LocalSketchBaseSeed provides appSketchBaseSeed,
    ) {
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
                    AndroidStatusBarIconAppearanceEffect(MaterialTheme.colorScheme.inverseSurface)
                    NavigatorEffect(
                        navigator = uiGraph.appNavigator,
                        backStack = backStack,
                        logger = uiGraph.logger,
                    )
                    DeepLinkEffect(
                        deepLinkStore = uiGraph.deepLinkStore,
                        timetableDayRequestStore = uiGraph.timetableDayRequestStore,
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
