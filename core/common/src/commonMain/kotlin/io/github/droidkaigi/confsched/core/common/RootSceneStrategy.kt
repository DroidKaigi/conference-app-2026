package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

class RootSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val entry = entries.lastOrNull() ?: return null
        if (RootSceneMetadataKey !in entry.metadata) return null
        return RootScene(entry)
    }

    companion object {
        fun root(): Map<String, Any> = metadata { put(RootSceneMetadataKey, true) }
    }
}

private data object RootSceneMetadataKey : NavMetadataKey<Boolean>

private class RootScene<T : Any>(private val entry: NavEntry<T>) : Scene<T> {
    override val key: Any = entry.contentKey
    override val entries: List<NavEntry<T>> = listOf(entry)

    override val previousEntries: List<NavEntry<T>> = emptyList()
    override val content: @Composable () -> Unit = { entry.Content() }

    override fun equals(other: Any?): Boolean = other is RootScene<*> && entry == other.entry

    override fun hashCode(): Int = entry.hashCode()
}

@Composable
fun <T : Any> rememberRootSceneStrategy(): SceneStrategy<T> = RootSceneStrategy()
