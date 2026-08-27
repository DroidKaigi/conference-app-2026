package io.github.droidkaigi.confsched.app.ios

import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.data.AppearanceSettingsStore
import io.github.droidkaigi.confsched.core.data.FavoritesStore
import io.github.droidkaigi.confsched.core.data.PersistedTimetableReader
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSFileManager
import kotlin.time.Duration

private const val SNAPSHOT_FILE_NAME = "favorites-widget-snapshot.json"

/** Emitted after a snapshot reaches the App Group container, so Swift can reload the timelines. */
class FavoritesWidgetSnapshotUpdate internal constructor(val schemaVersion: Int)

internal class FavoritesWidgetSnapshotStore(
    private val appGroup: String,
    private val schemaVersion: Int,
    private val favoritesStore: FavoritesStore,
    private val appearanceSettingsStore: AppearanceSettingsStore,
    private val persistedTimetableReader: PersistedTimetableReader,
    private val kaigiClock: KaigiClock,
    private val logger: KaigiLogger,
) {
    private val json = Json { encodeDefaults = true }
    private var missingContainerReported = false

    /**
     * Writes a snapshot for the current inputs, and again whenever any of them changes. A write
     * the App Group container cannot take emits nothing, so no reload chases a file that is absent.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun updates(): Flow<FavoritesWidgetSnapshotUpdate> = combine(
        favoritesStore.favoriteIds(),
        appearanceSettingsStore.colorScheme(),
        kaigiClock.offset,
        persistedTimetableReader.updates.onStart { emit(Unit) },
    ) { favoriteIds, colorScheme, clockOffset, _ -> Inputs(favoriteIds, colorScheme, clockOffset) }.mapLatest { inputs ->
        val timetable = persistedTimetableReader.read() ?: Timetable(items = persistentListOf())
        val written = write(
            favoritesWidgetSnapshot(
                schemaVersion = schemaVersion,
                timetable = timetable,
                favoriteIds = inputs.favoriteIds,
                colorScheme = inputs.colorScheme,
                clockOffset = inputs.clockOffset,
            ),
        )
        if (written) FavoritesWidgetSnapshotUpdate(schemaVersion) else null
    }.filterNotNull()

    private fun write(snapshot: FavoritesWidgetSnapshot): Boolean {
        val path = snapshotPath() ?: return false
        val staging = path.parent!! / "$SNAPSHOT_FILE_NAME.writing"
        FileSystem.SYSTEM.createDirectories(path.parent!!)
        FileSystem.SYSTEM.write(staging) { writeUtf8(json.encodeToString(snapshot)) }
        // A widget refresh can land mid-write, so the reader only ever sees a complete file.
        FileSystem.SYSTEM.atomicMove(staging, path)
        return true
    }

    private fun snapshotPath(): Path? {
        val container = NSFileManager.defaultManager
            .containerURLForSecurityApplicationGroupIdentifier(appGroup)
            ?.path
        if (container == null) {
            if (!missingContainerReported) {
                missingContainerReported = true
                logger.warn { "App Group $appGroup has no container; the favorites widget snapshot is not written." }
            }
            return null
        }
        return "$container/$SNAPSHOT_FILE_NAME".toPath()
    }

    private class Inputs(
        val favoriteIds: Set<TimetableItemId>,
        val colorScheme: KaigiColorScheme,
        val clockOffset: Duration,
    )
}
