import XCTest

/// Mirrors `core/model/src/commonTest/.../FavoritesWidgetStateTest.kt` over the snapshot the
/// extension actually reads, so the Swift port and the Kotlin original stay in step.
final class FavoritesWidgetStateTests: XCTestCase {
    private var fixture: FavoritesWidgetSnapshot!

    override func setUpWithError() throws {
        let url = try XCTUnwrap(
            Bundle(for: Self.self).url(forResource: "favorites-widget-snapshot", withExtension: "json")
        )
        fixture = try JSONDecoder().decode(FavoritesWidgetSnapshot.self, from: Data(contentsOf: url))
    }

    // MARK: - Snapshot contract

    func testTheFixtureCarriesTheSchemaVersionTheExtensionExpects() {
        XCTAssertEqual(fixture.schemaVersion, FavoritesWidgetContract.snapshotSchemaVersion)
        XCTAssertEqual(fixture.favorites.count, 8)
    }

    func testTheClockOffsetShiftsTheInstantTheWidgetDraws() {
        let systemNow = at(day: 2, hour: 9)
        let shifted = withClockOffset(3600).now(systemNow: systemNow)
        XCTAssertEqual(shifted, systemNow.addingTimeInterval(3600))
        XCTAssertEqual(fixture.now(systemNow: systemNow), systemNow)
    }

    // MARK: - State

    func testBeforeTheConferenceCountsDaysRegardlessOfFavorites() {
        let now = at(day: 2, hour: 9).addingTimeInterval(-14 * .day)
        assertCountdown(fixture.state(at: now), days: 14)
        assertCountdown(withFavorites([]).state(at: now), days: 14)
    }

    func testTheNightBeforeTheEventDayCountsTwoDays() {
        assertCountdown(fixture.state(at: eventDayStart.addingTimeInterval(-3600)), days: 2)
    }

    func testTheDayBeforeDay1ReadsAsTheEventDay() {
        assertEventDay(fixture.state(at: eventDayStart))
        assertEventDay(fixture.state(at: conferenceStart.addingTimeInterval(-60)))
    }

    func testADayWithoutFavoritesPromptsWithTheOtherDayCount() {
        let state = withFavorites(sessions("d2", "d2b")).state(at: at(day: 2, hour: 9))
        guard case let .empty(day, otherDayFavorites) = state else {
            return XCTFail("expected the empty state")
        }
        XCTAssertEqual(day, 1)
        XCTAssertEqual(otherDayFavorites, 2)
    }

    func testDay2CountsNoOtherDayFavorites() {
        let state = withFavorites(sessions("early")).state(at: at(day: 3, hour: 9))
        guard case let .empty(day, otherDayFavorites) = state else {
            return XCTFail("expected the empty state")
        }
        XCTAssertEqual(day, 2)
        XCTAssertEqual(otherDayFavorites, 0)
    }

    func testAfterTheConferenceThanksTheVisitor() {
        assertPostConference(fixture.state(at: conferenceEnd))
        assertPostConference(fixture.state(at: at(day: 3, hour: 9).addingTimeInterval(.day)))
    }

    func testTheScheduleHoldsOnlyTheFavoritesOfTheCurrentDay() {
        let state = withFavorites(sessions("d2", "late", "early")).state(at: at(day: 2, hour: 9))
        guard case let .schedule(day, slots) = state else { return XCTFail("expected the schedule state") }
        XCTAssertEqual(day, 1)
        XCTAssertEqual(slots.map { $0.sessions.map(\.id) }, [["early"], ["late"]])
        XCTAssertEqual(slots.map(\.isLive), [false, false])
    }

    func testARunningFavoriteMarksItsSlotLive() {
        let state = withFavorites(sessions("early", "next")).state(at: at(day: 2, hour: 10, minute: 10))
        XCTAssertEqual(scheduleSlots(state)?.map(\.isLive), [true, false])
    }

    func testParallelFavoritesShareOneSlot() {
        let state = withFavorites(sessions("early", "parallel")).state(at: at(day: 2, hour: 9))
        XCTAssertEqual(scheduleSlots(state)?.map { $0.sessions.map(\.id) }, [["early", "parallel"]])
    }

    func testFavoritesThatAllEndedReadAsDoneWhileTheDayRunsOn() {
        let state = withFavorites(sessions("early", "d2")).state(at: at(day: 2, hour: 11))
        guard case let .todayDone(day, otherDayFavorites) = state else {
            return XCTFail("expected the today-done state")
        }
        XCTAssertEqual(day, 1)
        XCTAssertEqual(otherDayFavorites, 1)
    }

