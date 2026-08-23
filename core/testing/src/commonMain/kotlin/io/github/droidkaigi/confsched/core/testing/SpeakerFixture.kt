package io.github.droidkaigi.confsched.core.testing

import io.github.droidkaigi.confsched.core.model.Speaker
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/**
 * The speakers a test session is given.
 *
 * [iconUrl] is null by default, which is the case a screen has to fall back on; pass one to
 * cover the path where a picture is loaded.
 */
fun fakeSpeakers(vararg names: String, iconUrl: String? = null): PersistentList<Speaker> =
    names.map { Speaker(name = it, iconUrl = iconUrl) }.toPersistentList()
