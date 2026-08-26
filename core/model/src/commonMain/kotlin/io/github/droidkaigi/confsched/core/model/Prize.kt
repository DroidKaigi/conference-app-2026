package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentList
import kotlin.jvm.JvmInline

@JvmInline
value class PrizeId(val value: String)

enum class PrizeGroup {
    A,
    B,
    C,
}

data class Prize(
    val id: PrizeId,
    val name: MultiLangText,
    val group: PrizeGroup,
    val imageUrl: String,
)

data class Prizes(
    val items: PersistentList<Prize>,
) {
    companion object
}
