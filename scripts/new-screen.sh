#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
feature=""
screen=""
create_module=false
while [ $# -gt 0 ]; do
  case "$1" in
    --feature) feature="$2"; shift 2 ;;
    --screen) screen="$2"; shift 2 ;;
    --create-module) create_module=true; shift ;;
    *) echo "Unknown argument: $1" >&2; exit 1 ;;
  esac
done

if ! [[ "$feature" =~ ^[a-z][a-z0-9]*$ ]] || ! [[ "$screen" =~ ^[A-Z][A-Za-z0-9]*$ ]]; then
  echo "Usage: scripts/new-screen.sh --feature <lowercase module> --screen <PascalCaseName> [--create-module]" >&2
  exit 1
fi

lower="$(printf '%s' "${screen:0:1}" | tr '[:upper:]' '[:lower:]')${screen:1}"
base_pkg="io.github.droidkaigi.confsched"
feature_pkg="$base_pkg.feature.$feature"
feature_dir="$root/feature/$feature"
feature_src="$feature_dir/src/commonMain/kotlin/${feature_pkg//.//}"
model_src="$root/core/model/src/commonMain/kotlin/${base_pkg//.//}/core/model"
app_src="$root/app-shared/src/commonMain/kotlin/${base_pkg//.//}/app"

created=()
skipped=()
put() {
  local path="$1"
  if [ -e "$path" ]; then
    skipped+=("$path")
    cat > /dev/null
    return
  fi
  mkdir -p "$(dirname "$path")"
  cat > "$path"
  created+=("$path")
}

if [ ! -d "$feature_dir" ] && [ "$create_module" != true ]; then
  echo "feature/$feature does not exist. Re-run with --create-module to scaffold the module" >&2
  echo "(build.gradle.kts + settings.gradle.kts include + app-shared dependency)." >&2
  exit 1
fi
if [ ! -d "$feature_dir" ]; then
  put "$feature_dir/build.gradle.kts" << EOF
plugins {
    alias(libs.plugins.droidkaigiConventionKmpFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:model"))
            implementation(project(":core:ui"))
        }
    }
}
EOF
  if ! grep -q "include(\":feature:$feature\")" "$root/settings.gradle.kts"; then
    sed -i '' "s|include(\":app-shared\")|include(\":feature:$feature\")\\
include(\":app-shared\")|" "$root/settings.gradle.kts"
    created+=("$root/settings.gradle.kts (include :feature:$feature)")
  fi
  if ! grep -q "\":feature:$feature\"" "$root/app-shared/build.gradle.kts"; then
    sed -i '' "s|            api(project(\":feature:about\"))|            api(project(\":feature:about\"))\\
            api(project(\":feature:$feature\"))|" "$root/app-shared/build.gradle.kts"
    created+=("$root/app-shared/build.gradle.kts (api :feature:$feature)")
  fi
fi

put "$model_src/${screen}ScreenScope.kt" << EOF
package $base_pkg.core.model

sealed interface ${screen}ScreenScope
EOF

put "$feature_src/${screen}ScreenContext.kt" << EOF
package $feature_pkg

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import $base_pkg.core.common.KaigiLogger
import $base_pkg.core.common.PresenterContext
import $base_pkg.core.common.ScreenContext
import $base_pkg.core.model.${screen}ScreenScope

@Inject
class ${screen}PresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(${screen}ScreenScope::class)
class ${screen}ScreenContext(
    override val logger: KaigiLogger,
    val presenterContext: ${screen}PresenterContext,
) : ScreenContext
EOF

put "$feature_src/${screen}ScreenGraph.kt" << EOF
package $feature_pkg

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import $base_pkg.core.common.UiScope
import $base_pkg.core.model.${screen}ScreenScope

@GraphExtension(${screen}ScreenScope::class)
interface ${screen}ScreenGraph {
    val screenContext: ${screen}ScreenContext

    val screenNavigator: ${screen}ScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun create${screen}ScreenGraph(): ${screen}ScreenGraph
    }
}
EOF

put "$feature_src/${screen}ScreenAction.kt" << EOF
package $feature_pkg

sealed interface ${screen}ScreenAction {
    data object Reload : ${screen}ScreenAction
}
EOF

put "$feature_src/${screen}ScreenActionResult.kt" << EOF
package $feature_pkg

sealed interface ${screen}ScreenActionResult {
    data object Reloaded : ${screen}ScreenActionResult
}
EOF

put "$feature_src/${screen}ScreenUiState.kt" << EOF
package $feature_pkg

data class ${screen}ScreenUiState(
    val title: String,
    val reloadCount: Int,
)
EOF

put "$feature_src/${screen}ScreenPresenter.kt" << EOF
package $feature_pkg

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import $base_pkg.core.common.ActionEffect
import $base_pkg.core.common.ScreenChannel

@Composable
context(_: ${screen}PresenterContext)
fun ${lower}ScreenPresenter(
    screenChannel: ScreenChannel<${screen}ScreenAction, ${screen}ScreenActionResult>,
): ${screen}ScreenUiState {
    var reloadCount by retain { mutableStateOf(0) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            ${screen}ScreenAction.Reload -> {
                reloadCount++
                screenChannel.emit(${screen}ScreenActionResult.Reloaded)
            }
        }
    }

    return ${screen}ScreenUiState(
        title = "${screen}",
        reloadCount = reloadCount,
    )
}
EOF

put "$feature_src/${screen}Screen.kt" << EOF
package $feature_pkg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import $base_pkg.core.model.KaigiColorScheme
import $base_pkg.core.preview.KaigiSchemeProvider
import $base_pkg.core.preview.wrapper.KaigiPreviewTheme

@Composable
fun ${screen}Screen(
    uiState: ${screen}ScreenUiState,
    onReloadClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(uiState.title)
        Text("Reloaded \${uiState.reloadCount} times")
        Button(onClick = onReloadClick) { Text("Reload") }
        Button(onClick = onBackClick) { Text("Back") }
    }
}

