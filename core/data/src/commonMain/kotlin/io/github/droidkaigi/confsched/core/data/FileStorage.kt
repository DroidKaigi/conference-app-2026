package io.github.droidkaigi.confsched.core.data

interface FileStorage {
    suspend fun get(key: String): ByteArray?

    suspend fun put(key: String, bytes: ByteArray)

    suspend fun delete(key: String)

    suspend fun clear()
}
