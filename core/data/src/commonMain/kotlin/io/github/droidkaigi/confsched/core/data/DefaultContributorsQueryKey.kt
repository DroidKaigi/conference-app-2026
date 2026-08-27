package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.ContributorsQueryKey
import io.github.droidkaigi.confsched.core.model.SoilIds

@Inject
@ContributesBinding(AppScope::class)
class DefaultContributorsQueryKey(
    private val api: ContributorsApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
) : ContributorsQueryKey by buildPersistedQueryKey(
    id = SoilIds.contributorsQuery,
    persistKey = "contributors",
    fileStorage = fileStorage,
    fetchResponse = { api.getContributors() },
    transformToDomainModel = ContributorListResponse::toContributors,
)
