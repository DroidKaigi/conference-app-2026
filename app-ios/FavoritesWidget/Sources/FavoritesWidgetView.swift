import SwiftUI
import WidgetKit

// Spacing follows the widget spec's five-step scale on a 4pt base, as the Android widget does.
private let insetBleed: CGFloat = 8
private let insetFrame: CGFloat = 12
private let insetRow: CGFloat = 8
private let gapTight: CGFloat = 4
private let gapBase: CGFloat = 8
private let gapWide: CGFloat = 16
private let gapArt: CGFloat = 20
private let rowHeight: CGFloat = 22
private let timeCellWidth: CGFloat = 40
private let maxMediumRows = 3

private let mascotCSize = CGSize(width: 28, height: 30)
private let mascotBSize = CGSize(width: 37, height: 34)
private func mascotDSize(medium: Bool) -> CGSize {
    medium ? CGSize(width: 37, height: 34) : CGSize(width: 33, height: 30)
}

private func mascotClearance(medium: Bool) -> CGFloat {
    medium ? mascotDSize(medium: true).width + gapArt : 0
}

struct FavoritesWidgetView: View {
    let entry: FavoritesWidgetEntry
    @Environment(\.widgetFamily) private var family

    private var medium: Bool { family != .systemSmall }
    private var colors: FavoritesWidgetSnapshot.Colors { entry.snapshot.colors }

    var body: some View {
        ZStack {
            SketchFrame(medium: medium)
                .stroke(
                    Color(argbHex: colors.primary),
                    style: StrokeStyle(
                        lineWidth: SketchFrame.borderThickness,
                        lineCap: .round,
                        lineJoin: .round
                    )
                )
                .padding(insetBleed)
            content
                .padding(insetBleed + insetFrame)
        }
        .widgetURL(backgroundURL)
        .containerBackground(Color(argbHex: colors.surface), for: .widget)
    }

    @ViewBuilder
    private var content: some View {
        switch entry.state {
        case let .countdown(days):
            CountdownContent(days: days, snapshot: entry.snapshot, medium: medium)
        case .eventDay:
            EventDayContent(colors: colors, medium: medium)
        case let .empty(day, otherDayFavorites):
            DayPromptContent(
                message: FavoritesWidgetStrings.emptyMessage,
                hint: FavoritesWidgetStrings.emptyHint(dayLabel: entry.snapshot.conference.label(ofDay: day)),
                otherDayFavorites: otherDayFavorites,
                colors: colors,
                medium: medium
            )
        case let .schedule(_, slots):
            ScheduleContent(slots: slots, colors: colors, medium: medium)
        case let .todayDone(_, otherDayFavorites):
            DayPromptContent(
                message: FavoritesWidgetStrings.doneMessage,
                hint: FavoritesWidgetStrings.doneHint,
                otherDayFavorites: otherDayFavorites,
                colors: colors,
                medium: medium
            )
        case let .dayWrapUp(tomorrowFavorites):
            FarewellContent(
                message: FavoritesWidgetStrings.wrapUpMessage,
                secondary: tomorrowFavorites > 0
                    ? FavoritesWidgetStrings.tomorrowFavorites(tomorrowFavorites)
                    : FavoritesWidgetStrings.wrapUpAdd,
                colors: colors,
                medium: medium
            )
        case .postConference:
            FarewellContent(
                message: FavoritesWidgetStrings.postMessage,
                secondary: nil,
                colors: colors,
                medium: medium
            )
        }
    }

    private var backgroundURL: URL? {
        switch entry.state {
        case let .schedule(_, slots):
            // A shared slot leaves the session choice open, so only a lone live session deep-links.
            if !medium, let slot = slots.first, slot.isLive, slot.sessions.count == 1 {
                return DeepLink.favoriteSession(slot.sessions[0].id)
            }
            return DeepLink.favorites
        case let .empty(day, _), let .todayDone(day, _):
            return DeepLink.timetableDay(day)
        case .dayWrapUp:
            return DeepLink.timetableDay(2)
        case .postConference:
            return DeepLink.about
        case .countdown, .eventDay:
            return nil
        }
    }
}

enum DeepLink {
    static let favorites = URL(string: "droidkaigi2026://favorites")
    static let about = URL(string: "droidkaigi2026://about")

    static func favoriteSession(_ id: String) -> URL? {
        URL(string: "droidkaigi2026://favorites/session/\(id)")
    }

