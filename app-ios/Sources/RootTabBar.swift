import AppShared
import ConferenceApp2026AppShared
import SwiftUI
import UIKit

/// Swift Export flattens only the package the exported module names, so the types it reaches in
/// :app-shared keep their fully qualified Kotlin package.
typealias RootTab = ExportedKotlinPackages.io.github.droidkaigi.confsched.app.RootTab
typealias RootTabBarPalette = ExportedKotlinPackages.io.github.droidkaigi.confsched.app.RootTabBarPalette

/// The root container: the Compose view controller fills the window, and the tab bar sits over its
/// bottom edge. UIKit sizes the bar from the safe area it finds itself in, so the bar's material
/// reaches the window's bottom edge while its items stay clear of the home indicator.
final class RootViewController: UIViewController {
    private let host: KaigiAppHost
    private let tabBar = UITabBar()
    private lazy var tabBarHeight = tabBar.heightAnchor.constraint(equalToConstant: 0)

    private var selection: RootTab?
    private var palette: RootTabBarPalette?
    private var observations: [Task<Void, Never>] = []

    init(host: KaigiAppHost) {
        self.host = host
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    deinit {
        observations.forEach { $0.cancel() }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        addComposeViewController()
        addTabBar()
        observations = [
            Task { try? await self.collectCurrentTab() },
            Task { try? await self.collectPalette() },
        ]
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        // A bar's intrinsic height leaves out the safe area it stands in, and only `sizeThatFits`
        // adds it — the measurement a UITabBarController lays its own bar out with.
        let fittingHeight = tabBar.sizeThatFits(
            CGSize(width: view.bounds.width, height: .greatestFiniteMagnitude)
        ).height
        if tabBarHeight.constant != fittingHeight {
            tabBarHeight.constant = fittingHeight
        }
    }

    private func addComposeViewController() {
        let compose = host.viewController()
        addChild(compose)
        compose.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(compose.view)
        NSLayoutConstraint.activate([
            compose.view.topAnchor.constraint(equalTo: view.topAnchor),
            compose.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            compose.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            compose.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        compose.didMove(toParent: self)
    }

    private func addTabBar() {
        tabBar.delegate = self
        tabBar.items = RootTab.allCases.enumerated().map { index, tab in
            UITabBarItem(title: tab.label, image: UIImage(systemName: tab.symbolName), tag: index)
        }
        tabBar.isHidden = true
        tabBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tabBar)
        NSLayoutConstraint.activate([
            tabBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tabBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tabBar.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            tabBarHeight,
        ])
    }

    private func collectCurrentTab() async throws {
        for try await selection in host.currentTab.asAsyncSequence() {
            self.selection = selection?.tab
            applyTabBarState()
        }
    }

    private func collectPalette() async throws {
        for try await palette in host.tabBarPalette.asAsyncSequence() {
            self.palette = palette
            applyTabBarState()
        }
    }

    private func applyTabBarState() {
        // The selection arrives before the palette: the navigator holds a tab from the start, while
        // the palette waits for the first composition. Showing the bar in between would put the
        // system tint on it for a frame, so it waits for both.
        tabBar.isHidden = selection == nil || palette == nil
        // A hidden bar keeps the selection it had, so returning from a detail screen does not
        // replay the selection animation.
        if let index = selection.flatMap({ RootTab.allCases.firstIndex(of: $0) }) {
            tabBar.selectedItem = tabBar.items?[index]
        }
        if let palette {
            tabBar.tintColor = UIColor(argb: palette.accentArgb)
            // The theme is the app's own, not the system's, so the bar's material follows it
            // rather than the device's appearance setting.
            tabBar.overrideUserInterfaceStyle = palette.isDark ? .dark : .light
        }
    }
}

extension RootViewController: UITabBarDelegate {
    func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        guard RootTab.allCases.indices.contains(item.tag) else { return }
        host.selectTab(tab: RootTab.allCases[item.tag])
    }
}

private extension UIColor {
    convenience init(argb: Int64) {
        func channel(_ shift: Int64) -> CGFloat {
            CGFloat((argb >> shift) & 0xFF) / 255
        }
        self.init(red: channel(16), green: channel(8), blue: channel(0), alpha: channel(24))
    }
}

private extension RootTab {
    /// The Compose bar names each destination with a Material `ImageVector`, which does not cross
    /// the framework boundary; the SF Symbol standing for the same destination lives here.
    var symbolName: String {
        switch self {
        case .Timetable: return "calendar"
        case .EventMap: return "map"
        case .Favorites: return "heart.fill"
        case .About: return "info.circle"
        case .ProfileCard: return "person.crop.circle"
        default: return "questionmark"
        }
    }
}
