package io.github.droidkaigi.confsched.app

import android.content.Context
import io.github.droidkaigi.confsched.core.model.MultiLangText

internal fun Context.localized(text: MultiLangText): String =
    if (resources.configuration.locales[0].language == "ja") text.ja else text.en
