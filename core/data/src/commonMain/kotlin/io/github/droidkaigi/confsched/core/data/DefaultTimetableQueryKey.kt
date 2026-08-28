package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.SoilIds
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultTimetableQueryKey(
    private val api: TimetableApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
    private val persistedTimetableReader: PersistedTimetableReader,
) : TimetableQueryKey by buildPersistedQueryKey(
    id = SoilIds.timetableQuery,
    persistKey = TIMETABLE_PERSIST_KEY,
    fileStorage = fileStorage,
    fetchResponse = { api.getTimetable() },
    transformToDomainModel = TimetableResponse::toTimetable,
    onPersisted = persistedTimetableReader::notifyPersisted,
)
