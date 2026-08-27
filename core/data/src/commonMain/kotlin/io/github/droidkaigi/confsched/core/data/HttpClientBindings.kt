package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.httpClientEngineFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
interface HttpClientBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpClient(): HttpClient = HttpClient(httpClientEngineFactory()) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        installAppUserAgent()
    }
}
