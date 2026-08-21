package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.common.AppInitializer
import io.github.droidkaigi.confsched.core.common.DeepLinkStore
import io.github.droidkaigi.confsched.core.common.SoilDataContext

interface AppGraph : SoilDataContext {
    val uiGraph: UiGraph

    val appInitializer: AppInitializer

    // Platform entry points (an Android intent, a web URL) emit into it before the UI exists.
    val deepLinkStore: DeepLinkStore
    val rootTabNavigator: RootTabNavigator
    val rootTabBarAppearance: RootTabBarAppearance
}
