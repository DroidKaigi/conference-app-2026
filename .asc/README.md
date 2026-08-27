# Local TestFlight release

Runs the KaigiApp archive → export → TestFlight publish pipeline on your machine
via the [App Store Connect CLI](https://github.com/rorkai/App-Store-Connect-CLI)
(`asc`). No CI/CD involved; signing uses the local Keychain.

## Install

```bash
brew install asc
brew install --cask 1password-cli   # if `op` is not already installed
```

Sign in to 1Password (`op signin`) and confirm a `AppStoreConnect` item exists
in the `Private` vault with `key_id`, `issuer_id`, and `private_key` fields
(the App Store Connect API key values).

## One-time setup

Fill in the placeholders in `.asc/workflow.json`:

- `APP_ID`: the app's numeric App Store Connect ID
- `TESTFLIGHT_GROUP`: the target beta group name or ID

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

The `inject_metadata` step rewrites the version and build number directly into
the git-tracked `app-ios/KaigiApp-Info.plist`. Revert it after the run so the
bump doesn't get committed:

```bash
git checkout app-ios/KaigiApp-Info.plist
```
