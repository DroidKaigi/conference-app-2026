package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.ProjectId
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionRoom
import kotlinx.collections.immutable.toPersistentList

fun ProjectListResponse.toProjects(): Projects {
    val roomLabelById = rooms.associateBy({ it.id }, { it.name.toMultiLangText() })
    return Projects(
        items = projects.map { it.toProject(roomLabelById) }.toPersistentList(),
    )
}

private fun ProjectResponse.toProject(roomLabelById: Map<Long, MultiLangText>): Project = Project(
    id = ProjectId(id),
    title = title.toMultiLangText(),
    description = i18nDesc.toMultiLangText(),
    room = roomLabelById[roomId]?.let(Room::of) ?: SessionRoom.UNKNOWN,
    message = message?.toMultiLangText(),
    moreDetailsUrl = moreDetailsUrl,
)
