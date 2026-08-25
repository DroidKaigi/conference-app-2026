package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.preview.PreviewImageResolver
import io.github.droidkaigi.confsched.core.preview.PreviewImageResolverDefaults
import io.github.droidkaigi.confsched.core.preview.impl.DefaultPreviewImageResolver

@ContributesTo(AppScope::class, replaces = [PreviewImageResolverDefaults::class])
interface AndroidPreviewImageResolverBindings {
    @Provides
    fun providePreviewImageResolver(): PreviewImageResolver = DefaultPreviewImageResolver()
}
