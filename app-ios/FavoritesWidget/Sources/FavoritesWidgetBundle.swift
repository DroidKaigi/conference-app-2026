import SwiftUI
import WidgetKit

@main
struct FavoritesWidgetBundle: WidgetBundle {
    var body: some Widget {
        FavoritesWidget()
    }
}

struct FavoritesWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "io.github.droidkaigi.confsched.FavoritesWidget", provider: FavoritesWidgetProvider()) {
            FavoritesWidgetView(entry: $0)
        }
        .configurationDisplayName(FavoritesWidgetStrings.favoritesLabel)
        .description(FavoritesWidgetStrings.description)
        .supportedFamilies([.systemSmall, .systemMedium])
        // The frame and the padding follow the Android widget's own inset scale, which the system
        // content margins would add to.
        .contentMarginsDisabled()
    }
}
