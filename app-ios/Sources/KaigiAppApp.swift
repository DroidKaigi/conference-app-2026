import AppShared
import SwiftUI
import WidgetKit

@main
struct KaigiAppApp: App {
    private let host = KaigiAppHost(
        swiftPackageLicensesJson: swiftPackageLicensesJson(),
        favoritesWidgetAppGroup: FavoritesWidgetContract.appGroup,
        favoritesWidgetSnapshotSchemaVersion: Int32(FavoritesWidgetContract.snapshotSchemaVersion)
    )

    init() {
        host.initialize()
    }

    var body: some Scene {
        WindowGroup {
            RootView(host: host)
                .ignoresSafeArea()
                .onOpenURL { host.submitDeepLink(url: $0.absoluteString) }
                .task { try? await reloadWidgetOnSnapshotChange() }
        }
    }

    /// Collecting the flow is what writes the snapshot the widget extension reads, so the reload
    /// always follows a file the extension can already see.
    private func reloadWidgetOnSnapshotChange() async throws {
        for try await _ in host.favoritesWidgetSnapshots.asAsyncSequence() {
            WidgetCenter.shared.reloadAllTimelines()
        }
    }
}

/// The export written into the bundle by `scripts/generate-swift-package-licenses.py`. An empty
/// string leaves the licenses screen with the Kotlin dependencies alone, which is what a build
/// missing that phase would show.
private func swiftPackageLicensesJson() -> String {
    guard let url = Bundle.main.url(forResource: "swift-package-licenses", withExtension: "json"),
          let json = try? String(contentsOf: url, encoding: .utf8)
    else {
        return ""
    }
    return json
}

private struct RootView: UIViewControllerRepresentable {
    let host: KaigiAppHost

    func makeUIViewController(context: Context) -> UIViewController {
        RootViewController(host: host)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
