package io.github.droidkaigi.confsched.app.ios

import androidx.compose.ui.window.ComposeUIViewController
import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.app.IosAppGraph
import io.github.droidkaigi.confsched.app.KaigiApp
import io.github.droidkaigi.confsched.app.RootTab
import io.github.droidkaigi.confsched.app.RootTabBarPalette
import io.github.droidkaigi.confsched.core.common.context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import platform.UIKit.UIViewController

// Swift Export drops @Composable from the function types it bridges, so every declaration Swift
// reaches must stay free of Compose types; the graph is held privately for that reason.
class KaigiAppHost(swiftPackageLicensesJson: String) {

    private val graph: IosAppGraph =
        createGraphFactory<IosAppGraph.Factory>().create(swiftPackageLicensesJson)

    val currentTab: Flow<RootTabSelection?> = graph.rootTabNavigator.currentTab.map { tab ->
        tab?.let(::RootTabSelection)
    }

    val tabBarPalette: Flow<RootTabBarPalette?> = graph.rootTabBarAppearance.palette

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun initialize() {
        graph.appInitializer.initialize()
        graph.sessionReminderNotificationDelegate.install()
        graph.sessionReminderSync.start(applicationScope)
    }

    fun selectTab(tab: RootTab) {
        graph.rootTabNavigator.select(tab)
    }

    fun viewController(): UIViewController = ComposeUIViewController {
        context(graph) {
            KaigiApp()
        }
    }
}

// Swift Export's flow iterator casts every element through its class bridge, which a Kotlin enum
// (bridged as a Swift enum, a value type) fails at runtime; a class element crosses intact.
class RootTabSelection internal constructor(val tab: RootTab)
