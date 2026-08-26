package io.github.droidkaigi.confsched.core.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class TimetableSpeakerId(val value: String)

data class TimetableSpeaker(
    val id: TimetableSpeakerId,
    val name: String,
    val tagLine: String,
    val iconUrl: String?,
)
