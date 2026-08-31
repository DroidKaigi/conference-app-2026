package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.AppearanceSubscriptionKey
import io.github.droidkaigi.confsched.core.model.DoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.model.ProfileCardMutationKey
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.core.model.ProfileCardSubscriptionKey

@Inject
class ProfileCardPresenterContext(
    override val logger: KaigiLogger,
    val profileCardMutationKey: ProfileCardMutationKey,
    val shareProfileCardMutationKey: ShareProfileCardMutationKey,
) : PresenterContext

@Inject
@SingleIn(ProfileCardScreenScope::class)
class ProfileCardScreenContext(
    override val logger: KaigiLogger,
    val appearanceSubscriptionKey: AppearanceSubscriptionKey,
    val profileCardSubscriptionKey: ProfileCardSubscriptionKey,
    val doodlesSubscriptionKey: DoodlesSubscriptionKey,
    val presenterContext: ProfileCardPresenterContext,
) : ScreenContext
