package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoodleStorePersistenceTest {

    private val directory: File = Files.createTempDirectory("doodle").toFile()
    private val fileStorage = TemporaryDirectoryFileStorage(directory)

    private val doodle = Doodle(
        strokes = listOf(
            DoodleStroke(points = listOf(DoodlePoint(x = -12.5f, y = 30f), DoodlePoint(x = 8f, y = 44.25f))),
        ),
    )

    @Test
    fun a_saved_doodle_is_read_by_the_next_store() = runTest {
        DoodleStore(fileStorage).save(doodle)

        assertTrue(directory.listFiles().orEmpty().isNotEmpty(), "no doodle file was written")
        assertEquals(doodle, DoodleStore(fileStorage).doodle().first())
    }

    @Test
    fun saving_an_empty_doodle_deletes_the_stored_one() = runTest {
        DoodleStore(fileStorage).save(doodle)

        DoodleStore(fileStorage).save(Doodle.Empty)

        assertEquals(Doodle.Empty, DoodleStore(fileStorage).doodle().first())
    }

    @Test
    fun clear_leaves_no_doodle_behind() = runTest {
        DoodleStore(fileStorage).save(doodle)

        DoodleStore(fileStorage).clear()

        assertEquals(Doodle.Empty, DoodleStore(fileStorage).doodle().first())
    }
}

private class TemporaryDirectoryFileStorage(private val directory: File) : FileStorage {
    private fun fileFor(key: String) = File(directory, key.encodeToByteArray().joinToString("") { "%02x".format(it) })

    override suspend fun get(key: String): ByteArray? = fileFor(key).takeIf { it.exists() }?.readBytes()

    override suspend fun put(key: String, bytes: ByteArray) {
        directory.mkdirs()
        fileFor(key).writeBytes(bytes)
    }

    override suspend fun delete(key: String) {
        fileFor(key).delete()
    }

    override suspend fun clear() {
        directory.listFiles()?.forEach { it.delete() }
    }
}
