package io.github.droidkaigi.confsched.core.model

data class ProfileCard(
    val nickName: String,
    val occupation: String,
    val link: String,
    val mascot: Mascot,
    val sketchiness: Sketchiness,
    val avatarImage: AvatarImage?,
) {
    companion object {
        val DefaultMascot = Mascot.C
        val DefaultSketchiness = Sketchiness.Normal
    }
}
