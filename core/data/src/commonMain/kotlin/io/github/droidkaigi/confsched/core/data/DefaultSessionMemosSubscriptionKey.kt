package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.SessionMemosSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultSessionMemosSubscriptionKey(private val store: SessionMemoStore) :
    SessionMemosSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.sessionMemosSubscription,
        subscribe = { store.memos() },
    )
