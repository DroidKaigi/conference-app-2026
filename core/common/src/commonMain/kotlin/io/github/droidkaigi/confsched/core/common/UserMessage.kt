package io.github.droidkaigi.confsched.core.common

data class UserMessage(val error: AppError)

fun Throwable.toUserMessage(): UserMessage = UserMessage(toAppError())
