# Session reminders

A favorited session raises a local notification 15 minutes before it starts. Android and iOS schedule it; desktop and web do not, and their graphs carry nothing of it.

## What to notify, and when

`:core:model` answers that as a pure function, next to the favorites widget's own computation:

```kotlin
data class SessionReminder(
    val itemId: TimetableItemId,
    val title: MultiLangText,
    val room: Room,
    val startsAt: Instant,
    val startsAtText: String,
    val notifyAt: Instant,
)

fun computeSessionReminders(
    now: Instant,
    timetable: Timetable,
    favoriteIds: Set<TimetableItemId>,
): List<SessionReminder>
```

The result holds the favorites whose session has not started yet, earliest first. A reminder stays in the result once its `notifyAt` has passed, so a reschedule never drops one that is still due, and a session favorited inside the lead time is notified immediately. It is a list of moments rather than a schedule the platform has to interpret, so the platform layer only converts and hands it over.

## Keeping the platform in step

`SessionReminderSync` (`:app-shared`, `AppScope`) reschedules whenever the favorites change, the `KaigiClock` offset shifts, the server environment changes, or a timetable fetch replaces the persisted payload (`PersistedTimetableReader.updates`). Each round reads the persisted timetable, computes the reminders and passes them to the `SessionReminderScheduler` binding. The first emission reschedules too, so an app start reconciles whatever the previous process left behind. A round without a readable timetable leaves the existing schedule in place, and a failing round is logged and never breaks the collector.

```kotlin
interface SessionReminderScheduler {
    /** Replaces every reminder scheduled before with this set. */
    suspend fun reschedule(reminders: List<SessionReminder>)
}
```

`:app-shared` binds no default. Each platform that wants reminders contributes its own implementation and exposes `sessionReminderSync` on its graph; `AndroidAppGraph` and `IosAppGraph` do, and the desktop and web graphs never reach the sync at all. Android starts it from `KaigiApplication.onCreate`, iOS from `KaigiAppHost.initialize()`, both on a process-lifetime scope.

## Android

`AndroidSessionReminderScheduler` schedules one `AlarmManager` alarm per reminder with `setAndAllowWhileIdle(RTC_WAKEUP, …)`, and stores the scheduled ids in a `DataStore<Preferences>` of its own. A round persists the union of the stored and the new ids first, cancels the alarm and any posted notification of the ids that fell out, arms the rest, then persists the new ids; a crash mid-round therefore leaves no alarm the next round cannot reach. The trigger time is the device clock plus the distance from `KaigiClock.now()` to `notifyAt`, so the debug tooling's shifted clock reaches the alarm too. A reminder already armed in an earlier round is left alone once its `notifyAt` has passed, so a reschedule does not repost a notification the user has seen.

The alarms are inexact on purpose. An exact alarm needs `SCHEDULE_EXACT_ALARM`, which the user must grant in system settings on Android 14 and later and can revoke at any time, or `USE_EXACT_ALARM`, which Google Play accepts only from apps whose core function is timekeeping. A reminder that lands a few minutes off is still a reminder, so the app stays out of that permission entirely.

`SessionReminderReceiver` posts the notification: the session title, "Starts at HH:mm · room" underneath, and the session deep link as its content intent. It skips posting where `POST_NOTIFICATIONS` is not granted. `BootCompletedReceiver` reschedules after a reboot, which clears every alarm the app set.

`MainActivity` asks for `POST_NOTIFICATIONS` when the favorites first become non-empty — the permission has nothing to carry until there is something to be reminded of — and only where the prompt has never been declined.

## iOS

`IosSessionReminderScheduler` keeps one `UNTimeIntervalNotificationTrigger` request per reminder, identified by the session id. A round removes the pending requests and the delivered notifications whose id is no longer wanted, and adds a request for each reminder still ahead. A reminder whose `notifyAt` has passed is added with a one-second interval, unless its identifier is already pending or delivered. iOS keeps at most 64 pending requests per app and drops the rest silently, so the scheduler keeps the 64 earliest.

Authorization (`alert`, `sound`, `badge`) is requested whenever there is something to schedule; the OS shows the prompt once. `SessionReminderNotificationDelegate`, installed from `KaigiAppHost.initialize()`, turns a tap into the session's favorites deep link through `DeepLinkStore` and lets a reminder show as a banner while the app is in the foreground.

Related: [Clock (KaigiClock)](./clock.md) · [AppGraph and UiGraph](./di-app-graph.md) · [Deep links (DeepLinkEffect)](./navigation-deep-links.md)
