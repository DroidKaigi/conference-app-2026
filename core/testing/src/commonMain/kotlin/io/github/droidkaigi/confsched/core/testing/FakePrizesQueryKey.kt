package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.model.PrizesQueryKey
import kotlinx.collections.immutable.persistentListOf
import soil.query.QueryId
import soil.query.buildQueryKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class, binding = binding<PrizesQueryKey>())
class FakePrizesQueryKey private constructor(
    fixture: FakeFixture<Prizes>,
) : FakeKeyControl<Prizes>(fixture),
    PrizesQueryKey by buildQueryKey(
        id = QueryId("fake-prizes"),
        fetch = { fixture.await() },
    ) {
    @Inject
    constructor() : this(FakeFixture(Prizes(items = persistentListOf())))
}
