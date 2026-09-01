package io.github.droidkaigi.confsched.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstFavoriteGuidanceStorePersistenceTest {

    private val directory = Files.createTempDirectory("guidance").toFile()
    private val file = File(directory, SETTINGS_DATA_STORE_FILE_NAME)

    // DataStore refuses a second instance over a live file, and frees the one it holds when the
    // scope it was given completes rather than when the cancel is asked for.
    private suspend fun <T> TestScope.withStore(block: suspend (FirstFavoriteGuidanceStore) -> T): T {
        val job = SupervisorJob()
        val scope = CoroutineScope(job + StandardTestDispatcher(testScheduler))
        try {
            return block(
                FirstFavoriteGuidanceStore(PreferenceDataStoreFactory.createWithPath(scope = scope) { file.path.toPath() }),
            )
        } finally {
            job.cancelAndJoin()
        }
    }

    @Test
    fun a_flag_consumed_by_one_store_is_read_by_the_next() = runTest {
        withStore { assertFalse(it.consumed().first()) }

        withStore { it.consume() }

        withStore { assertTrue(it.consumed().first()) }
    }

    @Test
    fun an_unreadable_file_reads_as_not_consumed() = runTest {
        file.writeBytes(byteArrayOf(0x00, 0x01, 0x02, 0x03))

        withStore { assertFalse(it.consumed().first()) }
    }
}
