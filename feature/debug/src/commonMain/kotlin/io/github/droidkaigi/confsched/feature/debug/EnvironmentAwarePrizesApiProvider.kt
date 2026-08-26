package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.data.DefaultPrizesApiProvider
import io.github.droidkaigi.confsched.core.data.FakePrizesApi
import io.github.droidkaigi.confsched.core.data.KtorfitFactory
import io.github.droidkaigi.confsched.core.data.PrizeListResponse
import io.github.droidkaigi.confsched.core.data.PrizesApi
import io.github.droidkaigi.confsched.core.data.PrizesApiProvider
import io.github.droidkaigi.confsched.core.data.ServerEnvironment
import io.github.droidkaigi.confsched.core.data.ServerEnvironmentStore
import io.github.droidkaigi.confsched.core.data.createPrizesApi

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultPrizesApiProvider::class])
class EnvironmentAwarePrizesApiProvider(
    private val ktorfitFactory: KtorfitFactory,
    private val store: ServerEnvironmentStore,
) : PrizesApiProvider {

    override val api: PrizesApi = EnvironmentAwarePrizesApi()

    private inner class EnvironmentAwarePrizesApi : PrizesApi {
        private val fake = FakePrizesApi()
        private val remotes = mutableMapOf<ServerEnvironment, PrizesApi>()

        override suspend fun getPrizes(): PrizeListResponse = current().getPrizes()

        private fun current(): PrizesApi {
            val environment = store.environment.value
            val baseUrl = environment.baseUrl ?: return fake
            return remotes.getOrPut(environment) { ktorfitFactory.create(baseUrl).createPrizesApi() }
        }
    }
}
