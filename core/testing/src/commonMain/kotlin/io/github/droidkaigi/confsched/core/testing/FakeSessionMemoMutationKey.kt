package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.SessionMemoEdit
import io.github.droidkaigi.confsched.core.model.SessionMemoMutationKey
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeSessionMemoMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<SessionMemoEdit, Unit>,
) : SessionMemoMutationKey by buildMutationKey(
    id = MutationId("fake-session-memo-${extraTag.value}"),
    mutate = { edit -> state.record(edit) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState(Unit))

    val invocations: Channel<SessionMemoEdit> get() = state.invocations

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
