package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.GraphExtension
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.BackStackDebuggingEffect
import io.github.droidkaigi.confsched.core.common.ClockOverlay
import io.github.droidkaigi.confsched.core.common.DeepLinkStore
import io.github.droidkaigi.confsched.core.common.HistorySyncEffect
import io.github.droidkaigi.confsched.core.common.InitialNavKeyOverrideProvider
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.MergedNavKeySerializersProvider
import io.github.droidkaigi.confsched.core.common.SemanticsDebuggingEffect
import io.github.droidkaigi.confsched.core.common.SoilErrorMonitor
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.data.ThemeColorSchemeSubscriptionKey
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsSubscriptionKey
import soil.query.SwrClientPlus

@GraphExtension(UiScope::class)
interface UiGraph {
    val appNavigator: AppNavigator
    val appEntryProvider: AppEntryProvider
    val deepLinkStore: DeepLinkStore

    val historySyncEffect: HistorySyncEffect
    val initialNavKeyOverrideProvider: InitialNavKeyOverrideProvider
    val navKeySerializersProvider: MergedNavKeySerializersProvider

    val logger: KaigiLogger
    val backStackDebuggingEffect: BackStackDebuggingEffect
    val semanticsDebuggingEffect: SemanticsDebuggingEffect
    val clockOverlay: ClockOverlay
    val soilErrorMonitor: SoilErrorMonitor
    val swrClient: SwrClientPlus
    val themeColorSchemeSubscriptionKey: ThemeColorSchemeSubscriptionKey
    val appearanceSettingsSubscriptionKey: AppearanceSettingsSubscriptionKey
}
