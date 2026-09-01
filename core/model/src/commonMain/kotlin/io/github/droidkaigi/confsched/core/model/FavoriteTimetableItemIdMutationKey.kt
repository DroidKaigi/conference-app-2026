package io.github.droidkaigi.confsched.core.model

import soil.query.MutationKey

typealias FavoriteTimetableItemIdMutationKey = MutationKey<FavoriteToggle, TimetableItemId>

data class FavoriteToggle(
    val id: TimetableItemId,
    val added: Boolean,
)
