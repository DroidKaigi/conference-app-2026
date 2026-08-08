package io.github.droidkaigi.confsched.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import io.github.droidkaigi.confsched.feature.about.AboutNavKey
import io.github.droidkaigi.confsched.feature.eventmap.EventMapNavKey
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey

// Public and free of UI types so the iOS shell can drive tab selection through RootTabNavigator.
enum class RootTab(internal val key: NavKey) {
    Timetable(TimetableNavKey),
    EventMap(EventMapNavKey),
    Favorites(FavoritesNavKey),
    About(AboutNavKey),
    ProfileCard(ProfileCardNavKey),
}

private val RootTab.label: String
    get() = when (this) {
        RootTab.Timetable -> "Timetable"
        RootTab.EventMap -> "Event map"
        RootTab.Favorites -> "Favorites"
        RootTab.About -> "About"
        RootTab.ProfileCard -> "Profile"
    }

private val RootTab.icon: ImageVector
    get() = when (this) {
        RootTab.Timetable -> Icons.Filled.DateRange
        RootTab.EventMap -> Icons.Filled.Map
        RootTab.Favorites -> Icons.Filled.Favorite
        RootTab.About -> Icons.Filled.Info
        RootTab.ProfileCard -> Icons.Filled.AccountCircle
    }

private class RootTabSceneDecorator(
    private val currentKey: () -> NavKey?,
    private val onSelectTab: (RootTab) -> Unit,
) : SceneDecoratorStrategy<NavKey> {

    override fun SceneDecoratorStrategyScope<NavKey>.decorateScene(scene: Scene<NavKey>): Scene<NavKey> {
        val currentTab = RootTab.entries.firstOrNull { it.key == currentKey() }
            ?: return scene
        return RootTabScene(scene, currentTab, onSelectTab)
    }
}

private class RootTabScene(
    private val delegate: Scene<NavKey>,
    private val currentTab: RootTab,
    private val onSelectTab: (RootTab) -> Unit,
) : Scene<NavKey> by delegate {
    override val content: @Composable () -> Unit = {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    RootTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == currentTab,
                            onClick = { onSelectTab(tab) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) { delegate.content() }
        }
    }

    override fun equals(other: Any?): Boolean =
        other is RootTabScene && delegate == other.delegate && currentTab == other.currentTab

    override fun hashCode(): Int = delegate.hashCode() * 31 + currentTab.hashCode()
}

@Composable
internal fun rememberRootTabSceneDecorator(
    currentKey: () -> NavKey?,
    onSelectTab: (RootTab) -> Unit,
): SceneDecoratorStrategy<NavKey> = remember(currentKey, onSelectTab) {
    RootTabSceneDecorator(currentKey, onSelectTab)
}
