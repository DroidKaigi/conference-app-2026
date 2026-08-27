import WidgetKit

struct FavoritesWidgetEntry: TimelineEntry {
    let date: Date
    let snapshot: FavoritesWidgetSnapshot
    let state: FavoritesWidgetState
}

/// WidgetKit refuses to hold an unbounded timeline, so the entries stop at this many boundaries
/// and the reload policy picks the schedule up from the last one.
private let maxEntries = 40

struct FavoritesWidgetProvider: TimelineProvider {
    func placeholder(in context: Context) -> FavoritesWidgetEntry {
        entry(from: .placeholder, at: Date())
    }

    func getSnapshot(in context: Context, completion: @escaping (FavoritesWidgetEntry) -> Void) {
        let snapshot = FavoritesWidgetSnapshot.load()
        let systemNow = Date()
        completion(entry(from: snapshot, at: snapshot.now(systemNow: systemNow), systemDate: systemNow))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<FavoritesWidgetEntry>) -> Void) {
        let snapshot = FavoritesWidgetSnapshot.load()
        let systemNow = Date()
        var moment = snapshot.now(systemNow: systemNow)
        // The snapshot's own clock runs ahead of the system clock by the debug offset, so an entry
        // has to be dated in system time for WidgetKit to schedule it.
        let offset = moment.timeIntervalSince(systemNow)
        var entries = [entry(from: snapshot, at: moment, systemDate: systemNow)]
        while entries.count < maxEntries, let boundary = snapshot.nextBoundary(after: moment) {
            moment = boundary
            entries.append(entry(from: snapshot, at: moment, systemDate: moment - offset))
        }
        let policy: TimelineReloadPolicy = entries.count < maxEntries
            ? .never
            : .after(entries[entries.count - 1].date)
        completion(Timeline(entries: entries, policy: policy))
    }

    private func entry(
        from snapshot: FavoritesWidgetSnapshot,
        at moment: Date,
        systemDate: Date? = nil
    ) -> FavoritesWidgetEntry {
        FavoritesWidgetEntry(
            date: systemDate ?? moment,
            snapshot: snapshot,
            state: snapshot.state(at: moment)
        )
    }
}
