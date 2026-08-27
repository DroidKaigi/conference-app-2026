package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.SoilIds
import io.github.droidkaigi.confsched.core.model.StaffQueryKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultStaffQueryKey(
    private val api: StaffApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
) : StaffQueryKey by buildPersistedQueryKey(
    id = SoilIds.staffQuery,
    persistKey = "staff",
    fileStorage = fileStorage,
    fetchResponse = { api.getStaff() },
    transformToDomainModel = StaffListResponse::toStaff,
)
