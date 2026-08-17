package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlin.jvm.JvmInline

@JvmInline
value class ProjectId(val value: String)

data class Project(
    val id: ProjectId,
    val title: MultiLangText,
    val i18nDesc: MultiLangText,
    val room: Room,
    val message: MultiLangText? = null,
    val moreDetailsUrl: String? = null,
)

data class Projects(
    val items: PersistentList<Project>,
) {
    companion object
}
