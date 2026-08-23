package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class PersistedDataResetter(
    private val appearanceSettingsStore: AppearanceSettingsStore,
    private val favoritesStore: FavoritesStore,
    private val sessionMemoStore: SessionMemoStore,
    private val fileStorage: FileStorage,
) {
    suspend fun clearAll() {
        appearanceSettingsStore.clear()
        favoritesStore.clear()
        sessionMemoStore.clear()
        fileStorage.clear()
    }
}