@Preview
@Composable
private fun ${screen}ScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ${screen}Screen(
            uiState = ${screen}ScreenUiState(title = "${screen}", reloadCount = 0),
            onReloadClick = {},
            onBackClick = {},
        )
    }
}
EOF

put "$feature_src/${screen}ScreenRoot.kt" << EOF
package $feature_pkg

import androidx.compose.runtime.Composable
import $base_pkg.core.common.ActionResultEffect
import $base_pkg.core.common.LocalSnackbarHostState
import $base_pkg.core.common.context
import $base_pkg.core.common.retainScreenChannel

@Composable
context(screenContext: ${screen}ScreenContext)
fun ${screen}ScreenRoot(
    onNavigateBack: () -> Unit,
) {
    val screenChannel = retainScreenChannel<${screen}ScreenAction, ${screen}ScreenActionResult>()
    val snackbarHostState = LocalSnackbarHostState.current

    ActionResultEffect(screenChannel) { result ->
        when (result) {
            ${screen}ScreenActionResult.Reloaded -> snackbarHostState.showSnackbar("Reloaded")
        }
    }

    val uiState = context(screenContext.presenterContext) {
        ${lower}ScreenPresenter(screenChannel)
    }
    ${screen}Screen(
        uiState = uiState,
        onReloadClick = { screenChannel.send(${screen}ScreenAction.Reload) },
        onBackClick = onNavigateBack,
    )
}
EOF

put "$feature_src/${screen}NavKey.kt" << EOF
package $feature_pkg

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ${screen}NavKey : NavKey
EOF

put "$feature_src/${screen}NavEntryProvider.kt" << EOF
package $feature_pkg

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import $base_pkg.core.common.AppNavigator
import $base_pkg.core.common.NavEntryProvider
import $base_pkg.core.common.UiScope
import $base_pkg.core.common.context

@ContributesIntoSet(UiScope::class)
@Inject
class ${screen}NavEntryProvider(
    private val screenGraphFactory: ${screen}ScreenGraph.Factory,
    private val appNavigator: AppNavigator,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<${screen}NavKey> {
            val graph = retain(screenGraphFactory::create${screen}ScreenGraph)
            context(graph.screenContext) {
                ${screen}ScreenRoot(
                    onNavigateBack = appNavigator::back,
                )
            }
        }
    }
}
EOF

put "$feature_src/${screen}ScreenNavigator.kt" << EOF
package $feature_pkg

import $base_pkg.core.common.Navigator

interface ${screen}ScreenNavigator : Navigator
EOF

put "$app_src/Default${screen}ScreenNavigator.kt" << EOF
package $base_pkg.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import $base_pkg.core.common.AppNavigator
import $base_pkg.core.model.${screen}ScreenScope
import $feature_pkg.${screen}ScreenNavigator

@Inject
@SingleIn(${screen}ScreenScope::class)
@ContributesBinding(${screen}ScreenScope::class)
class Default${screen}ScreenNavigator(
    @Suppress("unused") private val appNavigator: AppNavigator,
) : ${screen}ScreenNavigator
EOF

echo "Created:"
for f in "${created[@]}"; do echo "  ${f#"$root"/}"; done
if [ "${#skipped[@]}" -gt 0 ]; then
  echo "Skipped (already exist):"
  for f in "${skipped[@]}"; do echo "  ${f#"$root"/}"; done
fi
cat << EOF

Next steps:
  1. Navigate to the screen: appNavigator.goTo(${screen}NavKey) (or add it to the root tabs).
  2. Fill in the action / action result / UiState / presenter / screen; add Soil keys + SoilDataBoundary when the screen reads data.
  3. Add outgoing navigations to ${screen}ScreenNavigator and map them in Default${screen}ScreenNavigator.
  4. Build: ./gradlew :app-desktop:compileKotlinJvm
EOF