    func testDay1WrapsUpOnceEveryDay1SessionHasEnded() {
        let state = withFavorites(sessions("early", "d2")).state(at: at(day: 2, hour: 19))
        guard case let .dayWrapUp(tomorrowFavorites) = state else {
            return XCTFail("expected the day wrap-up state")
        }
        XCTAssertEqual(tomorrowFavorites, 1)
    }

    func testDay2ReadsAsPostConferenceOnceItsProgrammeHasEnded() {
        assertPostConference(withFavorites(sessions("d2")).state(at: at(day: 3, hour: 20)))
    }

    func testADayWithNoTimetableItemReadsAsEmptyRatherThanOver() {
        let snapshot = withFavorites([]).withLastSessionEnds(day1: nil, day2: nil)
        guard case let .empty(day, otherDayFavorites) = snapshot.state(at: at(day: 2, hour: 20)) else {
            return XCTFail("expected the empty state")
        }
        XCTAssertEqual(day, 1)
        XCTAssertEqual(otherDayFavorites, 0)
    }

    // MARK: - Rows

    func testRowsFitWithoutACountWhenSessionsDoNotOverflow() {
        let rows = [slot(of: "early"), slot(of: "next")].rows(maxRows: 3)
        XCTAssertEqual(rows.count, 2)
        XCTAssertEqual(rows.map(showsTime), [true, true])
    }

    func testASharedSlotPrintsItsTimeOnce() {
        let rows = [slot(of: "early", "parallel", isLive: true)].rows(maxRows: 3)
        XCTAssertEqual(rows.map(showsTime), [true, false])
        XCTAssertEqual(rows.map(\.isLive), [true, true])
    }

    func testOverflowingSessionsCollapseIntoACountRow() {
        let rows = [slot(of: "early", "parallel"), slot(of: "next"), slot(of: "late")].rows(maxRows: 3)
        XCTAssertEqual(rows.count, 3)
        guard case let .more(count) = rows[2] else { return XCTFail("expected a count row") }
        XCTAssertEqual(count, 2)
    }

    // MARK: - Boundaries

    func testBeforeTheEventDayTheBoundaryIsTheNextConferenceMidnight() {
        let now = at(day: 2, hour: 9).addingTimeInterval(-14 * .day)
        XCTAssertEqual(fixture.nextBoundary(after: now), conferenceStart.addingTimeInterval(-13 * .day))
    }

    func testTheEventDayIsBoundedByDay1() {
        XCTAssertEqual(fixture.nextBoundary(after: eventDayStart.addingTimeInterval(9 * 3600)), conferenceStart)
    }

    func testDuringADayTheBoundaryIsTheNextFavoriteStartOrEnd() {
        let programme = fixture.withLastSessionEnds(day1: epochEnd(of: "next"), day2: nil)
        let ab = programme.with(favorites: sessions("early", "next"))
        let abc = programme.with(favorites: sessions("early", "next", "mid"))
        XCTAssertEqual(ab.nextBoundary(after: at(day: 2, hour: 9)), at(day: 2, hour: 10))
        XCTAssertEqual(abc.nextBoundary(after: at(day: 2, hour: 10, minute: 5)), at(day: 2, hour: 10, minute: 20))
        XCTAssertEqual(abc.nextBoundary(after: at(day: 2, hour: 10, minute: 20)), at(day: 2, hour: 10, minute: 40))
        XCTAssertEqual(ab.nextBoundary(after: at(day: 2, hour: 10, minute: 40)), at(day: 2, hour: 11))
    }

    func testTheOtherDaysFavoritesDoNotBoundToday() {
        let snapshot = withFavorites(sessions("early", "d2"))
        XCTAssertEqual(snapshot.nextBoundary(after: at(day: 2, hour: 9)), at(day: 2, hour: 10))
    }

    func testTheDaysLastSessionEndBoundsADayWhoseFavoritesHaveEnded() {
        let snapshot = withFavorites(sessions("early"))
        XCTAssertEqual(snapshot.nextBoundary(after: at(day: 2, hour: 11)), at(day: 2, hour: 18))
    }

    func testAFinishedDay1IsBoundedByTheMidnightThatStartsDay2() {
        let snapshot = withFavorites(sessions("d2"))
        XCTAssertEqual(snapshot.nextBoundary(after: at(day: 2, hour: 19)), at(day: 3, hour: 0))
    }

    func testDay2FallsBackToTheConferenceEnd() {
        let dayOver = fixture
            .with(favorites: sessions("d2"))
            .withLastSessionEnds(day1: nil, day2: epochEnd(of: "d2"))
        XCTAssertEqual(dayOver.nextBoundary(after: at(day: 3, hour: 11)), conferenceEnd)

        let noProgramme = withFavorites([]).withLastSessionEnds(day1: nil, day2: nil)
        XCTAssertEqual(noProgramme.nextBoundary(after: at(day: 3, hour: 9)), conferenceEnd)
    }

