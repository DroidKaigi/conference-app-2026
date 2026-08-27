package io.github.droidkaigi.confsched.core.common

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = CIO
