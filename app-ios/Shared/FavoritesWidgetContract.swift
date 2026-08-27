import Foundation

/// The App Group and the snapshot schema version, which `app-ios/project.yml` stamps into every
/// bundle that takes part in the exchange. Reading them back from the running bundle is what
/// keeps the app, the extension and the entitlements on one value.
enum FavoritesWidgetContract {
    static let appGroup = infoValue("FavoritesWidgetAppGroup", as: String.self)
    static let snapshotSchemaVersion = infoValue("FavoritesWidgetSnapshotSchemaVersion", as: Int.self)

    static let snapshotFileName = "favorites-widget-snapshot.json"

    private static func infoValue<T>(_ key: String, as type: T.Type) -> T {
        guard let value = Bundle(for: ContractBundleToken.self).object(forInfoDictionaryKey: key) as? T else {
            preconditionFailure("\(key) is missing from the bundle's Info.plist")
        }
        return value
    }
}

private final class ContractBundleToken {}
