package io.github.droidkaigi.confsched.core.data

import kotlinx.coroutines.delay

class FakeStaffApi : StaffApi {
    override suspend fun getStaff(): StaffListResponse {
        delay(300)
        return StaffListResponse(
            status = HttpStatusResponse.OK,
            staff = listOf(
                fakeStaff(1L, "staff-a"),
                fakeStaff(2L, "staff-b"),
                fakeStaff(3L, "staff-c"),
                fakeStaff(4L, "staff-d"),
                fakeStaff(5L, "staff-e"),
            ),
        )
    }

    private fun fakeStaff(
        id: Long,
        username: String,
    ) = StaffResponse(
        id = id,
        username = username,
        iconUrl = "https://placehold.jp/128x128.png",
        icon32Url = "https://placehold.jp/32x32.png",
        icon64Url = "https://placehold.jp/64x64.png",
        icon128Url = "https://placehold.jp/128x128.png",
        profileUrl = "https://example.com/$username",
    )
}
