package io.github.droidkaigi.confsched.core.common

import io.ktor.client.engine.HttpClientEngineFactory

// Ktor picks an engine by itself only when exactly one is linked. Dev builds link a second one
// through :feature:debug, and on Kotlin/Native the loser of that race, CIO, cannot do TLS at all.
expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>
