import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'

// https://vitepress.dev/reference/site-config
export default withMermaid(defineConfig({
  // VitePress uses srcDir as the Vite root, so docs/ needs a node_modules link
  // back to this directory; scripts/link-docs-node-modules.mjs creates it on install.
  srcDir: '../docs',
  // Served from https://droidkaigi.github.io/conference-app-2026/docs/; the web app
  // (app-web, wasm) is planned to occupy the site root alongside this subpath.
  base: '/conference-app-2026/docs/',
  lang: 'en-US',
  title: 'conference-app-2026',
  description: 'Architecture and implementation guide for the DroidKaigi 2026 conference app',
  lastUpdated: true,
  cleanUrls: true,
  // session-log.md is an auto-generated chat log; exclude it from the site build.
  srcExclude: ['session-log.md'],
  themeConfig: {
    outline: { level: [2, 3], label: 'On this page' },
    search: { provider: 'local' },
    sidebar: [
      {
        text: 'Overview',
        items: [
          { text: 'Top / doc map', link: '/' },
        ],
      },
      {
        text: 'Project structure',
        collapsed: false,
        items: [
          { text: 'Module structure', link: '/project-structure' },
          { text: 'Platforms & modules', link: '/platforms-and-modules' },
        ],
      },
      {
        text: 'Architecture',
        collapsed: false,
        items: [
          { text: 'Architecture overview', link: '/architecture-overview' },
          { text: 'Error handling', link: '/error-handling' },
          { text: 'Presenter performance', link: '/presenter-performance' },
          { text: 'Enforcement', link: '/enforcement' },
          { text: 'Naming review', link: '/naming-review' },
          { text: 'CompositionLocal review', link: '/compositionlocal-review' },
        ],
      },
      {
        text: 'Single screen structure',
        collapsed: false,
        items: [
          { text: 'Building a screen', link: '/building-a-screen' },
          { text: 'ScreenContext design', link: '/screen-context' },
        ],
      },
      {
        text: 'Dependency injection',
        collapsed: false,
        items: [
          { text: 'AppGraph and UiGraph', link: '/di-app-graph' },
          { text: 'Per-screen graphs (@GraphExtension)', link: '/di-screen-graph' },
        ],
      },
      {
        text: 'Navigation',
        collapsed: false,
        items: [
          { text: 'Navigation overview', link: '/navigation' },
          { text: 'Navigator', link: '/navigation-navigator' },
          { text: 'NavEntry aggregation (NavEntryProvider)', link: '/navigation-entry-aggregation' },
          { text: 'NavKey serializer aggregation (NavKeySerializersProvider)', link: '/navigation-navkey-serializers' },
          { text: 'Entry retention (RetainNavEntryDecorator)', link: '/navigation-retain-entry-decorator' },
          { text: 'Root NavEntry emulation (RootSceneStrategy)', link: '/navigation-predictive-back-tabs' },
          { text: 'Root tab bar (RootTabSceneDecorator)', link: '/navigation-root-tab-bar' },
          { text: 'List-detail scenes (ListDetailSceneStrategy)', link: '/navigation-list-detail' },
        ],
      },
      {
        text: 'Soil (data layer)',
        collapsed: false,
        items: [
          { text: 'Soil keys', link: '/soil-keys' },
          { text: 'SoilDataBoundary', link: '/soil-data-boundary' },
          { text: 'Soil mutation', link: '/soil-mutation' },
          { text: 'Soil persistence', link: '/soil-persistence' },
        ],
      },
      {
        text: 'Build',
        collapsed: false,
        items: [
          { text: 'Version catalog', link: '/build-version-catalog' },
          { text: 'Convention plugins', link: '/build-convention-plugins' },
          { text: 'BuildKonfig (build-time values)', link: '/build-config-buildkonfig' },
          { text: 'Keeping dev-only code out of release', link: '/build-dev-only-exclusion' },
          { text: 'SwiftPM import cache across worktrees', link: '/build-worktree-swiftpm-cache' },
        ],
      },
      {
        text: 'iOS integration',
        collapsed: false,
        items: [
          { text: 'iOS overview', link: '/ios' },
          { text: 'Liquid Glass tab bar', link: '/ios-liquid-glass' },
          { text: 'iOS top bar', link: '/ios-top-bar' },
          { text: 'Swift ↔ Kotlin interop', link: '/ios-interop' },
          { text: 'CMP on iOS (embedding)', link: '/ios-cmp-embedding' },
        ],
      },
      {
        text: 'Testing',
        collapsed: false,
        items: [
          { text: 'Testing overview', link: '/testing' },
          { text: 'Test graph (TestingScope)', link: '/testing-graph' },
          { text: 'Presenter unit tests (Molecule)', link: '/testing-presenter' },
          { text: 'Preview screenshot tests', link: '/testing-preview-screenshot' },
          { text: 'Robot pattern tests', link: '/testing-robot' },
          { text: 'Enforcement checker tests', link: '/testing-enforcement' },
        ],
      },
      {
        text: 'Preview',
        collapsed: false,
        items: [
          { text: 'Preview & sample assets', link: '/preview' },
          { text: 'Preview image enum generation', link: '/preview-image-enum' },
          { text: 'Localization', link: '/localization' },
        ],
      },
      {
        text: 'Logging & debugging',
        collapsed: false,
        items: [
          { text: 'Logging (Kermit)', link: '/logging' },
          { text: 'Clock (KaigiClock)', link: '/clock' },
          { text: 'Debugging', link: '/debugging' },
        ],
      },
      {
        text: 'AI-assisted development',
        collapsed: false,
        items: [
          { text: 'AI-assisted development', link: '/ai-development' },
        ],
      },
    ],
  },
}))
