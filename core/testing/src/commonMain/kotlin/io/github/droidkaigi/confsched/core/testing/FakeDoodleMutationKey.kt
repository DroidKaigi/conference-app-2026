package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleMutationKey
import io.github.droidkaigi.confsched.core.model.MutationTag
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeDoodleMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<List<DoodleEdit>, Unit>,
) : DoodleMutationKey by buildMutationKey(
    id = MutationId("fake-doodle-${extraTag.value}"),
    mutate = { edits -> state.record(edits) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState(Unit))

    val invocations: Channel<List<DoodleEdit>> get() = state.invocations

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
