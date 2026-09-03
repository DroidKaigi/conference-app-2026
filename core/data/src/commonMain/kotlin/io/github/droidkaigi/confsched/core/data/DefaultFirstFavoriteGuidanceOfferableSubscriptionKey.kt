package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.common.currentPlatform
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceOfferableSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultFirstFavoriteGuidanceOfferableSubscriptionKey(private val store: FirstFavoriteGuidanceStore) :
    FirstFavoriteGuidanceOfferableSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.firstFavoriteGuidanceOfferableSubscription,
        subscribe = {
            // The desktop and the web post no notifications and have no home screen widget.
            if (currentPlatform == TargetPlatform.Android || currentPlatform == TargetPlatform.Ios) {
                store.consumed().map { consumed -> !consumed }
            } else {
                flowOf(false)
            }
        },
    )
