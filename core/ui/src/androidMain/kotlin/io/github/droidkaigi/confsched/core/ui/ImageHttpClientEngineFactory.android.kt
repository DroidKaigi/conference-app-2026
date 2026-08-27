package io.github.droidkaigi.confsched.core.ui

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun imageHttpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp
