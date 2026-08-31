package io.github.droidkaigi.confsched.core.preview

/**
 * Keeps a preview out of the desktop preview screenshot tests while leaving it available to the
 * tooling. The capture requires a single composition root, so a preview that opens a `Dialog` or
 * any other composable owning a root of its own carries this.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenshotTestExclude
