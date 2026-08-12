#!/bin/bash
set -euo pipefail

repo_root=$(cd "$(dirname "$0")/../.." && pwd)
cd "$repo_root"

configuration=Debug
kotlin_target=iosSimulatorArm64
# Drift from the settings app-ios/project.yml gives the real build leaves this check compiling
# against something Xcode never compiles against.
deployment_target=16.0
swift_version=6

# Kotlin's Gradle plugin registers the Swift Export tasks only when it finds Xcode's environment, so
# a run outside Xcode has to supply it: without all of these, the task below does not exist. Nothing
# writes to TARGET_BUILD_DIR here — the tasks that populate it are the ones this check leaves out.
export CONFIGURATION="$configuration"
export ARCHS=arm64
export SDK_NAME=iphonesimulator
export TARGET_BUILD_DIR="$repo_root/app-ios-kotlin/build/SwiftExportCheck"
export FRAMEWORKS_FOLDER_PATH=Frameworks
export DEPLOYMENT_TARGET_SETTING_NAME=IPHONEOS_DEPLOYMENT_TARGET
export IPHONEOS_DEPLOYMENT_TARGET="$deployment_target"

echo "==> Running Swift Export and compiling the generated Swift package"
./gradlew ":app-ios-kotlin:${kotlin_target}${configuration}BuildSPMPackage"

echo "==> Type-checking the app's Swift sources against the exported module"
export_build="app-ios-kotlin/build/SPMBuild/$kotlin_target/$configuration"
export_package="app-ios-kotlin/build/SPMPackage/$kotlin_target/$configuration"

# What Xcode reaches through SWIFT_INCLUDE_PATHS: the .swiftmodule interfaces of the exported module
# and the module maps of the bridges it is built on.
include_paths=(-I "$export_build/dd-interfaces")
for bridge in "$export_package"/OtherIncludes/*/; do
    if [ -d "$bridge" ]; then
        include_paths+=(-I "$bridge")
    fi
done

xcrun --sdk iphonesimulator swiftc -typecheck \
    -target "arm64-apple-ios${deployment_target}-simulator" \
    -swift-version "$swift_version" \
    "${include_paths[@]}" \
    app-ios/Sources/*.swift
