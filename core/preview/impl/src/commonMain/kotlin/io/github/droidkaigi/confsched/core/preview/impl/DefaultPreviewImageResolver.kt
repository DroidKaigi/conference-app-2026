package io.github.droidkaigi.confsched.core.preview.impl

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.preview.NoopPreviewImageResolver
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.PreviewImageResolver
import io.github.droidkaigi.confsched.core.preview.PreviewScope
import io.github.droidkaigi.confsched.core.preview.impl.generated.resources.Res
import io.github.droidkaigi.confsched.core.preview.impl.generated.resources.avatar_sample
import io.github.droidkaigi.confsched.core.preview.impl.generated.resources.prize_photo
import io.github.droidkaigi.confsched.core.preview.impl.generated.resources.session_cover
import org.jetbrains.compose.resources.DrawableResource

@Inject
@ContributesBinding(PreviewScope::class, replaces = [NoopPreviewImageResolver::class])
class DefaultPreviewImageResolver : PreviewImageResolver {
    override fun resolve(imageUrl: String): DrawableResource? {
        val image = PreviewImage.entries.firstOrNull { it.imageUrl == imageUrl } ?: return null
        return when (image) {
            PreviewImage.PrizePhoto -> Res.drawable.prize_photo
            PreviewImage.SessionCover -> Res.drawable.session_cover
            PreviewImage.AvatarSample -> Res.drawable.avatar_sample
        }
    }
}
