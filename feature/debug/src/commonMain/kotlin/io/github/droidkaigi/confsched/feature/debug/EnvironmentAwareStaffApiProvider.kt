package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.data.DefaultStaffApiProvider
import io.github.droidkaigi.confsched.core.data.FakeStaffApi
import io.github.droidkaigi.confsched.core.data.KtorfitFactory
import io.github.droidkaigi.confsched.core.data.ServerEnvironment
import io.github.droidkaigi.confsched.core.data.ServerEnvironmentStore
import io.github.droidkaigi.confsched.core.data.StaffApi
import io.github.droidkaigi.confsched.core.data.StaffApiProvider
import io.github.droidkaigi.confsched.core.data.StaffListResponse
import io.github.droidkaigi.confsched.core.data.createStaffApi

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultStaffApiProvider::class])
class EnvironmentAwareStaffApiProvider(
    private val ktorfitFactory: KtorfitFactory,
    private val store: ServerEnvironmentStore,
) : StaffApiProvider {

    override val api: StaffApi = EnvironmentAwareStaffApi()

    private inner class EnvironmentAwareStaffApi : StaffApi {
        private val fake = FakeStaffApi()
        private val remotes = mutableMapOf<ServerEnvironment, StaffApi>()

        override suspend fun getStaff(): StaffListResponse = current().getStaff()

        private fun current(): StaffApi {
            val environment = store.environment.value
            val baseUrl = environment.baseUrl ?: return fake
            return remotes.getOrPut(environment) { ktorfitFactory.create(baseUrl).createStaffApi() }
        }
    }
}
