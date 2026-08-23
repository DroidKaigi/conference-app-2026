package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsMutationKey
import io.github.droidkaigi.confsched.core.model.MutationTag
import kotlinx.coroutines.channels.Channel
import soil.query.MutationId
import soil.query.buildMutationKey

@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeAppearanceSettingsMutationKey private constructor(
    extraTag: MutationTag,
    private val state: FakeMutationState<AppearanceSettings>,
) : AppearanceSettingsMutationKey by buildMutationKey(
    id = MutationId("fake-appearance-settings-${extraTag.value}"),
    mutate = { settings -> state.record(settings) },
) {
    @Inject
    constructor(extraTag: MutationTag) : this(extraTag, FakeMutationState())

    val invocations: Channel<AppearanceSettings> get() = state.invocations

    fun failWith(throwable: Throwable) = state.failWith(throwable)
}
