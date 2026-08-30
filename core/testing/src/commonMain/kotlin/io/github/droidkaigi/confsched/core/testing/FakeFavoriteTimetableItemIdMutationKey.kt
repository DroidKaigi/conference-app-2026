package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeFavoriteTimetableItemIdMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<TimetableItemId, Boolean>,
) : FavoriteTimetableItemIdMutationKey by buildMutationKey(
    id = MutationId("fake-favorite-${extraTag.value}"),
    mutate = { id -> state.record(id) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState<TimetableItemId, Boolean>().apply { complete(true) })

    val invocations: Channel<TimetableItemId> get() = state.invocations

    fun complete(result: Boolean) = state.complete(result)

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
