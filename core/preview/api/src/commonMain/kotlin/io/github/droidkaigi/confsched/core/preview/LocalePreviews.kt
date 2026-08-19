package io.github.droidkaigi.confsched.core.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * Renders one preview per locale the app translates. English is the base resource set and Japanese
 * is the translated one, so a preview of locale-sensitive content carries this instead of `@Preview`.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Preview(name = "en", locale = "en")
@Preview(name = "ja", locale = "ja")
annotation class LocalePreviews

// A screen preview needs an explicit frame: a screen fills whatever viewport it is given, so
// without one it renders at the test surface's default size rather than a phone's.
const val SCREEN_PREVIEW_WIDTH_DP = 360
const val SCREEN_PREVIEW_HEIGHT_DP = 800

/**
 * Phone-sized (360dp x 800dp) variant of [LocalePreviews] for screen-level previews.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Preview(name = "en", locale = "en", widthDp = SCREEN_PREVIEW_WIDTH_DP, heightDp = SCREEN_PREVIEW_HEIGHT_DP)
@Preview(name = "ja", locale = "ja", widthDp = SCREEN_PREVIEW_WIDTH_DP, heightDp = SCREEN_PREVIEW_HEIGHT_DP)
annotation class LocaleScreenPreviews
