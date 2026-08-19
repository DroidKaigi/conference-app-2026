package io.github.droidkaigi.confsched.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.droidkaigi.confsched.core.common.NavigatorEffect
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.rememberRootSceneStrategy
import io.github.droidkaigi.confsched.core.common.rememberSnackbarNavEntryDecorator
import io.github.droidkaigi.confsched.core.common.retainNavEntryDecorator
import io.github.droidkaigi.confsched.core.designsystem.KaigiTheme
import io.github.droidkaigi.confsched.core.ui.SetupRemoteImageLoader
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.SwrClientProvider
import soil.query.compose.rememberSubscription
import kotlin.random.Random

private val appSketchBaseSeed = Random.nextInt()

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
context(appGraph: AppGraph)
fun KaigiApp() {
    val uiGraph = retain { appGraph.uiGraph }
    val backStack = context(uiGraph) { rememberKaigiBackStack() }

    uiGraph.backStackDebuggingEffect(backStack)
    uiGraph.semanticsDebuggingEffect()

    SetupRemoteImageLoader()

    SwrClientProvider(client = uiGraph.swrClient) {
        SoilDataBoundary(
            state = rememberSubscription(uiGraph.themeColorSchemeSubscriptionKey),
        ) { colorScheme ->
            KaigiTheme(
                colorScheme = colorScheme,
                sketchBaseSeed = appSketchBaseSeed,
            ) {
                NavigatorEffect(
                    navigator = uiGraph.appNavigator,
                    backStack = backStack,
                    logger = uiGraph.logger,
                )
                IosTabBarSyncEffect(
                    backStack = backStack,
                    rootTabNavigator = appGraph.rootTabNavigator,
                    rootTabBarAppearance = appGraph.rootTabBarAppearance,
                    colorScheme = colorScheme,
                    onSelectTab = { tab -> uiGraph.appNavigator.moveToTop(tab.key) },
                )

                // A themed backdrop behind the transition: while entries cross-fade, both are
                // translucent and the bare window (white) would show through as a flash.
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = uiGraph.appNavigator::back,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            retainNavEntryDecorator(),
                            rememberSnackbarNavEntryDecorator(),
                        ),
                        sceneStrategies = listOf(
                            rememberRootSceneStrategy(),
                            rememberListDetailSceneStrategy(),
                            SinglePaneSceneStrategy(),
                        ),
                        sceneDecoratorStrategies = listOfNotNull(
                            rememberRootTabSceneDecorator(
                                backStack = backStack,
                                onSelectTab = { tab -> uiGraph.appNavigator.moveToTop(tab.key) },
                            ),
                        ),
                        entryProvider = uiGraph.appEntryProvider.entryProvider,
                    )
                }

                uiGraph.soilErrorMonitor.Overlay()
                uiGraph.clockOverlay.Overlay()
            }
        }
    }
}
