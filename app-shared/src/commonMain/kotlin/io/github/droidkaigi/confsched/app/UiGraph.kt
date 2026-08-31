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
import io.github.droidkaigi.confsched.core.data.FavoritesStore
import io.github.droidkaigi.confsched.core.data.FirstFavoriteGuidanceStore
import io.github.droidkaigi.confsched.core.data.PersistedTimetableReader
import io.github.droidkaigi.confsched.core.model.AppearanceSubscriptionKey
import io.github.droidkaigi.confsched.core.preview.PreviewImageResolver
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableDayRequestStore
import soil.query.SwrClientPlus

@GraphExtension(UiScope::class)
interface UiGraph {
    val appNavigator: AppNavigator
    val appEntryProvider: AppEntryProvider
    val deepLinkStore: DeepLinkStore
    val timetableDayRequestStore: TimetableDayRequestStore
    val favoritesStore: FavoritesStore
    val firstFavoriteGuidanceStore: FirstFavoriteGuidanceStore
    val persistedTimetableReader: PersistedTimetableReader

    val historySyncEffect: HistorySyncEffect
    val initialNavKeyOverrideProvider: InitialNavKeyOverrideProvider
    val navKeySerializersProvider: MergedNavKeySerializersProvider

    val logger: KaigiLogger
    val backStackDebuggingEffect: BackStackDebuggingEffect
    val semanticsDebuggingEffect: SemanticsDebuggingEffect
    val clockOverlay: ClockOverlay
    val soilErrorMonitor: SoilErrorMonitor
    val swrClient: SwrClientPlus
    val appearanceSubscriptionKey: AppearanceSubscriptionKey
    val previewImageResolver: PreviewImageResolver
}
