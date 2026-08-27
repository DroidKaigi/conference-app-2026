package io.github.droidkaigi.confsched.core.common

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Js
