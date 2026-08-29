package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.NamedRoom
import io.github.droidkaigi.confsched.core.model.SessionRoom
import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectResponseMapperTest {

    @Test
    fun a_project_in_a_session_room_takes_that_room() {
        val projects = projectListResponse(
            rooms = listOf(roomResponse(1L, ja = "Otter", en = "Otter")),
            projects = listOf(projectResponse("p1", roomId = 1L)),
        ).toProjects()

        assertEquals(SessionRoom.OTTER, projects.items.single().room)
    }

    @Test
    fun a_project_in_any_other_room_keeps_the_name_the_payload_gives_it() {
        val projects = projectListResponse(
            rooms = listOf(roomResponse(2L, ja = "ホワイエ", en = "Foyer")),
            projects = listOf(projectResponse("p1", roomId = 2L)),
        ).toProjects()

        assertEquals(NamedRoom(MultiLangText(ja = "ホワイエ", en = "Foyer")), projects.items.single().room)
    }

    @Test
    fun a_project_whose_room_the_payload_does_not_list_is_placed_nowhere() {
        val projects = projectListResponse(
            rooms = emptyList(),
            projects = listOf(projectResponse("p1", roomId = 3L)),
        ).toProjects()

        assertEquals(SessionRoom.UNKNOWN, projects.items.single().room)
    }

    private fun projectListResponse(rooms: List<RoomResponse>, projects: List<ProjectResponse>) = ProjectListResponse(
        status = HttpStatusResponse.OK,
        projects = projects,
        rooms = rooms,
    )

    private fun roomResponse(id: Long, ja: String, en: String) = RoomResponse(
        id = id,
        name = LocaledResponse(ja = ja, en = en),
        sort = 0,
    )

    private fun projectResponse(id: String, roomId: Long) = ProjectResponse(
        id = id,
        title = LocaledResponse(ja = "企画", en = "Project"),
        i18nDesc = LocaledResponse(ja = "説明", en = "Description"),
        roomId = roomId,
        startsAt = "2026-09-02T12:00:00+09:00",
        endsAt = "2026-09-02T13:00:00+09:00",
        noShow = false,
        message = null,
        moreDetailsUrl = null,
    )
}
