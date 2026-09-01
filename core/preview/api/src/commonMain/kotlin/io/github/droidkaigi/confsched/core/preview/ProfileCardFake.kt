package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.ProfileCard

fun ProfileCard.Companion.fake(): ProfileCard = ProfileCard(
    nickName = "Speaker A",
    occupation = "Software Engineer",
    link = "https://example.com/speaker-a",
    mascot = ProfileCard.DefaultMascot,
    sketchiness = ProfileCard.DefaultSketchiness,
    avatarImage = null,
)
