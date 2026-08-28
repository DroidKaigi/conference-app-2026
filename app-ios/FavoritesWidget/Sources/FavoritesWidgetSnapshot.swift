import Foundation
import SwiftUI

/// The shape `FavoritesWidgetSnapshot.kt` writes.
struct FavoritesWidgetSnapshot: Decodable {
    let schemaVersion: Int
    let clockOffsetSeconds: Int
    let conference: Conference
    let colors: Colors
    let favorites: [Session]

    struct Conference: Decodable {
        let startEpochSeconds: Int
        let endEpochSeconds: Int
        let utcOffsetSeconds: Int
        let day1Month: Int
        let day1Day: Int
        let day2Month: Int
        let day2Day: Int
        /// nil when the day carries no timetable item, which leaves its programme's end unknown.
        let day1LastSessionEndEpochSeconds: Int?
        let day2LastSessionEndEpochSeconds: Int?
    }

    struct Colors: Decodable {
        let surface: String
        let onSurface: String
        let onSurfaceVariant: String
        let primary: String
        let onPrimary: String
        let isDark: Bool
    }

    struct Session: Decodable, Identifiable {
        let id: String
        let day: Int
        let titleEn: String
        let titleJa: String
        let startsAt: String
        let endsAt: String
        let startEpochSeconds: Int
        let endEpochSeconds: Int
        let roomLabel: String
        let roomContainer: String
        let roomOnContainer: String
    }
}

extension FavoritesWidgetSnapshot {
    /// The snapshot the app last wrote, or a placeholder one when the app has not run yet.
    static func load() -> FavoritesWidgetSnapshot {
        guard let container = FileManager.default
            .containerURL(forSecurityApplicationGroupIdentifier: FavoritesWidgetContract.appGroup),
            let data = try? Data(
                contentsOf: container.appendingPathComponent(FavoritesWidgetContract.snapshotFileName)
            ),
            let snapshot = try? JSONDecoder().decode(FavoritesWidgetSnapshot.self, from: data),
            snapshot.schemaVersion == FavoritesWidgetContract.snapshotSchemaVersion
        else {
            return .placeholder
        }
        return snapshot
    }

    /// Stands in before the app has written a snapshot, and in the widget gallery.
    static let placeholder = FavoritesWidgetSnapshot(
        schemaVersion: FavoritesWidgetContract.snapshotSchemaVersion,
        clockOffsetSeconds: 0,
        conference: Conference(
            // 2026-09-02 00:00 and 2026-09-04 00:00 at UTC+9.
            startEpochSeconds: 1_788_274_800,
            endEpochSeconds: 1_788_447_600,
            utcOffsetSeconds: 32400,
            day1Month: 9,
            day1Day: 2,
            day2Month: 9,
            day2Day: 3,
            day1LastSessionEndEpochSeconds: nil,
            day2LastSessionEndEpochSeconds: nil
        ),
        colors: Colors(
            surface: "#FFF7F4EF",
            onSurface: "#FF1F1B16",
            onSurfaceVariant: "#FF4F463A",
            primary: "#FFB4531C",
            onPrimary: "#FFFFFFFF",
            isDark: false
        ),
        favorites: []
    )

    var conferenceTimeZone: TimeZone {
        TimeZone(secondsFromGMT: conference.utcOffsetSeconds) ?? TimeZone(identifier: "Asia/Tokyo")!
    }
}

extension FavoritesWidgetSnapshot.Conference {
    /// The day before Day 1, which holds no timetable session.
    var eventDayStartEpochSeconds: Int { startEpochSeconds - secondsPerDay }

    var day2StartEpochSeconds: Int { endEpochSeconds - secondsPerDay }

    /// The midnight that ends `day`.
    func midnightRolloverEpochSeconds(ofDay day: Int) -> Int {
        (day == 1 ? startEpochSeconds : day2StartEpochSeconds) + secondsPerDay
    }

    func lastSessionEndEpochSeconds(ofDay day: Int) -> Int? {
        day == 1 ? day1LastSessionEndEpochSeconds : day2LastSessionEndEpochSeconds
    }

    /// The `9/2`-style label `DroidKaigi2026Day.label` derives on the Kotlin side.
    func label(ofDay day: Int) -> String {
        day == 1 ? "\(day1Month)/\(day1Day)" : "\(day2Month)/\(day2Day)"
    }
}

private let secondsPerDay = 24 * 60 * 60

extension FavoritesWidgetSnapshot.Session {
    var title: String { isJapanese ? titleJa : titleEn }
}

/// The widget renders in the extension's own process, which follows the system language.
let isJapanese = Locale.preferredLanguages.first?.hasPrefix("ja") == true

extension Color {
    /// Parses the `#AARRGGBB` form the snapshot carries.
    init(argbHex: String) {
        let digits = argbHex.hasPrefix("#") ? String(argbHex.dropFirst()) : argbHex
        let value = UInt64(digits, radix: 16) ?? 0xFF00_0000
        self.init(
            .sRGB,
            red: Double((value >> 16) & 0xFF) / 255,
            green: Double((value >> 8) & 0xFF) / 255,
            blue: Double(value & 0xFF) / 255,
            opacity: Double((value >> 24) & 0xFF) / 255
        )
    }
}
