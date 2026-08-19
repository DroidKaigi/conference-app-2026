package io.github.droidkaigi.confsched.app

import android.content.Intent
import androidx.core.net.toUri
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeepLinkIntentsTest {
    private fun viewIntent(uri: String) = Intent(Intent.ACTION_VIEW, uri.toUri())

    @Test
    fun a_session_uri_parses_to_its_session_id() {
        assertEquals(
            DeepLink.SessionDetail("abc123"),
            viewIntent("droidkaigi://session/abc123").toDeepLink(),
        )
    }

    @Test
    fun foreign_schemes_and_hosts_do_not_parse() {
        assertNull(viewIntent("https://session/abc").toDeepLink())
        assertNull(viewIntent("droidkaigi://about").toDeepLink())
        assertNull(viewIntent("droidkaigi://session/").toDeepLink())
        assertNull(Intent(Intent.ACTION_VIEW).toDeepLink())
    }

    @Test
    fun the_widget_intent_round_trips_through_the_parser() {
        val context = RuntimeEnvironment.getApplication()
        val intent = sessionDeepLinkIntent(context, TimetableItemId("abc123"))
        assertEquals(DeepLink.SessionDetail("abc123"), intent.toDeepLink())
    }
}
