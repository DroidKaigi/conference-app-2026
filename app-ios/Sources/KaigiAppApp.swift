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
            RootView(host: host)
                .ignoresSafeArea()
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
