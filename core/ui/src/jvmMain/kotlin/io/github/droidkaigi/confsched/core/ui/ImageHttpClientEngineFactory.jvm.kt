package io.github.droidkaigi.confsched.core.ui

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

internal actual fun imageHttpClientEngineFactory(): HttpClientEngineFactory<*> = CIO
