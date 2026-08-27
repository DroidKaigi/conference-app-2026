package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.PrizesQueryKey
import io.github.droidkaigi.confsched.core.model.SoilIds

@Inject
@ContributesBinding(AppScope::class)
class DefaultPrizesQueryKey(
    private val api: PrizesApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
) : PrizesQueryKey by buildPersistedQueryKey(
    id = SoilIds.prizesQuery,
    persistKey = "prizes",
    fileStorage = fileStorage,
    fetchResponse = { api.getPrizes() },
    transformToDomainModel = PrizeListResponse::toPrizes,
)
