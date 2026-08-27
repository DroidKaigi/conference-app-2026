package io.github.droidkaigi.confsched.core.ui

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

internal actual fun imageHttpClientEngineFactory(): HttpClientEngineFactory<*> = Js
