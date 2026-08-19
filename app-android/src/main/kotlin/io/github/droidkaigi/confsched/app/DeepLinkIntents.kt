package io.github.droidkaigi.confsched.app

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.droidkaigi.confsched.core.common.DeepLink
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
