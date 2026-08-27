package io.github.droidkaigi.confsched.app

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.droidkaigi.confsched.core.common.DeepLink
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItemId

internal fun Intent.toDeepLink(): DeepLink? = data?.toString()?.let(DeepLink::parse)

/** An explicit intent, so the link opens this app without offering a chooser. */
fun favoriteSessionDeepLinkIntent(context: Context, id: TimetableItemId): Intent =
    Intent(
        Intent.ACTION_VIEW,
        "${DeepLink.SCHEME}://${DeepLink.FAVORITES_HOST}/${DeepLink.SESSION_HOST}/${id.value}".toUri(),
    ).setClass(context, MainActivity::class.java)

/** An explicit intent, so the link opens this app without offering a chooser. */
fun favoritesDeepLinkIntent(context: Context): Intent =
    Intent(Intent.ACTION_VIEW, "${DeepLink.SCHEME}://${DeepLink.FAVORITES_HOST}".toUri())
        .setClass(context, MainActivity::class.java)

/** An explicit intent, so the link opens this app without offering a chooser. */
fun aboutDeepLinkIntent(context: Context): Intent =
    Intent(Intent.ACTION_VIEW, "${DeepLink.SCHEME}://${DeepLink.ABOUT_HOST}".toUri())
        .setClass(context, MainActivity::class.java)

/** An explicit intent, so the link opens this app without offering a chooser. */
fun timetableDayDeepLinkIntent(context: Context, day: DroidKaigi2026Day): Intent {
    val segment = when (day) {
        DroidKaigi2026Day.Day1 -> DeepLink.DAY1_SEGMENT
        DroidKaigi2026Day.Day2 -> DeepLink.DAY2_SEGMENT
    }
    return Intent(Intent.ACTION_VIEW, "${DeepLink.SCHEME}://${DeepLink.TIMETABLE_HOST}/$segment".toUri())
        .setClass(context, MainActivity::class.java)
}
