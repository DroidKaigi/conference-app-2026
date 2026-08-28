package io.github.droidkaigi.confsched.feature.search.component

import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.language_english
import io.github.droidkaigi.confsched.feature.search.generated.resources.language_japanese
import io.github.droidkaigi.confsched.feature.search.generated.resources.language_mixed
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_after_party
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_break
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_codelabs
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_fireside_chat
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_lunch
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_normal
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_recap
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_reserved
import io.github.droidkaigi.confsched.feature.search.generated.resources.session_type_welcome_talk
import org.jetbrains.compose.resources.StringResource

internal fun SessionType.labelResource(): StringResource = when (this) {
    SessionType.NORMAL -> Res.string.session_type_normal
    SessionType.WELCOME_TALK -> Res.string.session_type_welcome_talk
    SessionType.RESERVED -> Res.string.session_type_reserved
    SessionType.CODELABS -> Res.string.session_type_codelabs
    SessionType.FIRESIDE_CHAT -> Res.string.session_type_fireside_chat
    SessionType.LUNCH -> Res.string.session_type_lunch
    SessionType.BREAK -> Res.string.session_type_break
    SessionType.AFTER_PARTY -> Res.string.session_type_after_party
    SessionType.RECAP -> Res.string.session_type_recap
}

internal fun Language.labelResource(): StringResource = when (this) {
    Language.JAPANESE -> Res.string.language_japanese
    Language.ENGLISH -> Res.string.language_english
    Language.MIXED -> Res.string.language_mixed
}
