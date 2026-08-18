package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.data.DefaultProjectsApiProvider
import io.github.droidkaigi.confsched.core.data.FakeProjectsApi
import io.github.droidkaigi.confsched.core.data.KtorfitFactory
import io.github.droidkaigi.confsched.core.data.ProjectListResponse
import io.github.droidkaigi.confsched.core.data.ProjectsApi
import io.github.droidkaigi.confsched.core.data.ProjectsApiProvider
import io.github.droidkaigi.confsched.core.data.ServerEnvironment
import io.github.droidkaigi.confsched.core.data.ServerEnvironmentStore
import io.github.droidkaigi.confsched.core.data.createProjectsApi

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultProjectsApiProvider::class])
class EnvironmentAwareProjectsApiProvider(
    private val ktorfitFactory: KtorfitFactory,
    private val store: ServerEnvironmentStore,
) : ProjectsApiProvider {

    override val api: ProjectsApi = EnvironmentAwareProjectsApi()

    private inner class EnvironmentAwareProjectsApi : ProjectsApi {
        private val fake = FakeProjectsApi()
        private val remotes = mutableMapOf<ServerEnvironment, ProjectsApi>()

        override suspend fun getProjects(): ProjectListResponse = current().getProjects()

        private fun current(): ProjectsApi {
            val environment = store.environment.value
            val baseUrl = environment.baseUrl ?: return fake
            return remotes.getOrPut(environment) { ktorfitFactory.create(baseUrl).createProjectsApi() }
        }
    }
}
