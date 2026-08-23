package io.github.droidkaigi.confsched.core.common

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

interface KaigiLogger {
    fun debug(message: () -> String)

    fun info(message: () -> String)

    fun warn(message: () -> String)

    fun error(throwable: Throwable?, message: () -> String)
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class KermitKaigiLogger(
    minLogSeverity: MinLogSeverity,
    private val crashReporter: CrashReporter,
) : KaigiLogger {

    private val logger = Logger(
        config = StaticConfig(
            minSeverity = when (minLogSeverity) {
                MinLogSeverity.Verbose -> Severity.Verbose
                MinLogSeverity.ErrorOnly -> Severity.Error
            },
            logWriterList = listOf(platformLogWriter()),
        ),
        tag = "Confsched",
    )

    override fun debug(message: () -> String) = logger.d(message = message)

    override fun info(message: () -> String) = logger.i(message = message)

    override fun warn(message: () -> String) = logger.w(message = message)

    override fun error(throwable: Throwable?, message: () -> String) {
        logger.e(throwable = throwable, message = message)
        crashReporter.report(throwable, message())
    }
}
