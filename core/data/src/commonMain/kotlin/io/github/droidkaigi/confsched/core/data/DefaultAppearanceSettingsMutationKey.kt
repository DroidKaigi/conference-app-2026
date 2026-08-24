package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsMutationKey
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.SettingsScreenScope
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildMutationKey

@Inject
@ContributesBinding(SettingsScreenScope::class)
class DefaultAppearanceSettingsMutationKey(
    extraTag: MutationTag,
    private val store: AppearanceSettingsStore,
) : AppearanceSettingsMutationKey by buildMutationKey(
    id = SoilIds.appearanceSettingsMutation(extraTag),
    mutate = { settings -> store.save(settings) },
)
