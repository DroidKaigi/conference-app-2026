package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun a_room_this_app_does_not_know_still_reaches_the_timetable() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(99999L, "Somewhere Else")),
            sessions = listOf(sessionResponse("s1", roomId = 99999L, language = LanguageResponse.ENGLISH)),
        ).toTimetableItems()

        assertEquals(Room.UNKNOWN, items.single().room)
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

    @Test
    fun each_session_type_the_payload_can_carry_maps_to_its_own_value() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(
                sessionResponse("s1", roomId = 81669L, language = LanguageResponse.MIXED, sessionType = SessionTypeResponse.CODELABS),
                sessionResponse("s2", roomId = 81669L, language = LanguageResponse.MIXED, sessionType = SessionTypeResponse.LUNCH),
            ),
        ).toTimetableItems()

        assertEquals(listOf(SessionType.CODELABS, SessionType.LUNCH), items.map { it.sessionType })
    }

    @Test
    fun a_session_takes_the_category_its_id_names() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(sessionResponse("s1", roomId = 81669L, language = LanguageResponse.MIXED, categoryItemId = 11L)),
            categories = listOf(categoryResponse(11L to "Category 1", 12L to "Category 2")),
        ).toTimetableItems()

        assertEquals(
            SessionCategory(id = 11L, name = MultiLangText(ja = "Category 1", en = "Category 1")),
            items.single().category,
        )
    }

    @Test
    fun a_session_naming_no_category_carries_none() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(sessionResponse("s1", roomId = 81669L, language = LanguageResponse.MIXED)),
            categories = listOf(categoryResponse(11L to "Category 1")),
        ).toTimetableItems()

        assertNull(items.single().category)
    }

    @Test
    fun a_category_this_app_cannot_resolve_still_reaches_the_timetable() {
        val items = timetableResponse(
            rooms = listOf(roomResponse(81669L, "Narwhal")),
            sessions = listOf(sessionResponse("s1", roomId = 81669L, language = LanguageResponse.MIXED, categoryItemId = 99L)),
            categories = listOf(categoryResponse(11L to "Category 1")),
        ).toTimetableItems()

        assertNull(items.single().category)
    }

    @Test
    fun categories_are_lifted_out_of_their_groups_in_the_order_the_payload_sorts_them() {
        val categories = timetableResponse(
            rooms = emptyList(),
            sessions = emptyList(),
            categories = listOf(categoryResponse(12L to "Category 2", 11L to "Category 1")),
        ).toSessionCategories()

        assertEquals(listOf(12L, 11L), categories.map { it.id })
    }

    private fun timetableResponse(
        rooms: List<RoomResponse>,
        sessions: List<SessionResponse>,
        categories: List<CategoryResponse> = emptyList(),
    ) = TimetableResponse(
        status = HttpStatusResponse.OK,
        sessions = sessions,
        rooms = rooms,
        speakers = emptyList(),
        categories = categories,
    )

    private fun categoryResponse(vararg items: Pair<Long, String>) = CategoryResponse(
        id = 1L,
        title = LocaledResponse(ja = "カテゴリ", en = "Category"),
        sort = 1,
        items = items.mapIndexed { index, (id, name) ->
            CategoryItemResponse(id = id, name = LocaledResponse(ja = name, en = name), sort = index)
        },
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
        sessionType: SessionTypeResponse = SessionTypeResponse.NORMAL,
        categoryItemId: Long? = null,
    ) = SessionResponse(
        id = id,
        title = LocaledResponse(ja = "Session $id", en = "Session $id"),
        speakers = emptyList(),
        startsAt = "2026-09-02T10:00:00+09:00",
        endsAt = "2026-09-02T10:40:00+09:00",
        language = language,
        roomId = roomId,
        lengthInMinutes = 40,
        sessionType = sessionType,
        noShow = false,
        targetAudience = LocaledResponse(ja = "All", en = "All"),
        interpretationTarget = false,
        asset = SessionAssetResponse(),
        sessionCategoryItemId = categoryItemId,
    )
}
