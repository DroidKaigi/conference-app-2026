package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceMutationKey
import io.github.droidkaigi.confsched.core.model.MutationTag
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeFirstFavoriteGuidanceMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<Unit, Unit>,
) : FirstFavoriteGuidanceMutationKey by buildMutationKey(
    id = MutationId("fake-first-favorite-guidance-${extraTag.value}"),
    mutate = { state.record(Unit) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState(Unit))

    val invocations: Channel<Unit> get() = state.invocations

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
