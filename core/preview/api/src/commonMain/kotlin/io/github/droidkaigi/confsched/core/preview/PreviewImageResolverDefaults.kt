package io.github.droidkaigi.confsched.core.preview

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

/**
 * App-graph default: the running app resolves no preview sentinel, so a build without the preview
 * drawables renders those URLs as blank images. A development build replaces this binding with the
 * resolver from `:core:preview:impl`.
 */
@ContributesTo(AppScope::class)
interface PreviewImageResolverDefaults {
    @Provides
    fun providePreviewImageResolver(): PreviewImageResolver = PreviewImageResolver { null }
}
