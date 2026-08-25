package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.data.FileStorage

@Inject
@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeFileStorage : FileStorage {
    private val entries = mutableMapOf<String, ByteArray>()

    override suspend fun get(key: String): ByteArray? = entries[key]

    override suspend fun put(key: String, bytes: ByteArray) {
        entries[key] = bytes
    }

    override suspend fun delete(key: String) {
        entries.remove(key)
    }

    override suspend fun clear() {
        entries.clear()
    }
}
