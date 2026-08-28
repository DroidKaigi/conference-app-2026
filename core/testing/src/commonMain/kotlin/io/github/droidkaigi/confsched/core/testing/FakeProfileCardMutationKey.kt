package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.ProfileCardMutationKey
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeProfileCardMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<ProfileCard>,
) : ProfileCardMutationKey by buildMutationKey(
    id = MutationId("fake-profile-card-${extraTag.value}"),
    mutate = { card -> state.record(card) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState())

    val invocations: Channel<ProfileCard> get() = state.invocations

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
