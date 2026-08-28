import Foundation

/// The widget's copy, keyed as the Android string resources key it mirrors.
enum FavoritesWidgetStrings {
    static var description: String { localized("widget_description") }
    static var brand: String { localized("widget_brand") }
    static var brandFull: String { localized("widget_brand_full") }
    static var countdownPrefix: String { localized("widget_countdown_prefix") }
    static var countdownUnit: String { localized("widget_countdown_unit") }
    static var countdownNote: String { localized("widget_countdown_note") }
    static var nextLabel: String { localized("widget_next_label") }
    static var liveSmallLabel: String { localized("widget_live_small_label") }
    static var scheduleLabel: String { localized("widget_schedule_label") }
    static var favoritesLabel: String { localized("widget_favorites_label") }
    static var liveBadge: String { localized("widget_live_badge") }
    static var emptyMessage: String { localized("widget_empty_message") }
    static var doneMessage: String { localized("widget_done_message") }
    static var doneHint: String { localized("widget_done_hint") }
    static var eventDayMessage: String { localized("widget_event_day_message") }
    static var eventDayNote: String { localized("widget_event_day_note") }
    static var wrapUpMessage: String { localized("widget_wrap_up_message") }
    static var wrapUpAdd: String { localized("widget_wrap_up_add") }
    static var postMessage: String { localized("widget_post_message") }

    static func emptyHint(dayLabel: String) -> String {
        String(format: localized("widget_empty_hint"), dayLabel)
    }

    static func tomorrowFavorites(_ count: Int) -> String {
        String.localizedStringWithFormat(localized("widget_tomorrow_favorites"), count)
    }

    static func moreCountSmall(_ count: Int) -> String {
        String(format: localized("widget_same_slot_more_small"), count)
    }

    static func moreCountRow(_ count: Int) -> String {
        String(format: localized("widget_same_slot_more_row"), count)
    }

    static func countdownDates(_ conference: FavoritesWidgetSnapshot.Conference) -> String {
        String(
            format: localized("widget_countdown_dates"),
            conference.day1Month,
            conference.day1Day,
            conference.day2Month,
            conference.day2Day
        )
    }

    private static func localized(_ key: String) -> String {
        NSLocalizedString(key, bundle: .widgetResources, comment: "")
    }
}

private final class BundleToken {}

private extension Bundle {
    /// The bundle these sources are compiled into, which is the extension when it runs and the
    /// test bundle under `xcodebuild test`.
    static let widgetResources = Bundle(for: BundleToken.self)
}
