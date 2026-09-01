package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentMap
import soil.query.SubscriptionKey

/** A target with no entry carries no doodle. */
typealias DoodlesSubscriptionKey = SubscriptionKey<PersistentMap<DoodleTarget, Doodle>>