    static func timetableDay(_ day: Int) -> URL? {
        URL(string: "droidkaigi2026://timetable/day\(day)")
    }
}

// MARK: - Shared parts

private struct HeaderRow: View {
    let label: String
    let live: Bool
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        HStack(spacing: gapBase) {
            SymbolMark(size: 19, color: Color(argbHex: colors.primary))
            // The label yields the row's slack rather than taking a spacer's own spacing, which
            // the small widget has no room for beside the pill.
            Text(label)
                .font(.monoBold)
                .lineLimit(1)
                .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                .frame(maxWidth: .infinity, alignment: .leading)
            if live { LivePill(colors: colors) }
        }
        .padding(.horizontal, insetRow)
    }
}

private struct BrandRow: View {
    let medium: Bool
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        HStack(spacing: gapBase) {
            SymbolMark(size: 19, color: Color(argbHex: colors.primary))
            Text(medium ? FavoritesWidgetStrings.brandFull : FavoritesWidgetStrings.brand)
                .font(.monoBold)
                .foregroundStyle(Color(argbHex: colors.onSurface))
            Spacer(minLength: 0)
        }
    }
}

private struct LivePill: View {
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        Text(FavoritesWidgetStrings.liveBadge)
            .font(.monoBold)
            .lineLimit(1)
            .fixedSize()
            .foregroundStyle(Color(argbHex: colors.onPrimary))
            .padding(.horizontal, gapBase)
            .padding(.vertical, 1)
            .background(Color(argbHex: colors.primary), in: Capsule())
    }
}

private struct RoomChip: View {
    let session: FavoritesWidgetSnapshot.Session

    var body: some View {
        Text(session.roomLabel)
            .font(.monoBold)
            .lineLimit(1)
            .foregroundStyle(Color(argbHex: session.roomOnContainer))
            .padding(.horizontal, gapBase)
            .padding(.vertical, 3)
            .background(Color(argbHex: session.roomContainer), in: Capsule())
    }
}

// MARK: - Countdown

private struct CountdownFrame<Figures: View>: View {
    let colors: FavoritesWidgetSnapshot.Colors
    let medium: Bool
    @ViewBuilder let figures: () -> Figures

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            BrandRow(medium: medium, colors: colors)
            Spacer(minLength: 0)
            HStack(spacing: gapArt) {
                figures()
                if medium {
                    Spacer(minLength: 0)
                    Mascot(
                        artwork: WidgetArtworks.mascotC,
                        size: mascotCSize,
                        color: Color(argbHex: colors.onSurfaceVariant)
                    )
                }
            }
            Spacer(minLength: 0)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct CountdownContent: View {
    let days: Int
    let snapshot: FavoritesWidgetSnapshot
    let medium: Bool

    private var colors: FavoritesWidgetSnapshot.Colors { snapshot.colors }

    var body: some View {
        CountdownFrame(colors: colors, medium: medium) {
            VStack(alignment: .leading, spacing: gapTight) {
                HStack(alignment: .firstTextBaseline, spacing: gapTight) {
                    Text(FavoritesWidgetStrings.countdownPrefix).font(.sans)
                    Text("\(days)")
                        .font(.system(size: 36, design: .monospaced))
                        .foregroundStyle(Color(argbHex: colors.primary))
                    Text(FavoritesWidgetStrings.countdownUnit).font(.sans)
                }
                .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                Text(FavoritesWidgetStrings.countdownDates(snapshot.conference))
                    .font(.monoBold)
                    .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                if medium {
                    Text(FavoritesWidgetStrings.countdownNote)
                        .font(.sans)
                        .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                        .padding(.top, gapTight)
                }
            }
        }
    }
}

// MARK: - Event day

private struct EventDayContent: View {
    let colors: FavoritesWidgetSnapshot.Colors
    let medium: Bool

