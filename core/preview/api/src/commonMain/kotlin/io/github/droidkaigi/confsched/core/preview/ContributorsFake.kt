package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.Contributor
import io.github.droidkaigi.confsched.core.model.ContributorId
import io.github.droidkaigi.confsched.core.model.Contributors
import kotlinx.collections.immutable.persistentListOf

fun Contributors.Companion.fake(): Contributors = Contributors(
    items = persistentListOf(
        fakeContributor(1L, "user-a"),
        fakeContributor(2L, "user-b"),
        fakeContributor(3L, "user-c"),
    ),
)

private fun fakeContributor(id: Long, username: String) = Contributor(
    id = ContributorId(id),
    username = username,
    iconUrl = PreviewImage.AvatarSample.imageUrl,
    profileUrl = "https://example.com/$username",
)
