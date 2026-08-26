import AppShared
import SwiftUI

@main
struct KaigiAppApp: App {
    private let host = KaigiAppHost(
        swiftPackageLicensesJson: swiftPackageLicensesJson()
    )

    init() {
        host.initialize()
    }

    var body: some Scene {
        WindowGroup {
            ZStack(alignment: .bottom) {
                KaigiAppView(host: host)
                    .osCondition { view in
                        if #available(iOS 26.0, *) {
                            view
                        } else {
                            view.ignoresSafeArea(.all, edges: [.top, .leading, .trailing])
                        }
                    }
                RootTabBarView(
                    currentTab: host.currentTab.asAsyncSequence().map { $0?.tab },
                    palette: host.tabBarPalette.asAsyncSequence(),
                    select: host.selectTab(tab:)
                )
            }
            .osCondition { view in
                if #available(iOS 26.0, *) {
                    view.ignoresSafeArea()
                } else {
                    view
                }
            }
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

private struct KaigiAppView: UIViewControllerRepresentable {
    let host: KaigiAppHost

    func makeUIViewController(context: Context) -> UIViewController {
        host.viewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

private extension View {
    @ViewBuilder public func osCondition<Content: View>(
        @ViewBuilder modifier: (Self) -> Content
    ) -> some View {
        modifier(self)
    }
}
