package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.SoilIds
import io.github.droidkaigi.confsched.core.model.SponsorsQueryKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultSponsorsQueryKey(
    private val api: SponsorApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
) : SponsorsQueryKey by buildPersistedQueryKey(
    id = SoilIds.sponsorsQuery,
    persistKey = "sponsors",
    fileStorage = fileStorage,
    fetchResponse = { api.getSponsors() },
    transformToDomainModel = SponsorListResponse::toSponsors,
)
