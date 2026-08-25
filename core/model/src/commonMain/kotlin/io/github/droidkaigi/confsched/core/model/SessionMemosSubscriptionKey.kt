package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentMap
import soil.query.SubscriptionKey

typealias SessionMemosSubscriptionKey = SubscriptionKey<PersistentMap<TimetableItemId, String>>
