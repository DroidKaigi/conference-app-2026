# Preview image enum generation

Drop an image in, a build task turns it into a type-safe enum entry, and the UI resolves it by URL through a CompositionLocal. Three steps.

## 1. Place the image

Add the file under `:core:preview:impl`'s Compose Resources:

```text
core/preview/impl/src/commonMain/composeResources/drawable/
    avatar_sample.png
    session_cover.png
    speaker_avatar_a.png
```

## 2. A Gradle task generates the enum (hooked into compile)

A Gradle task reads those resource names and, wired to run before `compileKotlin` (its output directory is added to `:core:preview:api`'s `commonMain` source set), emits `PreviewImage.kt` with one entry per image — a plain build task, not KSP.

Each entry also carries a stable `imageUrl` under a dedicated `preview://` scheme (keyed off the resource name) so it can stand in for a real network URL without ever colliding with one — production UI loads images by URL, so previews must too.

```kotlin
// build/generated/.../preview/PreviewImage.kt   (lands in core:preview:api)
enum class PreviewImage(val imageUrl: String) {
    AvatarSample("preview://avatar_sample"),
    SessionCover("preview://session_cover"),
    SpeakerAvatarA("preview://speaker_avatar_a"),
}
```

So adding or removing a drawable changes the enum on the next build; a reference to a deleted image stops compiling.

## 3. Resolve it by URL in the UI (CompositionLocal)

Production loads network images with a common `RemoteImage(imageUrl: String)` composable. Sample data feeds it a preview URL instead of a real one:

```kotlin
Speaker(avatarUrl = PreviewImage.SpeakerAvatarA.imageUrl)   // "preview://speaker_avatar_a"
```

`RemoteImage` consults `LocalPreviewImageResolver` first; in a preview / test build it resolves a known preview URL to a local resource, otherwise (production) it loads from the network:

```kotlin
@Composable
fun RemoteImage(imageUrl: String, contentDescription: String?) {
    val resource = LocalPreviewImageResolver.current?.resolve(imageUrl)
    if (resource != null) Image(painter = painterResource(resource), contentDescription = contentDescription)
    else AsyncImage(model = imageUrl, contentDescription = contentDescription) // network in production
}
```

The resolver matches the preview URL back to the enum, then to its Compose Resource; it returns null for anything else, so a release build falls through to the network. Which compositions get a resolver at all is [Preview & sample assets](./preview.md).

```kotlin
// core:preview:api — contract + injection point (null in production)
fun interface PreviewImageResolver {
    fun resolve(imageUrl: String): DrawableResource?
}
val LocalPreviewImageResolver = staticCompositionLocalOf<PreviewImageResolver?> { null }
```

```kotlin
// core:preview:impl — where the binaries live
class DefaultPreviewImageResolver : PreviewImageResolver {
    override fun resolve(imageUrl: String): DrawableResource? {
        val image = PreviewImage.entries.firstOrNull { it.imageUrl == imageUrl } ?: return null
        return when (image) {
            PreviewImage.AvatarSample -> Res.drawable.avatar_sample
            PreviewImage.SessionCover -> Res.drawable.session_cover
            PreviewImage.SpeakerAvatarA -> Res.drawable.speaker_avatar_a
        }
    }
}
```

Related: [Preview & sample assets](./preview.md) · [Preview screenshot tests](./testing-preview-screenshot.md)