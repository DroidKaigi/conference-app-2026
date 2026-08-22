package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Room
import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectResponseMapperTest {

    @Test
    fun a_project_takes_the_room_the_payload_names() {
        val projects = projectListResponse(
            rooms = listOf(roomResponse(81666L, "Meerkat")),
            projects = listOf(projectResponse("p1", roomId = 81666L)),
        ).toProjects()

        assertEquals(Room.MEERKAT, projects.items.single().room)
    }

    @Test
    fun a_project_in_a_room_this_app_does_not_know_is_dropped() {
        val projects = projectListResponse(
            rooms = listOf(roomResponse(99999L, "Somewhere Else"), roomResponse(81666L, "Meerkat")),
            projects = listOf(
                projectResponse("p1", roomId = 99999L),
                projectResponse("p2", roomId = 81666L),
            ),
        ).toProjects()

        assertEquals(listOf("p2"), projects.items.map { it.id.value })
    }

    private fun projectListResponse(
        rooms: List<RoomResponse>,
        projects: List<ProjectResponse>,
    ) = ProjectListResponse(
        status = HttpStatusResponse.OK,
        projects = projects,
        rooms = rooms,
    )

    private fun roomResponse(id: Long, name: String) = RoomResponse(
        name = LocaledResponse(ja = name, en = name),
        id = id,
        sort = 0,
    )

    private fun projectResponse(id: String, roomId: Long) = ProjectResponse(
        id = id,
        title = LocaledResponse(ja = "Project $id", en = "Project $id"),
        i18nDesc = LocaledResponse(ja = "説明", en = "Description"),
        roomId = roomId,
        startsAt = "2026-09-02T10:00:00+09:00",
        endsAt = "2026-09-02T18:00:00+09:00",
        noShow = false,
    )
}
