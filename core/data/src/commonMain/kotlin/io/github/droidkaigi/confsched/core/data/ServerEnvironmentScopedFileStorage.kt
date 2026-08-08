package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * A [FileStorage] view that namespaces every key with the current server environment, so a
 * payload cached against one environment is never restored while another is selected. The
 * environment is read per access because it is chosen at launch (debug builds) after DI setup.
 *
 * Use this for server-derived caches; user-owned data (settings, profile images) should keep
 * using the unscoped [FileStorage].
 *
 * `clear` is delegated rather than scoped, so it clears every environment's entries.
 */
@Inject
@SingleIn(AppScope::class)
class ServerEnvironmentScopedFileStorage(
    private val fileStorage: FileStorage,
    private val serverEnvironmentStore: ServerEnvironmentStore,
) : FileStorage by fileStorage {
    private fun scopedKey(key: String): String =
        "${serverEnvironmentStore.environment.value.name}/$key"

    override suspend fun get(key: String): ByteArray? = fileStorage.get(scopedKey(key))

    override suspend fun put(key: String, bytes: ByteArray) = fileStorage.put(scopedKey(key), bytes)

    override suspend fun delete(key: String) = fileStorage.delete(scopedKey(key))
}
