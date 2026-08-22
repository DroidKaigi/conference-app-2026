package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class PersistedDataResetter(
    private val themeStore: ThemeStore,
    private val favoritesStore: FavoritesStore,
    private val sessionMemoStore: SessionMemoStore,
    private val fileStorage: FileStorage,
) {
    suspend fun clearAll() {
        themeStore.clear()
        favoritesStore.clear()
        sessionMemoStore.clear()
        fileStorage.clear()
    }
}
