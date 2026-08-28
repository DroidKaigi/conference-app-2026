#!/bin/bash
# usage: ./release.sh /path/to/secret/files
set -eu
cd "$(dirname "$0")"
app_dir="$PWD"

# Copied secrets leave the checkout even when a build step fails.
cleanup() {
  rm -f "$app_dir/keystore.properties" "$app_dir/droidkaigi2026.keystore" "$app_dir/src/prod/google-services.json"
}
trap cleanup EXIT

cp "$1/keystore.properties" "$app_dir"
cp "$1/droidkaigi2026.keystore" "$app_dir"
mkdir -p "$app_dir/src/prod"
cp "$1/google-services.json" "$app_dir/src/prod/google-services.json"

cd ..
./gradlew :app-android:assembleProdRelease
./gradlew :app-android:bundleProdRelease
