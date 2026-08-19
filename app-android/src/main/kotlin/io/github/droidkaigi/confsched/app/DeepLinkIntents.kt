package io.github.droidkaigi.confsched.app

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.model.TimetableItemId

internal const val DEEP_LINK_SCHEME = "droidkaigi"
internal const val DEEP_LINK_SESSION_HOST = "session"

internal fun Intent.toDeepLink(): DeepLink? {
    val uri = data ?: return null
    if (uri.scheme != DEEP_LINK_SCHEME) return null
    return when (uri.host) {
        DEEP_LINK_SESSION_HOST ->
            uri.lastPathSegment?.takeIf(String::isNotEmpty)?.let(DeepLink::SessionDetail)

        else -> null
    }
}

/** An explicit intent, so the link opens this app without offering a chooser. */
fun sessionDeepLinkIntent(context: Context, id: TimetableItemId): Intent =
    Intent(Intent.ACTION_VIEW, "$DEEP_LINK_SCHEME://$DEEP_LINK_SESSION_HOST/${id.value}".toUri())
        .setClass(context, MainActivity::class.java)
