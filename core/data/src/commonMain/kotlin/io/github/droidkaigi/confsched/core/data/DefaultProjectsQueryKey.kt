package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.ProjectsQueryKey
import io.github.droidkaigi.confsched.core.model.SoilIds

@Inject
@ContributesBinding(AppScope::class)
class DefaultProjectsQueryKey(
    private val api: ProjectsApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
) : ProjectsQueryKey by buildPersistedQueryKey(
    id = SoilIds.projectsQuery,
    persistKey = "projects",
    fileStorage = fileStorage,
    fetchResponse = { api.getProjects() },
    transformToDomainModel = ProjectListResponse::toProjects,
    onPersisted = {},
)
