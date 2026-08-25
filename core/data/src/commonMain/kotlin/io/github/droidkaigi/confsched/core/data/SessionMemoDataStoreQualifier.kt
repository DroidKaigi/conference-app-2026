package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.Qualifier

/** Qualifies the session memo `DataStore<Preferences>`, whose keys are session ids. */
@Qualifier
annotation class SessionMemoDataStoreQualifier

internal const val SESSION_MEMO_DATA_STORE_FILE_NAME = "confsched2026.sessionMemos.preferences_pb"
