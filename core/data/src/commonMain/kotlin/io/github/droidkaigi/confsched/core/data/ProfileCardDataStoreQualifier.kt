package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.Qualifier

/** Qualifies the profile card `DataStore<Preferences>` (the single card this device owns). */
@Qualifier
annotation class ProfileCardDataStoreQualifier

internal const val PROFILE_CARD_DATA_STORE_FILE_NAME = "confsched2026.profileCard.preferences_pb"
