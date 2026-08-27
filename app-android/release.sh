#!/bin/bash
# usage: ./release.sh /path/to/secret/files
set -eu
cd "$(dirname "$0")"
cp "$1/keystore.properties" .
cp "$1/droidkaigi2026.keystore" .
mkdir -p src/prod
cp "$1/google-services.json" src/prod/google-services.json

cd ..
./gradlew :app-android:assembleProdRelease
./gradlew :app-android:bundleProdRelease
