package io.github.droidkaigi.confsched.core.ui

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual fun imageHttpClientEngineFactory(): HttpClientEngineFactory<*> = Darwin
