package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.ProjectId
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.model.Room
import kotlinx.collections.immutable.toPersistentList

fun ProjectListResponse.toProjects(): Projects {
    val roomNameById = rooms.associateBy({ it.id }, { it.name.toMultiLangText().en })
    return Projects(
        items = projects.mapNotNull { it.toProject(roomNameById) }.toPersistentList(),
    )
}

// A room this app does not know cannot be placed on the event map; drop the project.
private fun ProjectResponse.toProject(roomNameById: Map<Long, String>): Project? {
    val room = Room.of(roomNameById[roomId].orEmpty()) ?: return null
    return Project(
        id = ProjectId(id),
        title = title.toMultiLangText(),
        description = i18nDesc.toMultiLangText(),
        room = room,
        message = message?.toMultiLangText(),
        moreDetailsUrl = moreDetailsUrl,
    )
}
