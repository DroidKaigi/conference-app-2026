package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import kotlin.test.Test
import kotlin.test.assertEquals

class TimetableResponseMapperTest {

    @Test
    fun a_session_takes_the_room_the_payload_names() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81666L, "Meerkat")),
            sessions = listOf(sessionResponse("s1", roomId = 81666L, language = LanguageResponse.JAPANESE)),
        ).toTimetableItems()

        assertEquals(Room.MEERKAT, items.single().room)
    }

    @Test
    fun a_session_in_a_room_this_app_does_not_know_is_dropped() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(99999L, "Somewhere Else"), roomResponse(81666L, "Meerkat")),
            sessions = listOf(
                sessionResponse("s1", roomId = 99999L, language = LanguageResponse.ENGLISH),
                sessionResponse("s2", roomId = 81666L, language = LanguageResponse.ENGLISH),
            ),
        ).toTimetableItems()

        assertEquals(listOf("s2"), items.map { it.id.value })
    }

    @Test
    fun timestamps_reach_the_timetable_as_wall_clock_times() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81666L, "Meerkat")),
            sessions = listOf(sessionResponse("s1", roomId = 81666L, language = LanguageResponse.JAPANESE)),
        ).toTimetableItems()

        assertEquals("10:00" to "10:40", items.single().startsAt to items.single().endsAt)
    }

    @Test
    fun a_room_left_unnamed_in_japanese_is_matched_on_its_english_name() {
        val items = timetableResponse(
            rooms = listOf(RoomResponse(name = LocaledResponse(ja = "", en = "Quail"), id = 81670L, sort = 4)),
            sessions = listOf(sessionResponse("s1", roomId = 81670L, language = LanguageResponse.MIXED)),
        ).toTimetableItems()

        assertEquals(Room.QUAIL, items.single().room)
    }

    @Test
    fun each_language_the_payload_can_carry_maps_to_its_own_value() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(
                sessionResponse("s1", roomId = 81669L, language = LanguageResponse.JAPANESE),
                sessionResponse("s2", roomId = 81669L, language = LanguageResponse.ENGLISH),
                sessionResponse("s3", roomId = 81669L, language = LanguageResponse.MIXED),
            ),
        ).toTimetableItems()

        assertEquals(
            listOf(Language.JAPANESE, Language.ENGLISH, Language.MIXED),
            items.map { it.language },
        )
    }

    @Test
    fun a_title_reaches_the_timetable_in_both_languages() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(
                sessionResponse("s1", roomId = 81669L, language = LanguageResponse.MIXED)
                    .copy(title = LocaledResponse(ja = "セッション", en = "Session")),
            ),
        ).toTimetableItems()

        assertEquals(MultiLangText(ja = "セッション", en = "Session"), items.single().title)
    }

    @Test
    fun a_title_left_untranslated_falls_back_to_the_language_the_payload_carries() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(
                sessionResponse("s1", roomId = 81669L, language = LanguageResponse.MIXED)
                    .copy(title = LocaledResponse(ja = "セッション", en = "")),
            ),
        ).toTimetableItems()

        assertEquals(MultiLangText(ja = "セッション", en = "セッション"), items.single().title)
    }

    private fun timetableResponse(
        rooms: List<RoomResponse>,
        sessions: List<SessionResponse>,
    ) = TimetableResponse(
        status = HttpStatusResponse.OK,
        sessions = sessions,
        rooms = rooms,
        speakers = emptyList(),
        categories = emptyList(),
    )

    private fun roomResponse(id: Long, name: String) = RoomResponse(
        name = LocaledResponse(ja = name, en = name),
        id = id,
        sort = 0,
    )

    private fun sessionResponse(
        id: String,
        roomId: Long,
        language: LanguageResponse,
    ) = SessionResponse(
        id = id,
        title = LocaledResponse(ja = "Session $id", en = "Session $id"),
        speakers = emptyList(),
        startsAt = "2026-09-02T10:00:00+09:00",
        endsAt = "2026-09-02T10:40:00+09:00",
        language = language,
        roomId = roomId,
        lengthInMinutes = 40,
        sessionType = SessionTypeResponse.NORMAL,
        noShow = false,
        targetAudience = LocaledResponse(ja = "All", en = "All"),
        interpretationTarget = false,
        asset = SessionAssetResponse(),
    )
}
