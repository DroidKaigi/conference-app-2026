#!/usr/bin/env python3
"""Writes the AboutLibraries export describing the Swift packages Xcode links into the app.

Swift packages are resolved by Xcode and appear in no Gradle configuration, so the shared licenses
screen cannot discover them the way it discovers the Kotlin dependencies. They are described here
from what the iOS build itself resolved: `Package.resolved` for the set and the versions, and the
checked-out sources for the license text. The app hands the result to `KaigiAppHost`, which merges
it with the export `app-ios-kotlin` generates.

Run from an Xcode build phase; it reads SRCROOT, BUILD_DIR, BUILT_PRODUCTS_DIR and
UNLOCALIZED_RESOURCES_FOLDER_PATH from the environment.
"""

import json
import os
import pathlib
import sys

# A licence file does not state its SPDX identifier, so each package names its own. A package
# missing from this map fails the build rather than reaching the screen unattributed.
SPDX_BY_IDENTITY = {
    "abseil-cpp-binary": "Apache-2.0",
    "app-check": "Apache-2.0",
    "firebase-ios-sdk": "Apache-2.0",
    "google-ads-on-device-conversion-ios-sdk": "Apache-2.0",
    "googleappmeasurement": "Apache-2.0",
    "googledatatransport": "Apache-2.0",
    "googleutilities": "Apache-2.0",
    "grpc-binary": "Apache-2.0",
    "gtm-session-fetcher": "Apache-2.0",
    "interop-ios-for-google-sdks": "Apache-2.0",
    "leveldb": "BSD-3-Clause",
    "nanopb": "Zlib",
    "promises": "Apache-2.0",
}

LICENSE_NAMES = {
    "Apache-2.0": "Apache License 2.0",
    "BSD-3-Clause": "BSD 3-Clause License",
    "Zlib": "zlib License",
}

LICENSE_URLS = {
    "Apache-2.0": "https://spdx.org/licenses/Apache-2.0.html",
    "BSD-3-Clause": "https://spdx.org/licenses/BSD-3-Clause.html",
    "Zlib": "https://spdx.org/licenses/Zlib.html",
}


def fail(message):
    print(f"error: {message}", file=sys.stderr)
    sys.exit(1)


def require_env(name):
    value = os.environ.get(name)
    if not value:
        fail(f"{name} is not set; this script runs from an Xcode build phase")
    return value


def find_checkouts(build_dir):
    # The archive action redirects BUILD_DIR into ArchiveIntermediates, so the depth of the derived
    # data root above it varies between a build and an archive; walk up rather than assume one.
    # SWIFT_PACKAGE_CHECKOUTS overrides the search for a build whose products sit outside that root.
    override = os.environ.get("SWIFT_PACKAGE_CHECKOUTS")
    if override:
        candidate = pathlib.Path(override)
        if not candidate.is_dir():
            fail(f"SWIFT_PACKAGE_CHECKOUTS names {candidate}, which is not a directory")
        return candidate

    tried = []
    for ancestor in pathlib.Path(build_dir).resolve().parents:
        candidate = ancestor / "SourcePackages" / "checkouts"
        tried.append(candidate)
        if candidate.is_dir():
            return candidate
    fail("no Swift package checkouts above BUILD_DIR; tried " + ", ".join(str(p) for p in tried))


def read_license_text(checkout):
    for entry in sorted(checkout.iterdir()):
        if entry.is_file() and entry.name.lower().startswith(("license", "licence")):
            return entry.read_text(encoding="utf-8", errors="replace")
    fail(f"no licence file in {checkout}")


def main():
    srcroot = pathlib.Path(require_env("SRCROOT"))
    checkouts = find_checkouts(require_env("BUILD_DIR"))
    output = (
        pathlib.Path(require_env("BUILT_PRODUCTS_DIR"))
        / require_env("UNLOCALIZED_RESOURCES_FOLDER_PATH")
        / "swift-package-licenses.json"
    )

    resolved_path = srcroot / "KaigiApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved"
    if not resolved_path.is_file():
        fail(f"no resolved package list at {resolved_path}")
    pins = json.loads(resolved_path.read_text())["pins"]

    # The checkout directory carries the package's own casing, which Package.resolved lowercases.
    checkout_by_identity = {entry.name.lower(): entry for entry in checkouts.iterdir() if entry.is_dir()}

    libraries = []
    licenses = {}
    for pin in sorted(pins, key=lambda pin: pin["identity"]):
        identity = pin["identity"]
        spdx_id = SPDX_BY_IDENTITY.get(identity)
        if spdx_id is None:
            fail(f"Swift package '{identity}' has no entry in SPDX_BY_IDENTITY in {__file__}")

        checkout = checkout_by_identity.get(identity)
        if checkout is None:
            fail(f"Swift package '{identity}' is not checked out under {checkouts}")

        # Kept apart from the hashes the Gradle export uses, so an entry carrying the licence text
        # never silently replaces one that does not.
        license_hash = f"{spdx_id}-swiftpm"
        licenses.setdefault(
            license_hash,
            {
                "name": LICENSE_NAMES[spdx_id],
                "url": LICENSE_URLS[spdx_id],
                "spdxId": spdx_id,
                "hash": license_hash,
                "content": read_license_text(checkout),
            },
        )

        location = pin["location"].removesuffix(".git")
        libraries.append(
            {
                "uniqueId": f"swiftpm:{identity}",
                "artifactVersion": pin["state"].get("version", pin["state"].get("revision", "")),
                "name": checkout.name,
                "website": location,
                "developers": [],
                "scm": {"url": location},
                "licenses": [license_hash],
                "funding": [],
            }
        )

    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps({"libraries": libraries, "licenses": licenses}))
    print(f"note: wrote {len(libraries)} Swift package licences to {output}")


if __name__ == "__main__":
    main()
