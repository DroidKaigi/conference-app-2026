package io.github.droidkaigi.confsched.core.common

/**
 * Whether the guidance that follows an added favorite is still to be offered. The desktop and the
 * web post no notifications and have no home screen widget, so they never offer it.
 */
fun shouldOfferFirstFavoriteGuidance(guidanceConsumed: Boolean): Boolean =
    !guidanceConsumed && (currentPlatform == TargetPlatform.Android || currentPlatform == TargetPlatform.Ios)
