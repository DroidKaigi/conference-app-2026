package io.github.droidkaigi.confsched.core.model

data class Project(
    val title: MultiLangText,
    val description: MultiLangText,
    val room: Room,
    val note: MultiLangText? = null,
    val detailPage: String? = null,
)
