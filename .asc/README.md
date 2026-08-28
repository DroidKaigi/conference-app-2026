# Local TestFlight release

Runs the KaigiApp archive → export → TestFlight publish pipeline on your machine
via the [App Store Connect CLI](https://github.com/rorkai/App-Store-Connect-CLI)
(`asc`). No CI/CD involved; signing uses the local Keychain.

## Install

```bash
brew install asc
brew install --cask 1password-cli   # if `op` is not already installed
```

Turn on the command-line integration in the 1Password desktop app (Settings →
Developer → Integrate with 1Password CLI); `op run` reports that no accounts are
configured until it is on. Then confirm an `AppStoreConnect` item exists in the
`Private` vault with `key_id`, `issuer_id`, and `private_key` fields (the App Store
Connect API key values).

## Run

```bash
op run --env-file=.asc/secrets.env.tpl -- asc workflow run --dry-run testflight VERSION:1.2.3
op run --env-file=.asc/secrets.env.tpl -- asc workflow run testflight VERSION:1.2.3
```

`op run` resolves the `op://` references in `secrets.env.tpl` and injects them
as `ASC_KEY_ID` / `ASC_ISSUER_ID` / `ASC_PRIVATE_KEY` for the duration of the
command only.

To resume a failed run:

```bash
op run --env-file=.asc/secrets.env.tpl -- asc workflow run testflight --resume RUN_ID
```

## Build location

The archive action redirects `BUILD_DIR` into `ArchiveIntermediates`, which leaves
`app-ios/scripts/generate-swift-package-licenses.py` unable to reach the Swift package
checkouts by walking up from it. The archive step therefore pins its derived data to
`.asc/artifacts/DerivedData` and passes `SWIFT_PACKAGE_CHECKOUTS`, naming the directory
outright. `SYMROOT` and `OBJROOT` must not be overridden here: the archive action assigns
them itself, and a command-line value makes it lose track of its own build products.

## Version and build numbers

The build number comes from `asc builds next-build-number`, and the archive step
passes both values to `xcodebuild` as `MARKETING_VERSION` and
`CURRENT_PROJECT_VERSION`. The app and the widget declare
`CFBundleShortVersionString` / `CFBundleVersion` as references to those two
settings in `app-ios/project.yml`, so a release stamps every bundle with the
same pair and leaves the git-tracked files untouched.
