package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.NotificationPermissionMutationKey
import io.github.droidkaigi.confsched.core.model.NotificationPermissionResult
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeNotificationPermissionMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<Unit, NotificationPermissionResult>,
) : NotificationPermissionMutationKey by buildMutationKey(
    id = MutationId("fake-notification-permission-${extraTag.value}"),
    mutate = { state.record(Unit) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState(NotificationPermissionResult.Enabled))

    val invocations: Channel<Unit> get() = state.invocations

    fun complete(result: NotificationPermissionResult) = state.complete(result)

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
