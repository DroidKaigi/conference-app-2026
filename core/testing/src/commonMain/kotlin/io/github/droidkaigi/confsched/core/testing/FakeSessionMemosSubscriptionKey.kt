package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.SessionMemosSubscriptionKey
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.flow
import soil.query.SubscriptionId
import soil.query.buildSubscriptionKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<SessionMemosSubscriptionKey>())
class FakeSessionMemosSubscriptionKey private constructor(
    fixture: FakeFixture<PersistentMap<TimetableItemId, String>>,
) : FakeKeyControl<PersistentMap<TimetableItemId, String>>(fixture),
    SessionMemosSubscriptionKey by buildSubscriptionKey(
        id = SubscriptionId("fake-session-memos"),
        subscribe = { flow { emit(fixture.await()) } },
    ) {
    @Inject
    constructor() : this(FakeFixture(persistentMapOf()))
}
