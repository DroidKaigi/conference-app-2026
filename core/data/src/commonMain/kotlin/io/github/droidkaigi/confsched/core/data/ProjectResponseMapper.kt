package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Project
import io.github.droidkaigi.confsched.core.model.ProjectId
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.model.Room
import kotlinx.collections.immutable.toPersistentList

fun ProjectListResponse.toProjects(): Projects {
    val roomNameById = rooms.associateBy({ it.id }, { it.name.toMultiLangText().en })
    return Projects(
        items = projects.map { it.toProject(roomNameById) }.toPersistentList(),
    )
}

private fun ProjectResponse.toProject(roomNameById: Map<Long, String>): Project = Project(
    id = ProjectId(id),
    title = title.toMultiLangText(),
    i18nDesc = i18nDesc.toMultiLangText(),
    room = Room.of(roomNameById[roomId].orEmpty()),
    message = message?.toMultiLangText(),
    moreDetailsUrl = moreDetailsUrl,
)
