import Foundation

/// A Swift port of `computeFavoritesWidgetState` / `nextFavoritesWidgetBoundary`
/// (`core/model/.../FavoritesWidgetState.kt`); the two must stay in step.
enum FavoritesWidgetState {
    /// Before the event: the number of days until Day 1, shown regardless of favorites.
    case countdown(daysUntilStart: Int)
    /// The event day before Day 1, which holds no timetable sessions.
    case eventDay
    /// A conference day with no favorite on it; `otherDayFavorites` counts Day 2's on Day 1.
    case empty(day: Int, otherDayFavorites: Int)
    /// The slots of `day` that have not ended yet, in start order.
    case schedule(day: Int, slots: [FavoritesWidgetSlot])
    /// Every favorite of `day` has ended while the day's programme runs on.
    case todayDone(day: Int, otherDayFavorites: Int)
    /// Day 1's programme is over and Day 2 is still ahead.
    case dayWrapUp(tomorrowFavorites: Int)
    /// After the conference end, or once Day 2's programme is over.
    case postConference
}

/// Favorited sessions sharing one start-end pair on one day.
struct FavoritesWidgetSlot: Identifiable {
    let startsAt: String
    let endsAt: String
    let isLive: Bool
    let sessions: [FavoritesWidgetSnapshot.Session]

    var id: String { "\(sessions.first?.startEpochSeconds ?? 0)-\(sessions.first?.id ?? "")" }
}

/// One row of the medium widget's schedule list.
enum FavoritesWidgetRow: Identifiable {
    /// `showsTime` is false for the second and later sessions of a shared slot.
    case session(FavoritesWidgetSnapshot.Session, showsTime: Bool, isLive: Bool)
    /// Replaces the last row when more sessions remain than the list holds.
    case more(count: Int)

    var id: String {
        switch self {
        case let .session(session, _, _): return session.id
        case let .more(count): return "more-\(count)"
        }
    }

    var isLive: Bool {
        switch self {
        case let .session(_, _, isLive): return isLive
        case .more: return false
        }
    }
}

extension FavoritesWidgetSnapshot {
    /// The instant the widget draws, which the debug clock offset shifts as it does in the app.
    func now(systemNow: Date) -> Date {
        systemNow.addingTimeInterval(TimeInterval(clockOffsetSeconds))
    }

    func state(at now: Date) -> FavoritesWidgetState {
        let seconds = Int(now.timeIntervalSince1970)
        if seconds < conference.eventDayStartEpochSeconds {
            return .countdown(daysUntilStart: daysUntilStart(from: now))
        }
        if seconds < conference.startEpochSeconds { return .eventDay }
        if seconds >= conference.endEpochSeconds { return .postConference }

        let day = conferenceDay(at: seconds)
        let otherDayFavorites = day == 1 ? favorites.filter { $0.day == 2 }.count : 0
        // A day with no timetable item leaves its programme's end unknown, so it is not over.
        if let lastEnd = conference.lastSessionEndEpochSeconds(ofDay: day), lastEnd <= seconds {
            return day == 1 ? .dayWrapUp(tomorrowFavorites: otherDayFavorites) : .postConference
        }

        let favoritesOnDay = favorites.filter { $0.day == day }
        let remaining = favoritesOnDay.filter { $0.endEpochSeconds > seconds }
        if remaining.isEmpty {
            return favoritesOnDay.isEmpty
                ? .empty(day: day, otherDayFavorites: otherDayFavorites)
                : .todayDone(day: day, otherDayFavorites: otherDayFavorites)
        }
        return .schedule(day: day, slots: slots(of: remaining, at: seconds))
    }

    func conferenceDay(at seconds: Int) -> Int {
        seconds < conference.day2StartEpochSeconds ? 1 : 2
    }

    /// The earliest instant after `now` at which the state changes on its own, or nil when only a
    /// new snapshot can change it.
    func nextBoundary(after now: Date) -> Date? {
        let seconds = Int(now.timeIntervalSince1970)
        if seconds >= conference.endEpochSeconds { return nil }
        if seconds < conference.eventDayStartEpochSeconds {
            var calendar = Calendar(identifier: .gregorian)
            calendar.timeZone = conferenceTimeZone
            return calendar.startOfDay(for: calendar.date(byAdding: .day, value: 1, to: now) ?? now)
        }
        if seconds < conference.startEpochSeconds {
            return Date(timeIntervalSince1970: TimeInterval(conference.startEpochSeconds))
        }

        let day = conferenceDay(at: seconds)
        var boundaries = favorites.filter { $0.day == day }
            .flatMap { [$0.startEpochSeconds, $0.endEpochSeconds] }
        if let lastEnd = conference.lastSessionEndEpochSeconds(ofDay: day) { boundaries.append(lastEnd) }
        boundaries.append(conference.midnightRolloverEpochSeconds(ofDay: day))
        boundaries.append(conference.endEpochSeconds)
        guard let next = boundaries.filter({ $0 > seconds }).min() else { return nil }
        return Date(timeIntervalSince1970: TimeInterval(next))
    }

    private func daysUntilStart(from now: Date) -> Int {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = conferenceTimeZone
        let today = calendar.startOfDay(for: now)
        let day1 = calendar.startOfDay(
            for: Date(timeIntervalSince1970: TimeInterval(conference.startEpochSeconds))
        )
        return calendar.dateComponents([.day], from: today, to: day1).day ?? 0
    }

    private func slots(of sessions: [Session], at seconds: Int) -> [FavoritesWidgetSlot] {
        var order: [String] = []
        var grouped: [String: [Session]] = [:]
        for session in sessions.sorted(by: { $0.startEpochSeconds < $1.startEpochSeconds }) {
            let key = "\(session.startEpochSeconds)-\(session.endEpochSeconds)"
            if grouped[key] == nil { order.append(key) }
            grouped[key, default: []].append(session)
        }
        return order.compactMap { key in
            guard let sessions = grouped[key], let first = sessions.first else { return nil }
            return FavoritesWidgetSlot(
                startsAt: first.startsAt,
                endsAt: first.endsAt,
                isLive: first.startEpochSeconds <= seconds,
                sessions: sessions
            )
        }
    }
}

extension Array where Element == FavoritesWidgetSlot {
    /// Flattens slots into at most `maxRows` rows; the last row becomes the remaining count when
    /// the sessions overflow.
    func rows(maxRows: Int) -> [FavoritesWidgetRow] {
        let sessionRows = flatMap { slot in
            slot.sessions.enumerated().map { index, session in
                FavoritesWidgetRow.session(session, showsTime: index == 0, isLive: slot.isLive)
            }
        }
        if sessionRows.count <= maxRows { return sessionRows }
        let kept = [FavoritesWidgetRow](sessionRows.prefix(maxRows - 1))
        return kept + [.more(count: sessionRows.count - kept.count)]
    }
}
