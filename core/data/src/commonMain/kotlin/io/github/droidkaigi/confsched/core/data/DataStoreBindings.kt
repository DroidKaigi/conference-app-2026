package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface DataStoreBindings {
    @Provides
    @SingleIn(AppScope::class)
    @SettingsDataStoreQualifier
    fun provideSettingsDataStore(pathProducer: DataStorePathProducer): DataStore<Preferences> =
        createDataStore(pathProducer, SETTINGS_DATA_STORE_FILE_NAME)

    @Provides
    @SingleIn(AppScope::class)
    @SessionMemoDataStoreQualifier
    fun provideSessionMemoDataStore(pathProducer: DataStorePathProducer): DataStore<Preferences> =
        createDataStore(pathProducer, SESSION_MEMO_DATA_STORE_FILE_NAME)

    @Provides
    @SingleIn(AppScope::class)
    @FavoritesDataStoreQualifier
    fun provideFavoritesDataStore(pathProducer: DataStorePathProducer): DataStore<Preferences> =
        createDataStore(pathProducer, FAVORITES_DATA_STORE_FILE_NAME)
}