    func testAfterTheConferenceThereIsNoBoundary() {
        XCTAssertNil(fixture.nextBoundary(after: conferenceEnd))
    }

    // MARK: - Helpers

    private var conferenceStart: Date {
        Date(timeIntervalSince1970: TimeInterval(fixture.conference.startEpochSeconds))
    }

    private var conferenceEnd: Date {
        Date(timeIntervalSince1970: TimeInterval(fixture.conference.endEpochSeconds))
    }

    private var eventDayStart: Date {
        Date(timeIntervalSince1970: TimeInterval(fixture.conference.eventDayStartEpochSeconds))
    }

    private func at(day: Int, hour: Int, minute: Int = 0) -> Date {
        var components = DateComponents()
        components.year = 2026
        components.month = 9
        components.day = day
        components.hour = hour
        components.minute = minute
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = fixture.conferenceTimeZone
        return calendar.date(from: components)!
    }

    private func sessions(_ ids: String...) -> [FavoritesWidgetSnapshot.Session] {
        ids.map { id in fixture.favorites.first { $0.id == id }! }
    }

    private func epochEnd(of id: String) -> Int {
        fixture.favorites.first { $0.id == id }!.endEpochSeconds
    }

    private func slot(
        of ids: String...,
        isLive: Bool = false
    ) -> FavoritesWidgetSlot {
        let sessions = ids.map { id in fixture.favorites.first { $0.id == id }! }
        return FavoritesWidgetSlot(
            startsAt: sessions[0].startsAt,
            endsAt: sessions[0].endsAt,
            isLive: isLive,
            sessions: sessions
        )
    }

    private func withFavorites(_ favorites: [FavoritesWidgetSnapshot.Session]) -> FavoritesWidgetSnapshot {
        fixture.with(favorites: favorites)
    }

    private func withClockOffset(_ seconds: Int) -> FavoritesWidgetSnapshot {
        FavoritesWidgetSnapshot(
            schemaVersion: fixture.schemaVersion,
            clockOffsetSeconds: seconds,
            conference: fixture.conference,
            colors: fixture.colors,
            favorites: fixture.favorites
        )
    }

    private func scheduleSlots(_ state: FavoritesWidgetState) -> [FavoritesWidgetSlot]? {
        guard case let .schedule(_, slots) = state else {
            XCTFail("expected the schedule state")
            return nil
        }
        return slots
    }

    private func showsTime(_ row: FavoritesWidgetRow) -> Bool {
        guard case let .session(_, showsTime, _) = row else { return false }
        return showsTime
    }

    private func assertCountdown(_ state: FavoritesWidgetState, days: Int, line: UInt = #line) {
        guard case let .countdown(actual) = state else {
            return XCTFail("expected the countdown state", line: line)
        }
        XCTAssertEqual(actual, days, line: line)
    }

    private func assertEventDay(_ state: FavoritesWidgetState, line: UInt = #line) {
        guard case .eventDay = state else {
            return XCTFail("expected the event-day state", line: line)
        }
    }

    private func assertPostConference(_ state: FavoritesWidgetState, line: UInt = #line) {
        guard case .postConference = state else {
            return XCTFail("expected the post-conference state", line: line)
        }
    }
}

private extension FavoritesWidgetSnapshot {
    func with(favorites: [Session]) -> FavoritesWidgetSnapshot {
        FavoritesWidgetSnapshot(
            schemaVersion: schemaVersion,
            clockOffsetSeconds: clockOffsetSeconds,
            conference: conference,
            colors: colors,
            favorites: favorites
        )
    }

    /// Stands in for a timetable whose days end where the case under test needs them to.
    func withLastSessionEnds(day1: Int?, day2: Int?) -> FavoritesWidgetSnapshot {
        FavoritesWidgetSnapshot(
            schemaVersion: schemaVersion,
            clockOffsetSeconds: clockOffsetSeconds,
            conference: Conference(
                startEpochSeconds: conference.startEpochSeconds,
                endEpochSeconds: conference.endEpochSeconds,
                utcOffsetSeconds: conference.utcOffsetSeconds,
                day1Month: conference.day1Month,
                day1Day: conference.day1Day,
                day2Month: conference.day2Month,
                day2Day: conference.day2Day,
                day1LastSessionEndEpochSeconds: day1,
                day2LastSessionEndEpochSeconds: day2
            ),
            colors: colors,
            favorites: favorites
        )
    }
}

private extension TimeInterval {
    static let day: TimeInterval = 24 * 60 * 60
}
