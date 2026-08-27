package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.core.ui.encodeToPng
import soil.query.buildMutationKey

@Inject
@ContributesBinding(ProfileCardScreenScope::class)
class DefaultShareProfileCardMutationKey(
    extraTag: MutationTag,
) : ShareProfileCardMutationKey by buildMutationKey(
    id = SoilIds.shareProfileCardMutation(extraTag),
    mutate = { image -> ShareableProfileCardImage(image.encodeToPng()) },
)
