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
            viewIntent("droidkaigi2026://session/abc123").toDeepLink(),
        )
    }

    @Test
    fun foreign_schemes_and_hosts_do_not_parse() {
        assertNull(viewIntent("https://session/abc").toDeepLink())
        assertNull(viewIntent("droidkaigi://session/abc").toDeepLink())
        assertNull(viewIntent("droidkaigi2026://staff").toDeepLink())
        assertNull(viewIntent("droidkaigi2026://session/").toDeepLink())
        assertNull(Intent(Intent.ACTION_VIEW).toDeepLink())
    }

    @Test
    fun the_widget_intent_round_trips_through_the_parser() {
        val context = RuntimeEnvironment.getApplication()
        val intent = favoriteSessionDeepLinkIntent(context, TimetableItemId("abc123"))
        assertEquals(DeepLink.FavoriteSessionDetail("abc123"), intent.toDeepLink())
    }

    @Test
    fun the_widget_background_intent_round_trips_through_the_parser() {
        val context = RuntimeEnvironment.getApplication()
        assertEquals(DeepLink.Favorites, favoritesDeepLinkIntent(context).toDeepLink())
    }
}
