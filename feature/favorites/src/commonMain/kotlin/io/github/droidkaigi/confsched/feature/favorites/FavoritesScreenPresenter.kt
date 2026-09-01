package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.rememberCurrentTime
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.ui.toTimetableTimeSlots
import io.github.droidkaigi.confsched.feature.favorites.component.FavoritesListSectionUiState
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: FavoritesPresenterContext)
fun favoritesScreenPresenter(
    screenChannel: ScreenChannel<FavoritesScreenAction, FavoritesScreenActionResult>,
    timetable: Timetable,
    offersFirstFavoriteGuidance: Boolean,
): FavoritesScreenUiState {
    val favoriteMutation = rememberMutation(presenterContext.favoriteTimetableItemIdMutationKey)
    var selectedDayFilter by retain { mutableStateOf<DroidKaigi2026Day?>(null) }
    val currentTime = presenterContext.clock.rememberCurrentTime()

    ActionEffect(screenChannel) { action ->
        when (action) {
            is FavoritesScreenAction.Bookmark -> favoriteMutation.mutateAsync(action.id)
            is FavoritesScreenAction.SelectDayFilter -> selectedDayFilter = action.day
        }
    }

    MutationErrorEffect(favoriteMutation) { error ->
        screenChannel.emit(FavoritesScreenActionResult.ShowMessage(error.toUserMessage()))
        favoriteMutation.reset()
    }

    MutationSuccessEffect(favoriteMutation) { toggle ->
        if (toggle.added && offersFirstFavoriteGuidance) {
            screenChannel.emit(FavoritesScreenActionResult.OfferFirstFavoriteGuidance(timetable.roomOf(toggle.id)))
        }
        favoriteMutation.reset()
    }

    val favoriteItems = timetable.items
        .filter { timetable.isFavorite(it.id) }
        .filter { selectedDayFilter == null || it.day == selectedDayFilter }

    return FavoritesScreenUiState(
        selectedDayFilter = selectedDayFilter,
        favoritesListSection = FavoritesListSectionUiState(
            timeSlots = favoriteItems.toTimetableTimeSlots(currentTime),
            dayHeadersVisible = selectedDayFilter == null,
        ),
    )
}