    var body: some View {
        CountdownFrame(colors: colors, medium: medium) {
            VStack(alignment: .leading, spacing: gapTight) {
                Text(FavoritesWidgetStrings.eventDayMessage)
                    .font(.system(size: 14, weight: .bold, design: .monospaced))
                    .lineLimit(2)
                    .foregroundStyle(Color(argbHex: colors.primary))
                Text(FavoritesWidgetStrings.eventDayNote)
                    .font(.sans)
                    .lineLimit(3)
                    .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
            }
        }
    }
}

// MARK: - Empty and today done

private struct DayPromptContent: View {
    let message: String
    let hint: String
    let otherDayFavorites: Int
    let colors: FavoritesWidgetSnapshot.Colors
    let medium: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: gapBase) {
            HeaderRow(
                label: medium ? FavoritesWidgetStrings.scheduleLabel : FavoritesWidgetStrings.favoritesLabel,
                live: false,
                colors: colors
            )
            ZStack(alignment: .bottomTrailing) {
                VStack(alignment: .leading, spacing: gapBase) {
                    Text(message)
                        .font(.sans)
                        .lineLimit(4)
                        .foregroundStyle(Color(argbHex: colors.onSurface))
                    if medium {
                        VStack(alignment: .leading, spacing: gapTight) {
                            Text(hint)
                                .font(.sans)
                                .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                            if otherDayFavorites > 0 {
                                Text(FavoritesWidgetStrings.tomorrowFavorites(otherDayFavorites))
                                    .font(.sans)
                                    .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                            }
                        }
                    }
                    Spacer(minLength: 0)
                }
                .padding(.trailing, mascotClearance(medium: medium))
                .frame(maxWidth: .infinity, alignment: .leading)
                Mascot(
                    artwork: WidgetArtworks.mascotD,
                    size: mascotDSize(medium: medium),
                    color: Color(argbHex: colors.onSurfaceVariant)
                )
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

// MARK: - Post-conference and day wrap-up

private struct FarewellContent: View {
    let message: String
    let secondary: String?
    let colors: FavoritesWidgetSnapshot.Colors
    let medium: Bool

    var body: some View {
        if medium {
            VStack(alignment: .leading, spacing: 0) {
                BrandRow(medium: true, colors: colors)
                Spacer(minLength: 0)
                HStack(spacing: gapArt) {
                    VStack(alignment: .leading, spacing: gapTight) {
                        Text(message)
                            .font(.sans)
                            .lineLimit(3)
                            .foregroundStyle(Color(argbHex: colors.onSurface))
                        if let secondary {
                            Text(secondary)
                                .font(.sans)
                                .lineLimit(1)
                                .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    Mascot(
                        artwork: WidgetArtworks.mascotB,
                        size: mascotBSize,
                        color: Color(argbHex: colors.onSurfaceVariant)
                    )
                }
                Spacer(minLength: 0)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        } else {
            VStack(spacing: gapBase) {
                SymbolMark(size: 44, color: Color(argbHex: colors.primary))
                Text(message)
                    .font(.sans)
                    .multilineTextAlignment(.center)
                    .lineLimit(3)
                    .foregroundStyle(Color(argbHex: colors.onSurface))
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

// MARK: - Schedule

private struct ScheduleContent: View {
    let slots: [FavoritesWidgetSlot]
    let colors: FavoritesWidgetSnapshot.Colors
    let medium: Bool

    var body: some View {
        if medium {
            VStack(alignment: .leading, spacing: gapBase) {
                HeaderRow(
                    label: FavoritesWidgetStrings.scheduleLabel,
                    live: slots.contains(where: \.isLive),
                    colors: colors
                )
                MediumRows(rows: slots.rows(maxRows: maxMediumRows), colors: colors)
                Spacer(minLength: 0)
            }
        } else if let slot = slots.first {
            VStack(alignment: .leading, spacing: gapBase) {
                HeaderRow(
                    label: slot.isLive ? FavoritesWidgetStrings.liveSmallLabel : FavoritesWidgetStrings.nextLabel,
                    live: slot.isLive,
                    colors: colors
                )
                if slot.isLive {
                    SmallLiveBody(slot: slot, colors: colors)
                } else {
                    SmallNextBody(slot: slot, colors: colors)
                }
                Spacer(minLength: 0)
            }
        }
    }
}

private struct SmallNextBody: View {
    let slot: FavoritesWidgetSlot
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        VStack(alignment: .leading, spacing: gapTight) {
            Text(slot.startsAt)
                .font(.system(size: 14, weight: .bold, design: .monospaced))
                .foregroundStyle(Color(argbHex: colors.primary))
            Text(slot.sessions[0].title)
                .font(.sans)
                .lineLimit(2)
                .foregroundStyle(Color(argbHex: colors.onSurface))
            SmallChipRow(slot: slot, ink: Color(argbHex: colors.onSurfaceVariant))
                .padding(.top, gapTight)
        }
    }
}

private struct SmallLiveBody: View {
    let slot: FavoritesWidgetSlot
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        VStack(alignment: .leading, spacing: gapTight) {
            Text("\(slot.startsAt) – \(slot.endsAt)")
                .font(.monoBold)
                .foregroundStyle(Color(argbHex: colors.onPrimary))
            Text(slot.sessions[0].title)
                .font(.sans)
                .lineLimit(2)
                .foregroundStyle(Color(argbHex: colors.onPrimary))
            SmallChipRow(slot: slot, ink: Color(argbHex: colors.onPrimary))
                .padding(.top, gapTight)
        }
        .padding(insetRow)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(argbHex: colors.primary), in: RoundedRectangle(cornerRadius: gapBase))
    }
}

private struct SmallChipRow: View {
    let slot: FavoritesWidgetSlot
    let ink: Color

    var body: some View {
        HStack(spacing: gapBase) {
            RoomChip(session: slot.sessions[0])
            if slot.sessions.count > 1 {
                Text(FavoritesWidgetStrings.moreCountSmall(slot.sessions.count - 1))
                    .font(.sans)
                    .foregroundStyle(ink)
            }
        }
    }
}

private struct MediumRows: View {
    let rows: [FavoritesWidgetRow]
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        VStack(spacing: gapBase) {
            ForEach(rows.bandGroups()) { group in
                if group.isLive {
                    VStack(spacing: gapBase) {
                        ForEach(group.rows) { ScheduleRow(row: $0, colors: colors) }
                    }
                    // The band runs 3pt beyond the row slot on top and bottom, per the band geometry.
                    .padding(.vertical, 3)
                    .background(Color(argbHex: colors.primary), in: RoundedRectangle(cornerRadius: gapBase))
                } else {
                    ForEach(group.rows) { ScheduleRow(row: $0, colors: colors) }
                }
            }
        }
    }
}

private struct ScheduleRow: View {
    let row: FavoritesWidgetRow
    let colors: FavoritesWidgetSnapshot.Colors

    var body: some View {
        switch row {
        case let .session(session, showsTime, isLive):
            let ink = Color(argbHex: isLive ? colors.onPrimary : colors.onSurface)
            let content = HStack(spacing: 0) {
                Text(showsTime ? session.startsAt : "")
                    .font(.monoBold)
                    .foregroundStyle(ink)
                    .frame(width: timeCellWidth, alignment: .leading)
                    .padding(.trailing, gapBase)
                Text(session.title).font(.sans).lineLimit(1).foregroundStyle(ink)
                Spacer(minLength: gapWide)
                RoomChip(session: session)
            }
            .frame(height: rowHeight)
            .padding(.horizontal, insetRow)
            if isLive, let url = DeepLink.favoriteSession(session.id) {
                Link(destination: url) { content }
            } else {
                content
            }
        case let .more(count):
            HStack(spacing: 0) {
                Color.clear.frame(width: timeCellWidth + gapBase, height: 1)
                Text(FavoritesWidgetStrings.moreCountRow(count))
                    .font(.sans)
                    .foregroundStyle(Color(argbHex: colors.onSurfaceVariant))
                Spacer(minLength: 0)
            }
            .frame(height: rowHeight)
            .padding(.horizontal, insetRow)
        }
    }
}

private struct RowGroup: Identifiable {
    let isLive: Bool
    let rows: [FavoritesWidgetRow]

    var id: String { rows.first?.id ?? "empty" }
}

private extension Array where Element == FavoritesWidgetRow {
    /// Consecutive live rows of one slot share a single band; everything else stands alone.
    func bandGroups() -> [RowGroup] {
        var groups: [RowGroup] = []
        for row in self {
            let sharesBand: Bool
            if case let .session(_, showsTime, isLive) = row {
                sharesBand = isLive && !showsTime && groups.last?.isLive == true
            } else {
                sharesBand = false
            }
            if sharesBand, let previous = groups.popLast() {
                groups.append(RowGroup(isLive: true, rows: previous.rows + [row]))
            } else {
                groups.append(RowGroup(isLive: row.isLive, rows: [row]))
            }
        }
        return groups
    }
}

private extension Font {
    static let monoBold = Font.system(size: 12, weight: .bold, design: .monospaced)
    static let sans = Font.system(size: 12)
}
