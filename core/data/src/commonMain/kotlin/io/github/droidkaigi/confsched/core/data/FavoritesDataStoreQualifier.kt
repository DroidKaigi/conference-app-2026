package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.Qualifier

/** Qualifies the favorites `DataStore<Preferences>`, which holds the favorited session ids. */
@Qualifier
annotation class FavoritesDataStoreQualifier

internal const val FAVORITES_DATA_STORE_FILE_NAME = "confsched2026.favorites.preferences_pb"
