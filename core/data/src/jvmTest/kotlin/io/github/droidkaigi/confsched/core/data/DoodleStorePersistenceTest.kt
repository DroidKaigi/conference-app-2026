package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DoodleStorePersistenceTest {

    private val directory: File = Files.createTempDirectory("doodle").toFile()
    private val fileStorage = TemporaryDirectoryFileStorage(directory)

    private val wallDoodle = Doodle(
        strokes = listOf(
            DoodleStroke(
                points = listOf(DoodlePoint(x = -12.5f, y = 30f), DoodlePoint(x = 8f, y = 44.25f)),
                width = DoodlePenSize.Thin.width,
                ink = DoodleInk.Default,
            ),
            DoodleStroke(
                points = listOf(DoodlePoint(x = 2f, y = 18f), DoodlePoint(x = 21.5f, y = 36f)),
                width = DoodlePenSize.Normal.width,
                ink = DoodleInk.Accent,
            ),
            DoodleStroke(
                points = listOf(DoodlePoint(x = -4f, y = 52f), DoodlePoint(x = 16f, y = 60.5f)),
                width = DoodlePenSize.Normal.width,
                ink = DoodleInk.Pink,
            ),
            DoodleStroke(
                points = listOf(DoodlePoint(x = 6f, y = 70f), DoodlePoint(x = 24f, y = 82f)),
                width = DoodlePenSize.Thick.width,
                ink = DoodleInk.Chalk,
            ),
        ),
    )

    private val cardDoodle = Doodle(
        strokes = listOf(
            DoodleStroke(
                points = listOf(DoodlePoint(x = 40f, y = 250f)),
                width = DoodlePenSize.Thick.width,
                ink = DoodleInk.Chalk,
            ),
        ),
    )

    @Test
    fun a_saved_doodle_is_read_by_the_next_store() = runTest {
        DoodleStore(fileStorage).save(DoodleTarget.AboutWall, wallDoodle)

        assertTrue(directory.listFiles().orEmpty().isNotEmpty(), "no doodle file was written")
        assertEquals(wallDoodle, DoodleStore(fileStorage).doodles().first()[DoodleTarget.AboutWall])
    }

    @Test
    fun each_target_keeps_its_own_doodle() = runTest {
        val store = DoodleStore(fileStorage)
        store.save(DoodleTarget.AboutWall, wallDoodle)
        store.save(DoodleTarget.ProfileCardBack, cardDoodle)

        val doodles = DoodleStore(fileStorage).doodles().first()
        assertEquals(wallDoodle, doodles[DoodleTarget.AboutWall])
        assertEquals(cardDoodle, doodles[DoodleTarget.ProfileCardBack])
        assertNull(doodles[DoodleTarget.ProfileCardFront])
    }

    @Test
    fun saving_an_empty_doodle_deletes_the_stored_one() = runTest {
        val store = DoodleStore(fileStorage)
        store.save(DoodleTarget.AboutWall, wallDoodle)
        store.save(DoodleTarget.ProfileCardFront, cardDoodle)

        store.save(DoodleTarget.AboutWall, Doodle.Empty)

        val doodles = DoodleStore(fileStorage).doodles().first()
        assertNull(doodles[DoodleTarget.AboutWall])
        assertEquals(cardDoodle, doodles[DoodleTarget.ProfileCardFront])
    }

    @Test
    fun clear_leaves_no_doodle_behind() = runTest {
        val store = DoodleStore(fileStorage)
        store.save(DoodleTarget.AboutWall, wallDoodle)
        store.save(DoodleTarget.ProfileCardBack, cardDoodle)

        store.clear()

        assertTrue(DoodleStore(fileStorage).doodles().first().isEmpty())
    }

    @Test
    fun a_payload_that_no_longer_decodes_reads_as_no_doodle() = runTest {
        fileStorage.put("doodle/aboutwall", "not json".encodeToByteArray())

        assertNull(DoodleStore(fileStorage).doodles().first()[DoodleTarget.AboutWall])
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
