package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.EventKit.EKEvent
import platform.EventKit.EKEventStore
import platform.EventKitUI.EKEventEditViewAction
import platform.EventKitUI.EKEventEditViewController
import platform.EventKitUI.EKEventEditViewDelegateProtocol
import platform.Foundation.NSDate
import platform.Foundation.NSURL
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.darwin.NSObject

@Composable
actual fun rememberCalendarEventAdder(): (CalendarEvent) -> Unit {
    // The edit controller only holds its delegate weakly, so it has to outlive the call here.
    val delegate = remember { DismissingEventEditDelegate() }
    return remember(delegate) {
        { event ->
            val store = EKEventStore()
            val controller = EKEventEditViewController().apply {
                eventStore = store
                this.event = EKEvent.eventWithEventStore(store).apply {
                    title = event.title
                    startDate = event.startsAt.toNSDate()
                    endDate = event.endsAt.toNSDate()
                    location = event.location
                    notes = event.url
                    URL = NSURL.URLWithString(event.url)
                }
                editViewDelegate = delegate
            }
            keyRootViewController()?.presentViewController(controller, animated = true, completion = null)
        }
    }
}

private class DismissingEventEditDelegate :
    NSObject(),
    EKEventEditViewDelegateProtocol {
    override fun eventEditViewController(controller: EKEventEditViewController, didCompleteWithAction: EKEventEditViewAction) {
        controller.dismissViewControllerAnimated(true, completion = null)
    }
}

private fun kotlin.time.Instant.toNSDate(): NSDate =
    NSDate.dateWithTimeIntervalSince1970(epochSeconds.toDouble())
