package io.github.droidkaigi.confsched.core.ui

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun imageHttpClientEngineFactory(): HttpClientEngineFactory<*>
