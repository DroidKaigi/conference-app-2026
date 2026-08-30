package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.Staff
import io.github.droidkaigi.confsched.core.model.StaffId
import io.github.droidkaigi.confsched.core.model.StaffMember
import kotlinx.collections.immutable.persistentListOf

fun Staff.Companion.fake(): Staff = Staff(
    items = persistentListOf(
        fakeStaffMember(1L, "staff-a"),
        fakeStaffMember(2L, "staff-b"),
        fakeStaffMember(3L, "staff-c"),
    ),
)

private fun fakeStaffMember(id: Long, username: String) = StaffMember(
    id = StaffId(id),
    username = username,
    iconUrl = PreviewImage.AvatarSample.imageUrl,
    profileUrl = "https://example.com/$username",
)
